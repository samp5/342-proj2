package views;

import java.io.FileNotFoundException;
import java.util.ArrayList;

import endpoints.my_weather.data.HourlyPeriod;
import endpoints.weather_observations.data.Observations;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import views.components.HumidityGraph;
import views.components.TempGraph;
import views.components.events.TempUnitEvent;
import views.components.widgets.CompassBox;
import views.components.widgets.DewPointBox;
import views.components.widgets.EmptyBox;
import views.components.widgets.PressureBox;
import views.components.widgets.SmallBox;
import views.components.widgets.VisibilityBox;
import views.util.IconResolver;
import views.util.LocationChangeData.DetailedForecasts;
import views.util.TextUtils;
import views.util.UnitHandler;
import views.util.UnitHandler.TemperatureUnit;

/**
 * A single day view of the weather, extending {@code DayScene}.
 * This is the primary view of the app.
 * Allows for changing of units globally, and displays more detailed and useful
 * information than other views.
 */
public class TodayScene extends DayScene {
  // display text
  TextField temperatureTxt, unitSeparatorBar;
  Text longforecastTxt;
  VBox forecastBox;
  boolean toggled = false;

  // images
  Image icon;
  ImageView weatherIcon;

  // scene blocking
  HBox headerContainer, temperatureContainer, detailContainer, graphContainer;
  HBox hBar;

  // temperature unit buttons
  Button fahrenheitBtn, celsiusBtn;
  HBox unitContainer;

  // store the temperature and forecast
  int fahrenheit, celsius;
  String shortForecast;
  String longForecast;

  // graph for today's temperature, data that made it
  TempGraph tempGraph;
  HumidityGraph humidGraph;
  VBox tempChart, humidChart;

  // currently stored forecast and observations
  ArrayList<HourlyPeriod> currentForecast;
  Observations currentObservations;
  DetailedForecasts detailedForecasts;

  // small charts blocking
  HBox smallBoxes;
  VBox compassBox, pressureBox, dewpointBox, visibilityBox;

  /**
   * initialize a TodayScene
   * to get the {@code Scene}, use {@code getScene()}
   *
   * @param forecast an {@code ArrayList} of {@code HourlyPeriod}, gathered from
   *                 {@code MyWeatherAPI}
   */
  public TodayScene(ArrayList<HourlyPeriod> forecast, Observations observations,
      DetailedForecasts detailed) {
    initComponents();

    // populate fields with forecast
    detailedForecasts = detailed;
    currentForecast = forecast;
    currentObservations = observations;

    applyForecast();

    // initialize buttons and text fields
    initialize_unit_buttons();
    initialize_text_fields();

    // add styles now that all elements exist
    styleComponents();

    // void any focus that may exist
    voidFocus();
  }

  /**
   * update the view to use a new forecast and observations
   *
   * @param forecast     the new forecast to use
   * @param observations the new observations to use
   */
  public void update(ArrayList<HourlyPeriod> forecast, Observations observations,
      DetailedForecasts detailed) {
    currentForecast = forecast;
    currentObservations = observations;
    detailedForecasts = detailed;
    applyForecast();
  }

  /**
   * update the scene with the {@code currentForecast}
   */
  protected void applyForecast() {
    // use the most current forecast
    HourlyPeriod now = currentForecast.getFirst();

    // update temperature, forecast, and other text fields
    setTemp(now.temperature);
    setLongForecast(detailedForecasts.getDetailedForecast(UnitHandler.getUnit()));

    // get the new weather icon. defaults to drizzle
    try {
      icon = new IconResolver().getIcon(now.shortForecast, now.isDaytime);
    } catch (FileNotFoundException e) {
      icon = new Image("icons/drizzle.png");
    }
    weatherIcon.setImage(icon);

    // create data charts
    tempGraph = new TempGraph(currentForecast, UnitHandler.getUnit());
    tempChart = tempGraph.component();
    humidGraph = new HumidityGraph(currentForecast, currentForecast.getFirst().startTime);
    humidChart = humidGraph.component();

    // place data charts
    graphContainer.getChildren().setAll(tempChart, humidChart);

    // create the smaller data views
    createSmallGraphs();
  }

  /**
   * initialize all components. most components will be added to or modified later
   * during initialization
   */
  protected void initComponents() {
    super.initComponents();

    temperatureTxt = new TextField(); // populated later
    longforecastTxt = new Text();

    hBar = new HBox();
    fahrenheitBtn = new Button("°F"); // functionality added later
    celsiusBtn = new Button("°C"); // functionality added later
    unitSeparatorBar = new TextField("|"); // bar between the two buttons

    weatherIcon = new ImageView();

    // containers for the unit buttons, temperature header, graphs
    forecastBox = new VBox(longforecastTxt);
    unitContainer = new HBox(fahrenheitBtn, unitSeparatorBar, celsiusBtn);
    temperatureContainer = new HBox(weatherIcon, temperatureTxt, unitContainer);
    detailContainer = new HBox(forecastBox);
    headerContainer = new HBox(temperatureContainer, detailContainer);
    graphContainer = new HBox();

    // small box container
    smallBoxes = new HBox();

    // add components to the main view
    mainView.getChildren().addAll(headerContainer, hBar, graphContainer, smallBoxes);

    // set this view as the emitter for unit changes
    UnitHandler.setEmitter(mainView);
    scene.addEventHandler(TempUnitEvent.TEMPUNITCHANGE, event -> {
      TemperatureUnit unit = event.getUnit();
      updateTempGraph(unit);
      updateDetailedForecast();
      createSmallGraphs();
    });
  }

  /**
   * sets the {@code forecast} string
   * 
   * @param forecast string to set {@code forecast} to
   */
  private void updateDetailedForecast() {
    setLongForecast(this.detailedForecasts.getDetailedForecast(UnitHandler.getUnit()));
  }

  /**
   * sets the {@code forecast} string
   * 
   * @param forecast string to set {@code forecast} to
   */
  private void setLongForecast(String forecast) {
    longForecast = forecast;
    longforecastTxt.setText(forecast);
  }

  /**
   * sets the current temperature in fahrenheit
   * 
   * @param f temperature in fahrenheit
   */
  private void setTemp(int f) {
    fahrenheit = f;
    celsius = (int) ((f - 32.) * 5. / 9.);

    if (UnitHandler.getUnit() == TemperatureUnit.Celsius) {
      setUnitCelcius();
    } else {
      setUnitFahrenheit();
    }
  }

  /**
   * sets the displayed temperature to show in fahrenheit
   */
  private void setUnitFahrenheit() {
    temperatureTxt.setText(String.format("%d", fahrenheit));
    fahrenheitBtn.setDisable(true);
    celsiusBtn.setDisable(false);

    UnitHandler.setUnit(TemperatureUnit.Fahrenheit);

    voidFocus();
  }

  /**
   * sets the displayed temperature to show in celsius
   */
  private void setUnitCelcius() {
    temperatureTxt.setText(String.format("%d", celsius));
    fahrenheitBtn.setDisable(false);
    celsiusBtn.setDisable(true);

    UnitHandler.setUnit(TemperatureUnit.Celsius);

    voidFocus();
  }

  /**
   * updates the temperature graph
   *
   * @param unit unit to fill the graph with
   */
  private void updateTempGraph(TemperatureUnit unit) {
    // find the location of the current chart
    int chartNdx = graphContainer.getChildren().indexOf(tempChart);

    // get the new graph
    tempGraph.update(currentForecast, unit);
    tempChart = tempGraph.component();

    // replace the old graph with the new one
    graphContainer.getChildren().remove(chartNdx);
    graphContainer.getChildren().add(chartNdx, tempChart);

    // re-set the min width again
    tempChart.setMinWidth(554);
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
    unitSeparatorBar.setEditable(false);
    longforecastTxt.setText(longForecast);

    // add specific style classes and required style
    temperatureTxt.setFont(new Font("Atkinson Hyperlegible Bold", 75));
    temperatureTxt.getStyleClass().add("temperature-field");
    unitSeparatorBar.getStyleClass().add("unit-separator");
  }

  /**
   * creates small graphs from current forecast
   */
  private void createSmallGraphs() {
    HourlyPeriod now = currentForecast.getFirst();
    SmallBox compass, pressure, dewpoint, visibility;

    // add the wind speed box. handle if null.
    if (currentObservations.windSpeed.value != null
        && currentObservations.windDirection.value != null) {
      compass = new CompassBox(currentObservations.windSpeed.mph(),
          currentObservations.windDirection.value);
    } else {
      compass = new EmptyBox("Wind Direction");
    }
    compassBox = compass.component();

    // add the air pressure box. handle if null.
    if (currentObservations.barometricPressure.value != null) {
      pressure = new PressureBox(currentObservations.barometricPressure.value);
    } else {
      pressure = new EmptyBox("Air Pressure");
    }
    pressureBox = pressure.component();

    // always add dewpoint, as it is (theoretically) always present
    dewpoint = new DewPointBox(now.dewpoint.value);
    dewpointBox = dewpoint.component();

    // add the visibility box. handle if null.
    if (currentObservations.visibility.value != null) {
      visibility = new VisibilityBox(currentObservations.visibility.value);
    } else {
      visibility = new EmptyBox("visibility");
    }
    visibilityBox = visibility.component();

    smallBoxes.getChildren().setAll(compassBox, pressureBox, dewpointBox, visibilityBox);
  }

  /**
   * styles all components, sorted in groups
   */
  private void styleComponents() {
    scene.getStylesheets().add("css/tempHeader.css");
    scene.getStylesheets().add("css/tempGraph.css");
    scene.getStylesheets().add("css/smallBox.css");

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

    // - short forecast and feels like
    longforecastTxt.setFont(new Font("Atkinson Hyperlegible Normal", 20));
    longforecastTxt.setWrappingWidth(600);

    forecastBox.setPadding(new Insets(0, 0, 0, 20));
    forecastBox.setAlignment(Pos.CENTER_LEFT);

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
    // - temperature box
    temperatureContainer.setAlignment(Pos.CENTER_LEFT);
    temperatureContainer.setPadding(new Insets(0));

    // - detail box
    detailContainer.getStyleClass().add("left-border");
    detailContainer.setAlignment(Pos.CENTER_LEFT);

    // - temp unit button box
    unitContainer.setAlignment(Pos.CENTER_LEFT);
    unitContainer.setMaxHeight(40);
    unitContainer.setPadding(new Insets(0, 0, 20, 0));

    // - forecast box
    hBar.getStyleClass().add("h-bar");

    // - small charts container
    smallBoxes.setSpacing(20);

    // - graph container
    graphContainer.setSpacing(20);
  }
}
