import javafx.animation.PauseTransition;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.stage.PopupWindow.AnchorLocation;
import javafx.util.Duration;
import javafx.util.Pair;
import my_weather.HourlyPeriod;
import my_weather.gridPoint.GridPoint;
import settings.Settings;
import settings.Settings.SettingsLoadException;
import my_weather.MyWeatherAPI;
import views.DayScene;
import views.LoadingScene;
import views.ThreeDayScene;
import views.TodayScene;
import views.components.events.LocationChangeEvent;
import views.components.events.NotificationEvent;
import views.components.sidebar.NavigationEvent;
import views.components.sidebar.Sidebar;
import views.util.NotificationBuilder;
import views.util.NotificationType;
import views.util.UnitHandler;
import weather_observations.Observations;
import weather_observations.WeatherObservations;

import java.util.ArrayList;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import javafx.stage.Popup;

/**
 * Main Application Class.
 * likely should not be referenced.
 */
public class JavaFX extends Application {
  // scenes to store
  TodayScene todayScene;
  ThreeDayScene threeDayScene;
  LoadingScene loadingScene;

  // global sidebar and the stage
  Sidebar sidebar;
  Stage primaryStage;

  // @MAIN
  public static void main(String[] args) {
    try {
      Settings.loadSettings();
    } catch (SettingsLoadException e) {
      return;
    }

    // set the temperature unit from settings
    UnitHandler.setUnit(Settings.getTempUnit());

    launch(args);
  }

  /**
   * runs on app startup
   */
  @Override
  public void start(Stage primaryStage) throws Exception {
    this.primaryStage = primaryStage;

    primaryStage.setTitle("WeatherFX");

    addEventHandlers();

    // data to load
    GridPoint gridPoint;
    ArrayList<HourlyPeriod> forecast;
    Observations observations;

    loadingScene = new LoadingScene();

    // load lat and longitude from settings
    double[] location = Settings.getLastLoc();
    double lat = location[0];
    double lon = location[1];

    // try to load the forecast and weather observations.
    // fails gracefully. by setting loading scene and sending notification
    try {
      gridPoint = my_weather.MyWeatherAPI.getGridPoint(lat, lon);
      forecast =
          my_weather.MyWeatherAPI.getHourlyForecast(gridPoint.region, gridPoint.gridX,
              gridPoint.gridY);
      observations = WeatherObservations.getWeatherObservations(gridPoint.region, gridPoint.gridX, gridPoint.gridY, lat, lon);
    } catch (Exception e) {
      primaryStage.setScene(loadingScene.getScene());
      primaryStage.show();

      PauseTransition delay = new PauseTransition(Duration.seconds(1));
      delay.setOnFinished(event -> {

        new NotificationBuilder()
            .withMessage(
                "Failed to connect to the internet, Connect to the internet and restart the application")
            .ofType(NotificationType.ConnectionError).showFor(25)
            .fire(loadingScene.getScene().getRoot());;
      });

      delay.play();

      return;
    }

    // if for some reason the forecast is still null, exit hard.
    if (forecast == null) {
      throw new RuntimeException("Forecast did not load");
    }

    // create weather scenes
    todayScene = new TodayScene(forecast, observations);
    threeDayScene = new ThreeDayScene(forecast);

    // create new sidebar based on scenes
    sidebar = Sidebar.fromScenes(
      new Pair<String, DayScene>("Daily Forecast", todayScene),
      new Pair<String, DayScene>("Three Day Forecast", threeDayScene)
    );

    sidebar.setTitle(gridPoint.location);
    todayScene.setActiveScene();

    primaryStage.setScene(todayScene.getScene());
    primaryStage.show();
  }

  /**
   * Add all event handlers
   */
  private void addEventHandlers() {
    primaryStage.addEventHandler(NavigationEvent.NAVIGATE, event -> {
      event.getTargetScene().setActiveScene();
      primaryStage.setScene(event.getTargetScene().getScene());
    });

    primaryStage.addEventHandler(LocationChangeEvent.LOCATIONCHANGE, event -> {
      changeLocation(event);
    });

    primaryStage.addEventHandler(NotificationEvent.NOTIFCATION, event -> {
      showPopup(primaryStage, event.component(), event.duration());
    });
  }

  /**
   * A error resistant change location function based on a
   * {@code LocationChangeEvent}
   */
  private void changeLocation(LocationChangeEvent event) {
    double lat = event.getLat();
    double lon = event.getLon();

    // start async call to get grid points
    CompletableFuture<GridPoint> pointFuture =
        MyWeatherAPI.getGridPointAsync(lat, lon);

    // store current scene then change to loading scene
    GridPoint point;
    Scene lastScene = primaryStage.getScene();
    primaryStage.setScene(loadingScene.getScene());

    try {
      // get the points. if something goes wrong, restore previous scene
      point = pointFuture.get();
      if (point == null) {
        primaryStage.setScene(lastScene);
        sidebar.recievedInvalidLocation();
        return;
      }
    } catch (ExecutionException e) {
      new NotificationBuilder()
          .withMessage("No Connection: Check your internet connection and try again")
          .ofType(NotificationType.ConnectionError).showFor(5).fire(lastScene.getRoot());

      primaryStage.setScene(lastScene);
      return;
    } catch (Exception e) {
      primaryStage.setScene(lastScene);
      sidebar.recievedInvalidLocation();

      return;
    }

    // start async calls for the forecast and weather observations
    CompletableFuture<ArrayList<HourlyPeriod>> periodFuture =
        MyWeatherAPI.getHourlyForecastAsync(point.region,
            point.gridX, point.gridY);
    CompletableFuture<Observations> observationFuture = WeatherObservations.getWeatherObservationsAsync(point.region, point.gridX, point.gridY, lat, lon);

    ArrayList<HourlyPeriod> periods;
    Observations observations;

    // load the forecast and observations. if either is null or error occurs, load last scene
    try {
      periods = periodFuture.get(3, TimeUnit.SECONDS);
      observations = observationFuture.get();
      if (periods == null || observations == null) {
        primaryStage.setScene(lastScene);
        sidebar.recievedInvalidLocation();
        return;
      }
      todayScene.update(periods, observations);
      threeDayScene.update(periods);

    } catch (TimeoutException timeout) {

      primaryStage.setScene(lastScene);
      new NotificationBuilder()
          .withMessage("Request timed out, check your internet connection and try again")
          .ofType(NotificationType.ConnectionError).fire(lastScene.getRoot());
      return;

    } catch (Exception e) {

      primaryStage.setScene(lastScene);
      new NotificationBuilder().withMessage("Error retrieving forecast data")
          .ofType(NotificationType.ConnectionError).fire(lastScene.getRoot());

      return;
    }

    // successful data load, set new location and scene
    sidebar.setTitle(point.location);
    primaryStage.setScene(lastScene);
    sidebar.recievedValidLocation();
    Settings.setLastLoc(lat, lon);
  }

  /**
   * show a popup to the current scene
   */
  private void showPopup(Stage s, VBox inner_element, int duration) {
    Popup p = new Popup();

    p.getContent().add(inner_element);
    inner_element.setMaxWidth(300);

    p.setAnchorX(s.getX() + 1100);
    p.setAnchorY(s.getY() + 40);
    p.setAnchorLocation(AnchorLocation.WINDOW_TOP_RIGHT);

    p.show(s);

    PauseTransition delay = new PauseTransition(Duration.seconds(duration));
    delay.setOnFinished(event -> p.hide());

    delay.play();
  }

  /**
   * stop the program.
   * overriden to allow for settings to be saved.
   */
  @Override
  public void stop() throws Exception {
    super.stop();

    // save state to settings
    Settings.saveSettings();
  }
}
