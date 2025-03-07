import javafx.application.Application;

import javafx.stage.Stage;
import javafx.util.Pair;
import my_weather.HourlyPeriod;
import my_weather.gridPoint.GridPoint;
import my_weather.MyWeatherAPI;
import views.DayScene;
import views.ThreeDayScene;
import views.TodayScene;
import views.components.events.LocationChangeEvent;
import views.components.sidebar.NavigationEvent;
import views.components.sidebar.Sidebar;

import java.util.ArrayList;

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
    ArrayList<HourlyPeriod> forecast = my_weather.MyWeatherAPI.getHourlyForecast(gridPoint.region, gridPoint.gridX, gridPoint.gridY);
    if (forecast == null) {
      throw new RuntimeException("Forecast did not load");
    }

    todayScene = new TodayScene(forecast);
    threeDayScene = new ThreeDayScene(forecast);

    Sidebar sidebar = Sidebar.fromScenes(
      new Pair<String, DayScene>("Daily Forecast", todayScene),
      new Pair<String, DayScene>("Three Day Forecast", threeDayScene)
    );

    todayScene.setActiveScene();

    primaryStage.addEventHandler(NavigationEvent.NAVIGATE, event -> {
      event.getTargetScene().setActiveScene();
      primaryStage.setScene(event.getTargetScene().getScene());
    });

    primaryStage.addEventHandler(LocationChangeEvent.LOCATIONCHANGE, event -> {
      GridPoint point = MyWeatherAPI.getGridPoint(event.getLat(), event.getLon());
      if (point == null) {
        sidebar.recievedInvalidLocation();
        return;
      }

      sidebar.recievedValidLocation();
      ArrayList<HourlyPeriod> periods = MyWeatherAPI.getHourlyForecast(point.region, point.gridX, point.gridY);
      todayScene.update(periods);
    });

    primaryStage.setScene(todayScene.getScene());
    primaryStage.show();
  }

}
