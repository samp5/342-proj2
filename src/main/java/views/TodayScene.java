package views;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Date;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.chart.AreaChart;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import my_weather.HourlyPeriod;
import views.components.TempGraph;
import views.util.IconResolver;
import views.util.TextUtils;

public class TodayScene {
  // void element to set focus to
  Canvas focusVoid = new Canvas();

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

  // graph for today's temperature
  AreaChart<Number, Number> tempGraph;

  /**
   * initialize a TodayScene
   * to get the {@code Scene}, use {@code getScene()}
   *
   * @param forecast an {@code ArrayList} of {@code HourlyPeriod}, gathered from
   *                 {@code MyWeatherAPI}
   */
  public TodayScene(ArrayList<HourlyPeriod> forecast) {
    initComponents();

    // initialize buttons and text fields
    initialize_unit_buttons();
    initialize_text_fields();

    // populate fields with forecast
    applyForecast(forecast);
    mainView.getChildren().addAll(tempGraph); // needs to be added here, as will otherwise be NULL

    // add styles now that all elements exist
    styleComponents();
    scene.getStylesheets().add("css/baseScene.css");
    scene.getStylesheets().add("css/tempHeader.css");
    scene.getStylesheets().add("css/tempGraph.css");
    scene.getStylesheets().add("css/sidebar.css");
    scene.getStylesheets().add("css/sidebarHeader.css");

    // void any focus that may exist
    focusVoid.requestFocus();
  }

  /**
   * update the scene to use a new forecast
   * 
   * @param forecast an {@code ArrayList} of {@code HourlyPeriod} gathered from
   *                 {@code MyWeatherAPI}
   */
  public void applyForecast(ArrayList<HourlyPeriod> forecast) {
    HourlyPeriod now = forecast.getFirst();

    setTemp(now.temperature);
    setForecast(now.shortForecast);

    try {
      icon = new IconResolver().getIcon(now.shortForecast);
    } catch (FileNotFoundException e) {
      icon = new Image("icons/drizzle.png");
    }
    weatherIcon.setImage(icon);

    TempGraph graph = new TempGraph(forecast, forecast.getFirst().startTime);
    tempGraph = graph.component();
  }

  /**
   * @return a {@code Scene} with the current weather state set by
   *         {@code applyForecast}
   */
  public Scene getScene() {
    return scene;
  }

  /**
   * initialize all components. most components will be added to or modified later
   * during initialization
   */
  private void initComponents() {
    sidebar = new VBox(); // mostly ignored for now

    temperature = new TextField(); // populated later
    weather = new TextField(); // populated later

    fahrenheitBtn = new Button("°F"); // functionality added later
    celsiusBtn = new Button("°C"); // functionality added later
    unitSeparatorBar = new TextField("|"); // bar between the two buttons

    weatherIcon = new ImageView();

    // containers for the unit buttons, temperature header
    unitContainer = new HBox(fahrenheitBtn, unitSeparatorBar, celsiusBtn, focusVoid);
    headerContainer = new HBox(weatherIcon, temperature, unitContainer);

    // main, right-side panel
    mainView = new VBox(headerContainer, weather);

    // entire scene blocks
    sceneBox = new HBox(sidebar, mainView);
    scene = new Scene(sceneBox, 1440, 1024);
  }

  /**
   * sets the {@code forecast} string
   * 
   * @param forecast string to set {@code forecast} to
   */
  private void setForecast(String forecast) {
    this.forecast = forecast;
    weather.setText(forecast);
  }

  /**
   * sets the current temperature in fahrenheit
   * 
   * @param f temperature in fahrenheit
   */
  private void setTemp(int f) {
    fahrenheit = f;
    celsius = (f - 32) * 5 / 9;
    temperature.setText(String.format("%d", f));
  }

  /**
   * sets the displayed temperature to show in fahrenheit
   */
  private void setUnitFahrenheit() {
    temperature.setText(String.format("%d", fahrenheit));
    fahrenheitBtn.setDisable(true);
    celsiusBtn.setDisable(false);
    focusVoid.requestFocus();
  }

  /**
   * sets the displayed temperature to show in celsius
   */
  private void setUnitCelcius() {
    temperature.setText(String.format("%d", celsius));
    fahrenheitBtn.setDisable(false);
    celsiusBtn.setDisable(true);
    focusVoid.requestFocus();
  }

  /**
   * adds functionality and css to the unit switching buttons
   */
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

  /**
   * adds functionality and css to the text fields
   */
  private void initialize_text_fields() {
    // set non editable
    temperature.setEditable(false);
    weather.setEditable(false);
    unitSeparatorBar.setEditable(false);

    // add specific style classes and required style
    temperature.setFont(new Font("Atkinson Hyperlegible Bold", 75));
    temperature.getStyleClass().add("temperature-field");
    unitSeparatorBar.getStyleClass().add("unit-separator");
  }

  /**
   * styles all components, sorted in groups
   */
  private void styleComponents() {
    // TEXT FIELDS
    // - temp
    temperature.textProperty().addListener(new ChangeListener<String>() {
      @Override
      public void changed(ObservableValue<? extends String> ob, String o, String n) {
        setFitWidth(temperature);
      }
    });
    setFitWidth(temperature);
    temperature.setPadding(new Insets(0));
    temperature.setAlignment(Pos.CENTER);
    // - unit sep bar
    unitSeparatorBar.setPrefWidth(16);
    unitSeparatorBar.setPadding(new Insets(0));
    unitSeparatorBar.setAlignment(Pos.CENTER);

    // BUTTONS
    // - unit buttons
    fahrenheitBtn.setPrefWidth(32);
    fahrenheitBtn.setPadding(new Insets(5, 0, 5, 0));
    celsiusBtn.setPrefWidth(32);
    celsiusBtn.setPadding(new Insets(5, 0, 5, 0));

    // IMAGES
    // - weather icon
    weatherIcon.setPreserveRatio(true);
    weatherIcon.setFitWidth(150);
    weatherIcon.setX(100);
    weatherIcon.setY(100);

    // GRAPHS
    // - temperature graph
    tempGraph.setMaxWidth(1000);

    // CONTAINERS
    // - the sidebar
    sidebar.setMinWidth(256);
    sidebar.setStyle("-fx-background-color: #D9D9D9");
    // - main view
    mainView.setMinWidth(1440 - 256);
    mainView.setStyle("-fx-background-color: #FFFFFF");
    mainView.setPadding(new Insets(5));
    // - header box
    headerContainer.setAlignment(Pos.CENTER_LEFT);
    headerContainer.setPadding(new Insets(0));
    // - temp unit button box
    unitContainer.setAlignment(Pos.CENTER_LEFT);
    unitContainer.setMaxHeight(40);
    unitContainer.setPadding(new Insets(0, 0, 20, 0));
  }

  private void setFitWidth(TextField t) {
    t.setPrefWidth(TextUtils.computeTextWidth(t.getFont(), t.getText(), 0.0D) + 10);
  }
}
