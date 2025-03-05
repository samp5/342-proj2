import javafx.application.Application;

import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.stage.Stage;
import views.ThreeDayScene;
import views.TodayScene;
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

		ArrayList<Period> forecast = WeatherAPI.getForecast("LOT",77,70);
		if (forecast == null){
			throw new RuntimeException("Forecast did not load");
		}

    todayScene = new TodayScene();
    threeDayScene = new ThreeDayScene();

    currentScene = todayScene.getScene();
    todayScene.setFahrenheight(forecast.get(0).temperature);
    todayScene.setForecast(forecast.get(0).shortForecast);

		primaryStage.setScene(currentScene);
		primaryStage.show();
	}

}
