import java.util.ArrayList;
import java.util.Collections;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

import endpoints.my_weather.api.MyWeatherAPI;
import endpoints.my_weather.data.GridPoint;
import endpoints.my_weather.data.HourlyPeriod;
import endpoints.weather_observations.api.WeatherObservations;
import endpoints.weather_observations.data.Observations;
import javafx.animation.PauseTransition;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.layout.VBox;
import javafx.stage.Popup;
import javafx.stage.PopupWindow.AnchorLocation;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;
import javafx.util.Pair;
import settings.Settings;
import settings.Settings.SettingsLoadException;
import views.DayScene;
import views.LoadingScene;
import views.TenDayScene;
import views.ThreeDayScene;
import views.TodayScene;
import views.components.events.LocationChangeEvent;
import views.components.events.NotificationEvent;
import views.components.sidebar.NavigationEvent;
import views.components.sidebar.Sidebar;
import views.util.LocationChangeData;
import views.util.NotificationBuilder;
import views.util.NotificationType;
import views.util.UnitHandler;

/**
 * Main Application Class.
 * likely should not be referenced.
 */
public class JavaFX extends Application {
  // scenes to store
  TodayScene todayScene;
  ThreeDayScene threeDayScene;
  TenDayScene tenDayScene;
  LoadingScene loadingScene;

  // enumerated scenes
  ArrayList<DayScene> scenes = new ArrayList<>();
  int sceneNdx;

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
      gridPoint = MyWeatherAPI.getGridPoint(lat, lon);
      forecast = MyWeatherAPI.getHourlyForecast(gridPoint.region, gridPoint.gridX, gridPoint.gridY);
      observations = WeatherObservations.getWeatherObservations(gridPoint.region, gridPoint.gridX, gridPoint.gridY, lat,
          lon);
    } catch (Exception e) {
      primaryStage.setScene(loadingScene.getScene());
      primaryStage.show();

      PauseTransition delay = new PauseTransition(Duration.seconds(1));
      delay.setOnFinished(event -> {

        new NotificationBuilder()
            .withMessage(
                "Failed to connect to the internet, Connect to the internet and restart the application")
            .ofType(NotificationType.ConnectionError).showFor(25)
            .fire(loadingScene.getScene().getRoot());
        ;
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
    tenDayScene = new TenDayScene(forecast);

    // enumerate scenes
    sceneNdx = Settings.getLastScene();
    Collections.addAll(scenes, todayScene, threeDayScene, tenDayScene);

    // create new sidebar based on scenes
    sidebar = Sidebar.fromScenes(
        new Pair<String, DayScene>("Daily Forecast", todayScene),
        new Pair<String, DayScene>("Three Day Forecast", threeDayScene),
        new Pair<String, DayScene>("Ten Day Forecast", tenDayScene));

    sidebar.setTitle(gridPoint.location);

    // set current scene
    DayScene curScene = scenes.get(sceneNdx);
    curScene.setActiveScene();
    primaryStage.setScene(curScene.getScene());
    primaryStage.show();
  }

  /**
   * Add all event handlers
   */
  private void addEventHandlers() {
    primaryStage.addEventHandler(NavigationEvent.NAVIGATE, event -> {
      DayScene newScene = event.getTargetScene();
      sceneNdx = scenes.indexOf(newScene);
      Settings.setLastScene(sceneNdx);

      primaryStage.setScene(newScene.getScene());
      newScene.setActiveScene();
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

    // Store the current scene
    Scene lastScene = primaryStage.getScene();

    // Show loading scene first
    primaryStage.setScene(loadingScene.getScene());

    // thenCompose is like flatMap but for CompletableFutures
    // i.e. takes a CF<T> and a function which maps T to another CF<U> and returns
    // CF<U> (as opposed to CF<CF<U>>)
    MyWeatherAPI.getGridPointAsync(lat, lon).orTimeout(8, TimeUnit.SECONDS).thenCompose(point -> {
      if (point == null) {
        throw new RuntimeException("National Weather Service does not have data for this location");
      }

      // Start async calls for forecast and weather observations
      CompletableFuture<ArrayList<HourlyPeriod>> periodFuture = MyWeatherAPI.getHourlyForecastAsync(point.region,
          point.gridX, point.gridY);
      CompletableFuture<Observations> observationFuture = WeatherObservations.getWeatherObservationsAsync(point.region,
          point.gridX, point.gridY, lat, lon);

      // return a combination of both the period future and observation future
      return periodFuture.thenCombine(observationFuture, (periods, observations) -> {
        if (periods == null || observations == null) {
          throw new RuntimeException("Sorry, the National Weather Service does not provide data for " + point.location);
        }
        return new LocationChangeData(periods, observations, point, event.getName());
      }).orTimeout(8, TimeUnit.SECONDS);
    }).thenAccept(result -> { // `thenAccept conditionally runs a consumer function on the previous Future's
                              // sucessful completion

      // NOTE:
      // these Platform.runLater calls are needed because JavaFX UI updates
      // run on a single thread.
      //
      // CompletableFuture methods like `thenAccept` run on **different thread**.
      // JavaFX will ignore any updates to UI made from anywhere but their
      // ❤ special❤ thread.
      //
      // So, we just need to schedule the update on the main thread. (which we can do
      // with `Platform.runLater()`)

      Platform.runLater(() -> {
        // Update scenes
        todayScene.update(result.periods, result.observations);
        threeDayScene.update(result.periods);
        tenDayScene.update(result.periods);

        if (result.name == null) {
          sidebar.setTitle(result.point.location);
        } else {
          // set new location and scene
          sidebar.setTitle(result.name);
        }
        sidebar.setLatLon(lat, lon);
        primaryStage.setScene(lastScene);
        sidebar.recievedValidLocation();
        Settings.setLastLoc(lat, lon);
      });
    }).exceptionally(ex -> { // This is the error case and only runs the enclosed func with an exceptional
                             // completion from the calling future (where ex is the exception thrown)

      // Again, see note above for necesscity of this `runLater` call
      Platform.runLater(() -> {
        String msg = ex.getMessage().substring(ex.getMessage().indexOf(":") + 2);
        NotificationType type = NotificationType.Error;
        if (msg.toLowerCase().contains("timeout")) {
          msg = "Request timed out, check your internet connection";
          type = NotificationType.ConnectionError;
        }
        primaryStage.setScene(lastScene);
        sidebar.recievedInvalidLocation();
        new NotificationBuilder()
            .withMessage(msg)
            .ofType(type)
            .showFor(3)
            .fire(lastScene.getRoot());
      });
      return null;
    });
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
