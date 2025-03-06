package views;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Date;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
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
import views.components.TempGraph.TempUnit;
import views.components.HumidityGraph;
import views.components.sidebar.Sidebar;
import views.util.IconResolver;
import views.util.TextUtils;

public class TodayScene {
  // void element to set focus to
  Canvas focusVoid = new Canvas();

  // main scene
  Scene scene;

  // display text
  TextField temperatureTxt, forecastTxt, unitSeparatorBar;

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
  String shortForecast;

  // graph for today's temperature, data that made it
  TempGraph tempGraph;
  AreaChart<Number, Number> tempChart;
  AreaChart<Number, Number> humidChart;
  ArrayList<HourlyPeriod> currentForecast;

  /**
   * initialize a TodayScene
   * to get the {@code Scene}, use {@code getScene()}
   *
   * @param forecast an {@code ArrayList} of {@code HourlyPeriod}, gathered from
   *                 {@code MyWeatherAPI}
   */
  public TodayScene(ArrayList<HourlyPeriod> forecast) {
    initComponents();

    // populate fields with forecast
    currentForecast = forecast;
    applyForecast();
    mainView.getChildren().addAll(tempChart); // needs to be added here, as will otherwise be NULL
    mainView.getChildren().addAll(humidChart); // needs to be added here, as will otherwise be NULL
                                            
    // initialize buttons and text fields
    initialize_unit_buttons();
    initialize_text_fields();

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
   * update the scene with the {@code currentForecast}
   */
  public void applyForecast() {
    HourlyPeriod now = currentForecast.getFirst();

    setTemp(now.temperature);
    setShortForecast(now.shortForecast);

    try {
      icon = new IconResolver().getIcon(now.shortForecast);
    } catch (FileNotFoundException e) {
      icon = new Image("icons/drizzle.png");
    }
    weatherIcon.setImage(icon);

    tempGraph = new TempGraph(currentForecast, currentForecast.getFirst().startTime, TempUnit.Fahrenheit);
    tempChart = tempGraph.component();
    HumidityGraph hGraph = new HumidityGraph(currentForecast, currentForecast.getFirst().startTime);
    humidChart = hGraph.component();
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

    temperatureTxt = new TextField(); // populated later
    forecastTxt = new TextField(); // populated later

    fahrenheitBtn = new Button("°F"); // functionality added later
    celsiusBtn = new Button("°C"); // functionality added later
    unitSeparatorBar = new TextField("|"); // bar between the two buttons

    weatherIcon = new ImageView();

    // containers for the unit buttons, temperature header
    unitContainer = new HBox(fahrenheitBtn, unitSeparatorBar, celsiusBtn, focusVoid);
    headerContainer = new HBox(weatherIcon, temperatureTxt, unitContainer);

    // main, right-side panel
    mainView = new VBox(headerContainer, forecastTxt);

    // entire scene blocks
    sceneBox = new HBox(sidebar, mainView);
    scene = new Scene(sceneBox, 1440, 1024);
  }

  /**
   * sets the {@code forecast} string
   * 
   * @param forecast string to set {@code forecast} to
   */
  private void setShortForecast(String forecast) {
    shortForecast = forecast;
    forecastTxt.setText(forecast);
  }

  /**
   * sets the current temperature in fahrenheit
   * 
   * @param f temperature in fahrenheit
   */
  private void setTemp(int f) {
    fahrenheit = f;
    celsius = (f - 32) * 5 / 9;
    temperatureTxt.setText(String.format("%d", f));
  }

  /**
   * sets the displayed temperature to show in fahrenheit
   */
  private void setUnitFahrenheit() {
    temperatureTxt.setText(String.format("%d", fahrenheit));
    fahrenheitBtn.setDisable(true);
    celsiusBtn.setDisable(false);

    int chartNdx = mainView.getChildren().indexOf(tempChart);
    tempGraph.update(currentForecast, new Date(), TempUnit.Fahrenheit);
    tempChart = tempGraph.component();
    mainView.getChildren().remove(chartNdx);
    mainView.getChildren().add(chartNdx, tempChart);

    focusVoid.requestFocus();
  }

  /**
   * sets the displayed temperature to show in celsius
   */
  private void setUnitCelcius() {
    temperatureTxt.setText(String.format("%d", celsius));
    fahrenheitBtn.setDisable(false);
    celsiusBtn.setDisable(true);

    int chartNdx = mainView.getChildren().indexOf(tempChart);
    tempGraph.update(currentForecast, new Date(), TempUnit.Celsius);
    tempChart = tempGraph.component();
    mainView.getChildren().remove(chartNdx);
    mainView.getChildren().add(chartNdx, tempChart);

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
    temperatureTxt.setEditable(false);
    forecastTxt.setEditable(false);
    unitSeparatorBar.setEditable(false);

    // add specific style classes and required style
    temperatureTxt.setFont(new Font("Atkinson Hyperlegible Bold", 75));
    temperatureTxt.getStyleClass().add("temperature-field");
    forecastTxt.getStyleClass().add("short-forecast");
    unitSeparatorBar.getStyleClass().add("unit-separator");
  }

  /**
   * styles all components, sorted in groups
   */
  private void styleComponents() {
    // TEXT FIELDS
    // - temp
    temperatureTxt.textProperty().addListener(new ChangeListener<String>() {
      @Override
      public void changed(ObservableValue<? extends String> ob, String o, String n) {
        setFitWidth(temperatureTxt);
      }
    });
    setFitWidth(temperatureTxt);
    temperatureTxt.setPadding(new Insets(0));
    temperatureTxt.setAlignment(Pos.CENTER);
    // - unit sep bar
    unitSeparatorBar.setPrefWidth(16);
    unitSeparatorBar.setPadding(new Insets(0));
    unitSeparatorBar.setAlignment(Pos.CENTER);
    // - short forecast
    forecastTxt.setFont(new Font("Atkinson Hyperlegible Normal", 20));
    forecastTxt.setPadding(new Insets(10, 0, 0, 0));
    forecastTxt.setAlignment(Pos.CENTER);
    setFitWidth(forecastTxt, 26);

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
    tempChart.setMaxWidth(1000);
    humidChart.setMaxWidth(1000);

    // CONTAINERS
    // - the sidebar
    sidebar.setMinWidth(256);
    sidebar.setStyle("-fx-background-color: #D9D9D9");
    // - main view
    mainView.setMinWidth(1440 - 256);
    mainView.setStyle("-fx-background-color: #FFFFFF");
    mainView.setPadding(new Insets(20));
    // - header box
    headerContainer.setAlignment(Pos.CENTER_LEFT);
    headerContainer.setPadding(new Insets(0));
    // - temp unit button box
    unitContainer.setAlignment(Pos.CENTER_LEFT);
    unitContainer.setMaxHeight(40);
    unitContainer.setPadding(new Insets(0, 0, 20, 0));
  }

  private void setFitWidth(TextField t, double padding) {
    double width = TextUtils.computeTextWidth(t.getFont(), t.getText(), 0.0D) + padding;
    t.setMaxWidth(width);
    t.setMinWidth(width);
    t.setPrefWidth(width);
  }

  private void setFitWidth(TextField t) {
    setFitWidth(t, 10);
  }
}
