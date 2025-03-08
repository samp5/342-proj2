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
import weather_observations.Properties;
import weather_observations.WeatherObservations;

import java.util.ArrayList;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import javafx.stage.Popup;

public class JavaFX extends Application {
  TodayScene todayScene;
  ThreeDayScene threeDayScene;
  LoadingScene loadingScene;

  Sidebar sidebar;
  Stage primaryStage;

  public static void main(String[] args) {
    launch(args);
  }

  // feel free to remove the starter code from this method
  @Override
  public void start(Stage primaryStage) throws Exception {
    this.primaryStage = primaryStage;

    primaryStage.setTitle("WeatherFX");

    addEventHandlers();

    GridPoint gridPoint = null;
    ArrayList<HourlyPeriod> forecast = null;

    loadingScene = new LoadingScene();

    double lat = 41.8781;
    double lon = -87.6298;
    try {
      gridPoint = my_weather.MyWeatherAPI.getGridPoint(lat, lon);
      forecast =
          my_weather.MyWeatherAPI.getHourlyForecast(gridPoint.region, gridPoint.gridX,
              gridPoint.gridY);
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

    if (forecast == null) {
      throw new RuntimeException("Forecast did not load");
    }

    Properties weatherObservations = WeatherObservations.getWeatherObservations(gridPoint.region, gridPoint.gridX, gridPoint.gridY, lat, lon);

    todayScene = new TodayScene(forecast);
    threeDayScene = new ThreeDayScene(forecast);

    sidebar = Sidebar.fromScenes(
        new Pair<String, DayScene>("Daily Forecast", todayScene),
        new Pair<String, DayScene>("Three Day Forecast", threeDayScene));

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
    CompletableFuture<GridPoint> pointFuture =
        MyWeatherAPI.getGridPointAsync(event.getLat(), event.getLon());

    GridPoint point = null;
    Scene lastScene = primaryStage.getScene();
    primaryStage.setScene(loadingScene.getScene());

    try {
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



    // get forecast
    CompletableFuture<ArrayList<HourlyPeriod>> future_period =
        MyWeatherAPI.getHourlyForecastAsync(point.region,
            point.gridX, point.gridY);

    ArrayList<HourlyPeriod> periods = null;

    try {
      periods = future_period.get(3, TimeUnit.SECONDS);
      if (periods == null) {
        primaryStage.setScene(lastScene);
        sidebar.recievedInvalidLocation();
        return;
      }
      todayScene.update(periods);
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

    sidebar.setTitle(point.location);

    primaryStage.setScene(lastScene);
    sidebar.recievedValidLocation();
  }

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

}
