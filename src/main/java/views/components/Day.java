package views.components;

import views.util.IconResolver;
import views.util.UnitHandler;
import views.components.Day.DayView.DayViewType;
import views.components.events.DaySelectionEvent;
import views.util.UnitHandler.TemperatureUnit;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Hashtable;
import java.util.List;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.util.Pair;
import my_weather.HourlyPeriod;

/**
 * A simple object containing forecast information for a given {@code Date}.
 * Typically created in a {@code DayCollection}.
 * Shows a given days temperature range, max precipitation, average windspeed,
 * and average humidity.
 * Allows for additional graph shown to give more detailed temperature and
 * humidity information.
 * 
 * @see {@code DayCollection}.
 */
public class Day {
  // regex pattern for getting the wind number from a string in the format of `X
  // mph`
  static Pattern windPattern = Pattern.compile("(\\d+)");

  // temperatures stored in {min, max} format
  Integer[] fahrenheit, celsius;

  // true if selected for graph display, false otherwise.
  // only one Day will be true at once
  boolean selected = false;

  // util for building the Day
  Date date;
  DayView viewType;
  List<HourlyPeriod> currentForecast;
  Region component;
  TemperatureUnit unit;

  /**
   * get this {@code Day}s forecast
   *
   * @return a {@code List} of {@code HourlyPeriod}s
   */
  public List<HourlyPeriod> getForecast() {
    return this.currentForecast;
  }

  /**
   * get the {@code Date} associated with this {@code Day}
   *
   * @return the associated {@code Date}
   */
  public Date getDate() {
    return this.date;
  }

  /**
   * set the type of view for the day.
   * 
   * @see DayViewType
   *
   * @param view the type of view
   */
  public void setViewType(DayView.DayViewType view) {
    this.viewType.type = view;
  }

  public class DayView {
    // default to OneDay view
    public DayViewType type;

    public enum DayViewType {
      ThreeDay, TenDay
    }

    /**
     * create a new DayView with a given type
     *
     * @param type the {@code DayViewType}
     */
    public DayView(DayViewType type) {
      this.type = type;
    }

    /**
     * get the associated string with this {@code DayView}
     *
     * @return a {@code String} for styling
     */
    @Override
    public String toString() {
      switch (this.type) {
        case ThreeDay:
          return "three-day";
        case TenDay:
          return "ten-day";
        default:
          return "";
      }

    }

  }

  /**
   * create a new {@code Day} from a list of {@code HourlyPeriod}s and a given
   * {@code Date}
   *
   * @param data a list of {@code HourlyPeriod} containing data for the given
   *             {@code day}
   * @param day  the {@code Date} for the data to display
   */
  @SuppressWarnings("deprecation")
  public Day(ArrayList<HourlyPeriod> data, Date day) {
    this.date = day;
    this.currentForecast = data.stream().filter(hperiod -> hperiod.startTime.getDate() == day.getDate()).toList();
    this.fahrenheit = getMinMaxTemp(false);
    this.celsius = getMinMaxTemp(true);
    this.unit = UnitHandler.getUnit();
  }

  /**
   * calculate the minimum and maximum temperature throughout the day
   * 
   * @param celsius {@code true} to calculate celsius data, {@code false} to
   *                calculate fahrenheit data.
   * @return an {@code Integer} array of size two in the format of {min, max}
   */
  private Integer[] getMinMaxTemp(boolean celsius) {
    Integer[] minMax = new Integer[] { null, null };
    int temperature;
    for (HourlyPeriod p : currentForecast) {
      if (celsius) {
        temperature = (int) ((p.temperature - 32.) * 5. / 9.);
      } else {
        temperature = p.temperature;
      }

      if (minMax[0] == null || temperature < minMax[0]) {
        minMax[0] = temperature;
      }
      if (minMax[1] == null || temperature > minMax[1]) {
        minMax[1] = temperature;
      }
    }

    return minMax;
  }

  /**
   * get the {@code Region} component for this {@code Day}
   *
   * @return the {@code Region} component for this {@code Day}
   */
  public Region component(DayView.DayViewType viewType) {
    // read view type
    this.viewType = new DayView(viewType);

    // gather data
    HBox title = dayTitle();
    BorderPane icon = getIcon();
    HBox temperature = getTemperature();
    VBox statistics = getStatistics();

    switch (this.viewType.type) {
      case TenDay:

        VBox leftBox = new VBox(title, icon);
        leftBox.setPadding(new Insets(20));

        VBox textBox = new VBox(temperature, statistics);
        textBox.setPadding(new Insets(20));
        textBox.setAlignment(Pos.CENTER_LEFT);

        VBox graphBox = new VBox(new TempGraph(this.currentForecast, this.unit).smallComponent());
        graphBox.setAlignment(Pos.CENTER);

        this.component = new HBox(leftBox, textBox, graphBox);
        this.component.setMaxHeight(75);

        ((ImageView) icon.getCenter()).setFitWidth(75);
        ((ImageView) icon.getCenter()).setFitHeight(75);

        ((HBox) this.component).setSpacing(20);

        break;

      case ThreeDay:
        // create component
        this.component = new VBox(title, icon, temperature, statistics);

        // add click functionality
        // responds regardless of which part of the component is clicked
        component.addEventFilter(MouseEvent.MOUSE_CLICKED, e -> {
          if (selected)
            return;

          select();
          component.fireEvent(new DaySelectionEvent(this));
        });
        break;
      default:
        break;
    }

    // add style class based on viewType
    component.getStyleClass().add("day-backdrop-" + this.viewType.toString());

    return component;
  }

  /**
   * select this {@code Day} as the current day for graphs.
   */
  public void select() {
    selected = true;
    component.getStyleClass().remove("day-backdrop-" + this.viewType.toString());
    component.getStyleClass().add("day-backdrop-" + this.viewType.toString() + "-clicked");
  }

  /**
   * set this {@code Day} as not selected for the current day for graphs
   */
  public void deselect() {
    selected = false;
    component.getStyleClass().add("day-backdrop-" + this.viewType.toString());
    component.getStyleClass().remove("day-backdrop-" + this.viewType.toString() + "-clicked");
  }

  /**
   * get a {@code VBox} containing basic statistics about the {@code Day}
   * includes precipitation, humitidy, and wind
   *
   * @return a {@code VBox} for displaying the statistics
   */
  public VBox getStatistics() {
    int[] stats = parseStats();
    String precipitation_str = "Precipitation: " + stats[0] + "%";
    String humidity_str = "Humidity: " + stats[1] + "%";
    String wind_str = "Wind: " + stats[2] + " mph";

    Text precipitation = new Text(precipitation_str);
    Text humidity = new Text(humidity_str);
    Text wind = new Text(wind_str);

    humidity.getStyleClass().add("day-statistics-" + this.viewType.toString());
    wind.getStyleClass().add("day-statistics-" + this.viewType.toString());
    precipitation.getStyleClass().add("day-statistics-" + this.viewType.toString());

    VBox statbox = new VBox(precipitation, humidity, wind);
    statbox.getStyleClass().add("day-statistics-box-" + this.viewType.toString());

    return statbox;
  }

  /**
   * takes the {@code currentForecast} and parses the data for displaying.
   * finds the maximum precipitation, the average humidity, and the average
   * windspeed.
   *
   * @return an {@code int} array of size 3, formatted {precipitation, humidity,
   *         windspeed}
   */
  private int[] parseStats() {
    int maxPrep = 0;
    int totalRelHumid = 0;
    int totalWindspeed = 0;

    for (HourlyPeriod p : currentForecast) {
      // precipitation
      if (p.probabilityOfPrecipitation.value > maxPrep) {
        maxPrep = p.probabilityOfPrecipitation.value;
      }

      // relative humidity
      totalRelHumid += p.relativeHumidity.value;

      // wind speed
      Matcher m = Day.windPattern.matcher(p.windSpeed);
      if (m.find())
        totalWindspeed += Integer.parseInt(m.group(1));
      else
        totalWindspeed += 0;
    }

    // divide for averages
    int size = currentForecast.size();
    return new int[] { maxPrep, totalRelHumid / size, totalWindspeed / size };
  }

  /**
   * gets the current temperature range based on the stored unit
   *
   * @return a {@code TextField} containing the labelled temperature range
   */
  public HBox getTemperature() {
    String text = null;
    switch (this.unit) {
      case Celsius:
        text = String.format("%d-%d°C", this.celsius[0], this.celsius[1]);
        break;
      case Fahrenheit:
        text = String.format("%d-%d°F", this.fahrenheit[0], this.fahrenheit[1]);
        break;
    }

    Text tempText = new Text(text);
    tempText.getStyleClass().add("day-temperature-" + this.viewType.toString());
    HBox box = new HBox(tempText);
    box.getStyleClass().add("day-temperature-box-" + this.viewType.toString());

    return box;
  }

  /**
   * gets the icon for the current {@code Day}
   *
   * @return a {@code BorderPane} containing the icon for the average weather
   *         throughout the {@code Day}
   */
  public BorderPane getIcon() {
    Image icon;
    ImageView weatherIcon = new ImageView();

    // get the most common forecast string and whether or not it is night
    Pair<String, Boolean> commonForecast = getCommonForecast();
    // get the icon from above
    try {
      icon = new IconResolver().getIcon(commonForecast.getKey(), commonForecast.getValue());
    } catch (FileNotFoundException e) {
      icon = new Image("/icons/drizzle.png");
    }

    weatherIcon.setImage(icon);
    weatherIcon.setPreserveRatio(true);

    BorderPane pane = new BorderPane();
    pane.setCenter(weatherIcon);
    return pane;
  }

  /**
   * gets the most common {@code shortForecast} string found throughout the day
   *
   * @return a {@code Pair} of {@code String} and {@code Boolean} of the most
   *         common {@code shortForecast} and {@code true} if day, {@code false}
   *         if night.
   */
  private Pair<String, Boolean> getCommonForecast() {
    // table of shortForecast to occurrence count, day/night
    Hashtable<String, Pair<Integer, Boolean>> table = new Hashtable<>();

    // iterate through the days forecast
    for (HourlyPeriod p : currentForecast) {
      // if found before, add one, else set to one
      if (table.keySet().contains(p.shortForecast)) {
        Pair<Integer, Boolean> pair = table.get(p.shortForecast);
        table.put(p.shortForecast, new Pair<>(pair.getKey() + 1, p.isDaytime));
      } else {
        table.put(p.shortForecast, new Pair<>(1, p.isDaytime));
      }
    }

    // find the most common
    Pair<String, Boolean> common = new Pair<>("Sunny", true);
    int commonCount = -1;

    // go through the map to find the max count
    for (Entry<String, Pair<Integer, Boolean>> pair : table.entrySet()) {
      if (pair.getValue().getKey() > commonCount) {
        commonCount = pair.getValue().getKey();
        common = new Pair<>(pair.getKey(), pair.getValue().getValue());
      }
    }

    return common;
  }

  /**
   * get the name of the day of the week based on stored {@code Date}
   *
   * @return an {@code HBox} with the text set as the day of the week
   */
  @SuppressWarnings("deprecation")
  public HBox dayTitle() {
    String text = null;
    // if the date is today, just return today.
    // otherwise return day of the week
    if (this.date.getDate() == new Date().getDate()) {
      text = "Today";
    } else {
      switch (this.date.getDay()) {
        case 0:
          text = "Sunday";
          break;
        case 1:
          text = "Monday";
          break;
        case 2:
          text = "Tuesday";
          break;
        case 3:
          text = "Wednesday";
          break;
        case 4:
          text = "Thursday";
          break;
        case 5:
          text = "Friday";
          break;
        case 6:
          text = "Saturday";
          break;
      }
    }

    // create the hbox
    Text title = new Text(text);
    title.getStyleClass().add("day-title-" + this.viewType.toString());
    HBox box = new HBox(title);
    box.getStyleClass().add("day-title-box-" + this.viewType.toString());

    return box;
  }

}
