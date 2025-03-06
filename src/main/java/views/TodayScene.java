package views;

import java.util.ArrayList;

import javafx.scene.Scene;
import javafx.scene.chart.AreaChart;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import my_weather.HourlyPeriod;
import views.components.TempGraph;


public class TodayScene {
  // main scene
  Scene scene;

  // display text
  TextField temperature, weather, unitSeparatorBar;

  // images
  Image weatherIcon;

  // scene blocking
  HBox sceneBox, headerContainer;
  VBox sidebar, mainView;
  
  // temperature unit buttons
  Button fahrenheitBtn, celsiusBtn;
  HBox unitContainer;

  // store the temperature and forecast
  int fahrenheit, celsius;
  String forecast;

  
  // init all components
  private void initComponents() {
    sidebar = new VBox();
    mainView = new VBox();
    sceneBox = new HBox(sidebar, mainView);
		scene = new Scene(sceneBox, 1440, 1024);

    temperature = new TextField();
    weather = new TextField();

    fahrenheitBtn = new Button("°F");
    celsiusBtn = new Button("°C");
    unitSeparatorBar = new TextField("|");

    unitContainer = new HBox(fahrenheitBtn, unitSeparatorBar, celsiusBtn);
    headerContainer = new HBox(temperature, unitContainer);
  }

  public TodayScene(ArrayList<HourlyPeriod> forecast) {
    initComponents();

    // create the sidebar
    sidebar.setMinWidth(256);
    sidebar.setStyle("-fx-background-color: #D9D9D9");

    // create the main view
    mainView.setMinWidth(1440 - 256);
    mainView.setStyle("-fx-background-color: #FFFFFF");

    // initialize the buttons
    initialize_unit_buttons();

    // initialize text fields
    initialize_text_fields();
    TempGraph chart = new TempGraph(forecast, forecast.getFirst().startTime);

    // add all elements
    mainView.getChildren().addAll(temperature, weather, unitContainer, chart.component());

    // add global css
    scene.getStylesheets().add("css/baseScene.css");
    scene.getStylesheets().add("css/tempHeader.css");
    scene.getStylesheets().add("css/tempGraph.css");

    // populate fields with forecast
    applyForecast(forecast);
  }

  private void applyForecast(ArrayList<HourlyPeriod> forecast) {
    HourlyPeriod now = forecast.getFirst();

    setFahrenheight(now.temperature);
    setForecast(now.shortForecast);
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
    fahrenheitBtn.setDisable(true);
    celsiusBtn.setDisable(false);
    temperature.requestFocus();
  }

  private void setUnitCelcius() {
    temperature.setText(String.format("%d", celsius));
    fahrenheitBtn.setDisable(false);
    celsiusBtn.setDisable(true);
    temperature.requestFocus();
  }

  private void initialize_unit_buttons() {
    // set on click actions
    fahrenheitBtn.setOnAction(e -> {
      setUnitFahrenheit();
    });
    celsiusBtn.setOnAction(e -> {
      setUnitCelcius();
    });

    // initialize default to fahrenheit
    setUnitFahrenheit();
  }

  private void initialize_text_fields() {
    // set non editable
    temperature.setEditable(false);
    weather.setEditable(false);
    unitSeparatorBar.setEditable(false);

    // add specific style classes
    temperature.getStyleClass().add("temperature-field");
  }
}
