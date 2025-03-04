import javafx.application.Application;

import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import weather.Period;
import weather.WeatherAPI;

import java.util.ArrayList;

public class JavaFX extends Application {
	TextField temperature, weather;
  
  // temp units
  Button fahrenheit_btn, celsius_btn;
  HBox unit_container;

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

		temperature = new TextField();
		weather = new TextField();
		temperature.setText("Today's weather is: "+String.valueOf(forecast.get(0).temperature));
		weather.setText(forecast.get(0).shortForecast);

    initialize_unit_buttons();


		Scene scene = new Scene(new VBox(temperature, weather, unit_container), 1440, 1024);
		primaryStage.setScene(scene);
		primaryStage.show();
	}

  private void initialize_unit_buttons() {
    // create the buttons
    fahrenheit_btn = new Button("°F");
    celsius_btn = new Button("°C");

    // create their container
    unit_container = new HBox(fahrenheit_btn, celsius_btn);

    // set on click actions
    fahrenheit_btn.setOnAction(e -> {
      ;
    });
    celsius_btn.setOnAction(e -> {
      ;
    });

    // appearance
    fahrenheit_btn.setStyle("-fx-background-color: 0xFF0000; ");
  }
}
