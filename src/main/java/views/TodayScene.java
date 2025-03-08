package views;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Date;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import my_weather.HourlyPeriod;
import views.components.TempGraph;
import views.components.TempGraph.TempUnit;
import views.components.CompassBox;
import views.components.HumidityGraph;
import views.util.IconResolver;
import views.util.TextUtils;

public class TodayScene extends DayScene {
  // display text
  TextField temperatureTxt, forecastTxt, unitSeparatorBar;

  // images
  Image icon;
  ImageView weatherIcon;

  // scene blocking
  HBox headerContainer, graphContainer;
  Pane forecastBox;

  // temperature unit buttons
  Button fahrenheitBtn, celsiusBtn;
  HBox unitContainer;

  // store the temperature and forecast
  int fahrenheit, celsius;
  String shortForecast;

  // graph for today's temperature, data that made it
  TempGraph tempGraph;
  HumidityGraph humidGraph;
  VBox tempChart, humidChart;
  ArrayList<HourlyPeriod> currentForecast;

  // small charts blocking
  HBox smallCharts;

  // wind direction compass
  VBox compassBox;

  // temperature trend
  VBox tempTrendBox;

  // dewpoint
  VBox dewpointBox;

  /**
   * initialize a TodayScene
   * to get the {@code Scene}, use {@code getScene()}
   *
   * @param forecast an {@code ArrayList} of {@code HourlyPeriod}, gathered from
   *        {@code MyWeatherAPI}
   */
  public TodayScene(ArrayList<HourlyPeriod> forecast) {
    initComponents();

    // populate fields with forecast
    currentForecast = forecast;

    applyForecast();

    // initialize buttons and text fields
    initialize_unit_buttons();
    initialize_text_fields();

    // add styles now that all elements exist
    styleComponents();

    // void any focus that may exist
    voidFocus();
  }

  public void update(ArrayList<HourlyPeriod> forecast) {
    currentForecast = forecast;
    applyForecast();
  }

  /**
   * update the scene with the {@code currentForecast}
   */
  protected void applyForecast() {
    HourlyPeriod now = currentForecast.getFirst();

    setTemp(now.temperature);
    setShortForecast(now.shortForecast);

    try {
      icon = new IconResolver().getIcon(now.shortForecast, now.isDaytime);
    } catch (FileNotFoundException e) {
      icon = new Image("icons/drizzle.png");
    }
    weatherIcon.setImage(icon);

    tempGraph =
        new TempGraph(currentForecast, currentForecast.getFirst().startTime, TempUnit.Fahrenheit);
    tempChart = tempGraph.component();
    humidGraph = new HumidityGraph(currentForecast, currentForecast.getFirst().startTime);
    humidChart = humidGraph.component();

    graphContainer.getChildren().setAll(tempChart, humidChart);

    createSmallGraphs();
  }

  /**
   * initialize all components. most components will be added to or modified later
   * during initialization
   */
  protected void initComponents() {
    super.initComponents();

    temperatureTxt = new TextField(); // populated later
    forecastTxt = new TextField(); // populated later
    forecastBox = new Pane(forecastTxt);

    fahrenheitBtn = new Button("°F"); // functionality added later
    celsiusBtn = new Button("°C"); // functionality added later
    unitSeparatorBar = new TextField("|"); // bar between the two buttons

    weatherIcon = new ImageView();

    // containers for the unit buttons, temperature header, graphs
    unitContainer = new HBox(fahrenheitBtn, unitSeparatorBar, celsiusBtn);
    headerContainer = new HBox(weatherIcon, temperatureTxt, unitContainer);
    graphContainer = new HBox();

    // small charts & their container
    compassBox = new VBox();
    tempTrendBox = new VBox();
    dewpointBox = new VBox();
    smallCharts = new HBox();

    // add components to the main view
    mainView.getChildren().addAll(headerContainer, forecastBox, graphContainer, smallCharts);
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
    celsius = (int) ((f - 32.) * 5. / 9.);
    temperatureTxt.setText(String.format("%d", f));
  }

  /**
   * sets the displayed temperature to show in fahrenheit
   */
  private void setUnitFahrenheit() {
    temperatureTxt.setText(String.format("%d", fahrenheit));
    fahrenheitBtn.setDisable(true);
    celsiusBtn.setDisable(false);

    updateTempGraph(TempUnit.Fahrenheit);

    voidFocus();
  }

  /**
   * sets the displayed temperature to show in celsius
   */
  private void setUnitCelcius() {
    temperatureTxt.setText(String.format("%d", celsius));
    fahrenheitBtn.setDisable(false);
    celsiusBtn.setDisable(true);

    updateTempGraph(TempUnit.Celsius);

    voidFocus();
  }

  /**
   * updates the temperature graph
   *
   * @param unit unit to fill the graph with
   */
  private void updateTempGraph(TempUnit unit) {
    // find the location of the current chart
    int chartNdx = graphContainer.getChildren().indexOf(tempChart);

    // get the new graph
    tempGraph.update(currentForecast, new Date(), unit);
    tempChart = tempGraph.component();

    // replace the old graph with the new one
    graphContainer.getChildren().remove(chartNdx);
    graphContainer.getChildren().add(chartNdx, tempChart);

    // re-set the max width again
    tempChart.setMaxWidth(1000);
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
   * creates small graphs from current forecast
   */
  private void createSmallGraphs() {
    HourlyPeriod now = currentForecast.getFirst();

    tempTrendBox.getChildren().setAll(TextUtils.staticTextField(now.temperatureTrend));
    dewpointBox.getChildren()
        .setAll(TextUtils.staticTextField("" + now.dewpoint.value + " " + now.dewpoint.unitCode));

    tempTrendBox.getStyleClass().addAll("temp-trend-box", "small-graph");
    dewpointBox.getStyleClass().addAll("dewpoint-box", "small-graph");

    CompassBox compass = new CompassBox(now.windSpeed, now.windDirection);
    compassBox = compass.component();

    smallCharts.getChildren().setAll(compassBox/* , tempTrendBox, dewpointBox */);
  }

  /**
   * styles all components, sorted in groups
   */
  private void styleComponents() {
    scene.getStylesheets().add("css/tempHeader.css");
    scene.getStylesheets().add("css/tempGraph.css");
    scene.getStylesheets().add("css/smallChart.css");

    // TEXT FIELDS
    // - temp
    temperatureTxt.textProperty().addListener(new ChangeListener<String>() {
      @Override
      public void changed(ObservableValue<? extends String> ob, String o, String n) {
        TextUtils.setFitWidth(temperatureTxt);
      }
    });
    TextUtils.setFitWidth(temperatureTxt);
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
    tempChart.setMinWidth(554);
    humidChart.setMinWidth(554);

    // CONTAINERS
    // - header box
    headerContainer.setAlignment(Pos.CENTER_LEFT);
    headerContainer.setPadding(new Insets(0));

    // - temp unit button box
    unitContainer.setAlignment(Pos.CENTER_LEFT);
    unitContainer.setMaxHeight(40);
    unitContainer.setPadding(new Insets(0, 0, 20, 0));

    // - forecast box
    forecastBox.getStyleClass().add("forecast-box");

    // - small charts container
    smallCharts.setSpacing(20);

    // - graph container
    graphContainer.setSpacing(20);
  }
}
