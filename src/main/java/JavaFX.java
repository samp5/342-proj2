import javafx.application.Application;

import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.util.Pair;
import my_weather.HourlyPeriod;
import views.ThreeDayScene;
import views.TodayScene;
import views.components.sidebar.NavigationEvent;
import views.components.sidebar.Sidebar;

import java.util.ArrayList;

public class JavaFX extends Application {
  TodayScene todayScene;
  ThreeDayScene threeDayScene;
  Scene currentScene;

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

    Sidebar globalSidebar = Sidebar.fromScenes(
      new Pair<String, Scene>("Daily Forecast", todayScene.getScene()),
      new Pair<String, Scene>("Three Day Forecast", threeDayScene.getScene())
    );
    todayScene.setSidebar(globalSidebar);
    threeDayScene.setSidebar(globalSidebar);

    currentScene = todayScene.getScene();
    todayScene.setActiveScene();

    primaryStage.addEventHandler(NavigationEvent.NAVIGATE, event -> {
      primaryStage.setScene(event.getTargetScene());
    });

    primaryStage.setScene(currentScene);
    primaryStage.show();
  }

}
