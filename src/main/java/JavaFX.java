import javafx.application.Application;

import javafx.stage.Stage;
import javafx.util.Pair;
import my_weather.HourlyPeriod;
import views.DayScene;
import views.ThreeDayScene;
import views.TodayScene;
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

    ArrayList<HourlyPeriod> forecast = my_weather.MyWeatherAPI.getHourlyForecast("LOT", 77, 70);
    if (forecast == null) {
      throw new RuntimeException("Forecast did not load");
    }

    todayScene = new TodayScene(forecast);
    threeDayScene = new ThreeDayScene(forecast);

    Sidebar.fromScenes(
      new Pair<String, DayScene>("Daily Forecast", todayScene),
      new Pair<String, DayScene>("Three Day Forecast", threeDayScene)
    );

    todayScene.setActiveScene();

    primaryStage.addEventHandler(NavigationEvent.NAVIGATE, event -> {
      event.getTargetScene().setActiveScene();
      primaryStage.setScene(event.getTargetScene().getScene());
    });

    primaryStage.setScene(todayScene.getScene());
    primaryStage.show();
  }

}
