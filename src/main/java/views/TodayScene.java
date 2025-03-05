package views;

import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

public class TodayScene {
  // main scene
  Scene scene;

  // display text
  TextField temperature, weather;

  // scene blocking
  HBox sceneBox;
  VBox sidebar, mainView;
  
  // temperature unit buttons
  Button fahrenheit_btn, celsius_btn;
  HBox unit_container;

  // store the temperature and forecast
  int fahrenheit, celsius;
  String forecast;

  public TodayScene() {
    sidebar = new VBox();
    sidebar.setMinWidth(256);
    sidebar.setStyle("-fx-background-color: #D9D9D9");

    mainView = new VBox();
    mainView.setMinWidth(1440 - 256);
    mainView.setStyle("-fx-background-color: #FFFFFF");

		temperature = new TextField();
		weather = new TextField();

    initialize_unit_buttons();

    mainView.getChildren().addAll(temperature, weather, unit_container);

    sceneBox = new HBox(sidebar, mainView);
		scene = new Scene(sceneBox, 1440, 1024);

    scene.getStylesheets().add("css/buttons.css");
  }

  public Scene getScene() {
    return scene;
  }

  public void setForecast(String forecast) {
    this.forecast = forecast;
    weather.setText(forecast);
  }

  public void setFahrenheight(int f) {
    fahrenheit = f;
    celsius = (f - 32) * 5 / 9;
    temperature.setText(String.format("%d", f));
  }

  public void setCelcius(int c) {
    celsius = c;
    fahrenheit = (c * 9 / 5) + 32;
    temperature.setText(String.format("%d", c));
  }

  private void setUnitFahrenheit() {
    temperature.setText(String.format("%d", fahrenheit));
    fahrenheit_btn.setDisable(true);
    celsius_btn.setDisable(false);
    temperature.requestFocus();
  }

  private void setUnitCelcius() {
    temperature.setText(String.format("%d", celsius));
    fahrenheit_btn.setDisable(false);
    celsius_btn.setDisable(true);
    temperature.requestFocus();
  }

  private void initialize_unit_buttons() {
    // create the buttons
    fahrenheit_btn = new Button("°F");
    celsius_btn = new Button("°C");

    // create their container
    unit_container = new HBox(fahrenheit_btn, celsius_btn);

    // set on click actions
    fahrenheit_btn.setOnAction(e -> {
      setUnitFahrenheit();
    });
    celsius_btn.setOnAction(e -> {
      setUnitCelcius();
    });

    // initialize default to fahrenheit
    setUnitFahrenheit();
  }
}
