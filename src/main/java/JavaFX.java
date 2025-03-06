import javafx.application.Application;

import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.stage.Stage;
import my_weather.HourlyPeriod;
import views.ThreeDayScene;
import views.TodayScene;
import views.components.sidebar.NavigationEvent;
import weather.Period;
import weather.WeatherAPI;

import java.util.ArrayList;

public class JavaFX extends Application {
  TodayScene todayScene;
  ThreeDayScene threeDayScene;
  Scene currentScene;
  
	public static void main(String[] args) {
		launch(args);
	}

	//feel free to remove the starter code from this method
	@Override
	public void start(Stage primaryStage) throws Exception {
		primaryStage.setTitle("I'm a professional Weather App!");

		ArrayList<HourlyPeriod> forecast = my_weather.MyWeatherAPI.getHourlyForecast("LOT",77,70);
		if (forecast == null){
			throw new RuntimeException("Forecast did not load");
		}

    
    todayScene = new TodayScene(forecast);
    threeDayScene = new ThreeDayScene();

    currentScene = todayScene.getScene();

    primaryStage.addEventHandler(NavigationEvent.NAVIGATE, event -> {
      primaryStage.setScene(event.getTargetScene());
    });

    primaryStage.setScene(currentScene);
    primaryStage.show();
  }

}
