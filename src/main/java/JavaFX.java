import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.util.Pair;
import my_weather.HourlyPeriod;
import my_weather.gridPoint.GridPoint;
import my_weather.MyWeatherAPI;
import views.DayScene;
import views.LoadingScene;
import views.ThreeDayScene;
import views.TodayScene;
import views.components.events.LocationChangeEvent;
import views.components.sidebar.NavigationEvent;
import views.components.sidebar.Sidebar;

import java.util.ArrayList;
import java.util.Date;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class JavaFX extends Application {
  TodayScene todayScene;
  ThreeDayScene threeDayScene;

  public static void main(String[] args) {
    launch(args);
  }

  // feel free to remove the starter code from this method
  @Override
  public void start(Stage primaryStage) throws Exception {
    primaryStage.setTitle("I'm a professional Weather App!");

    GridPoint gridPoint = my_weather.MyWeatherAPI.getGridPoint(41.8781, -87.6298);
    ArrayList<HourlyPeriod> forecast = my_weather.MyWeatherAPI.getHourlyForecast(gridPoint.region, gridPoint.gridX,
        gridPoint.gridY);
    if (forecast == null) {
      throw new RuntimeException("Forecast did not load");
    }

    todayScene = new TodayScene(forecast);
    threeDayScene = new ThreeDayScene(forecast);

    Sidebar sidebar = Sidebar.fromScenes(
        new Pair<String, DayScene>("Daily Forecast", todayScene),
        new Pair<String, DayScene>("Three Day Forecast", threeDayScene));

    todayScene.setActiveScene();

    primaryStage.addEventHandler(NavigationEvent.NAVIGATE, event -> {
      event.getTargetScene().setActiveScene();
      primaryStage.setScene(event.getTargetScene().getScene());
    });

    primaryStage.addEventHandler(LocationChangeEvent.LOCATIONCHANGE, event -> {
      CompletableFuture<GridPoint> pointFuture = MyWeatherAPI.getGridPointAsync(event.getLat(), event.getLon());
      GridPoint point = null;

      // do some styling here..
      System.out.println("Waiting on gridpoint");
      try {
        point = pointFuture.get();
      } catch (Exception e) {
        e.printStackTrace();
      }

      if (point == null) {
        sidebar.recievedInvalidLocation();
        return;
      }

      Scene lastScene = primaryStage.getScene();
      primaryStage.setScene(new LoadingScene().getScene());

      CompletableFuture<ArrayList<HourlyPeriod>> future_period = MyWeatherAPI.getHourlyForecastAsync(point.region,
          point.gridX, point.gridY);

      sidebar.recievedValidLocation();
      sidebar.setTitle(point.location);
      ArrayList<HourlyPeriod> periods = new ArrayList<>();
      System.out.println("Waiting on forecast");
      try {
        periods = future_period.get(3, TimeUnit.SECONDS);
        todayScene.update(periods);
      } catch (TimeoutException timeout) {
        System.err.println("getting forecast timed out :(");
      } catch (Exception e) {
        e.printStackTrace();
      }
      primaryStage.setScene(lastScene);
    });

    primaryStage.setScene(todayScene.getScene());
    primaryStage.show();
  }

}
