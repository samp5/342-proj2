package views;

import java.io.FileNotFoundException;
import java.util.ArrayList;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import my_weather.HourlyPeriod;

import views.util.IconResolver;
import views.util.TextUtils;


public class TodayScene {
  // main scene
  Scene scene;

  // display text
  TextField temperature, weather, unitSeparatorBar;

  // images
  Image icon;
  ImageView weatherIcon;

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

    weatherIcon = new ImageView();

    unitContainer = new HBox(fahrenheitBtn, unitSeparatorBar, celsiusBtn);
    headerContainer = new HBox(weatherIcon, temperature, unitContainer);
  }

  public TodayScene(ArrayList<HourlyPeriod> forecast) {
    initComponents();
    styleComponents();

    // initialize the buttons
    initialize_unit_buttons();

    // initialize text fields
    initialize_text_fields();

    // add all elements
    mainView.getChildren().addAll(headerContainer, weather);

    // add global css
    scene.getStylesheets().add("css/baseScene.css");
    scene.getStylesheets().add("css/tempHeader.css");

    // populate fields with forecast
    applyForecast(forecast);
  }

  private void applyForecast(ArrayList<HourlyPeriod> forecast) {
    HourlyPeriod now = forecast.getFirst();

    setFahrenheight(now.temperature);
    setForecast(now.shortForecast);

    try{
      icon = new IconResolver().getIcon(now.shortForecast);
    } catch (FileNotFoundException e) {
      icon = new Image("icons/drizzle.png");
    }
    weatherIcon.setImage(icon);
    weatherIcon.setPreserveRatio(true);
    weatherIcon.setFitWidth(150);
    weatherIcon.setX(100);
    weatherIcon.setY(100);
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

    // add specific style classes
    fahrenheitBtn.getStyleClass().add("temperature-button");
    celsiusBtn.getStyleClass().add("temperature-button");
  }

  private void initialize_text_fields() {
    // set non editable
    temperature.setEditable(false);
    weather.setEditable(false);
    unitSeparatorBar.setEditable(false);

    // add specific style classes
    temperature.getStyleClass().add("temperature-field");
    unitSeparatorBar.getStyleClass().add("unit-separator");
  }

  private void styleComponents() {
    // TEXT FIELDS
    //  - temp
    temperature.setFont(new Font("Atkinson Hyperlegible Bold", 75));
    temperature.textProperty().addListener(new ChangeListener<String>() {
      @Override
      public void changed(ObservableValue<? extends String> ob, String o, String n) {
        temperature.setPrefWidth(TextUtils.computeTextWidth(temperature.getFont(), temperature.getText(), 0.0D) + 10);
      }
    });
    temperature.setPadding(new Insets(10, 0, 0, 0));
    temperature.setAlignment(Pos.CENTER_RIGHT);
    //  - unit sep bar
    unitSeparatorBar.setPrefWidth(20);
    unitSeparatorBar.setAlignment(Pos.CENTER);

    // BUTTONS
    //  - unit buttons
    fahrenheitBtn.setPrefWidth(20);
    fahrenheitBtn.setPadding(new Insets(5, 0, 5, 0));
    celsiusBtn.setPrefWidth(20);
    celsiusBtn.setPadding(new Insets(5, 0, 5, 0));

    // CONTAINERS
    //  - the sidebar
    sidebar.setMinWidth(256);
    sidebar.setStyle("-fx-background-color: #D9D9D9");
    //  - main view
    mainView.setMinWidth(1440 - 256);
    mainView.setStyle("-fx-background-color: #FFFFFF");
    mainView.setPadding(new Insets(5));
    //  - 
  }
}
