package views.components;

import views.util.IconResolver;
import views.util.TextUtils;
import views.components.TempGraph.TempUnit;
import views.components.events.DaySelectionEvent;
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
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.util.Pair;
import my_weather.HourlyPeriod;

public class Day {
  static Pattern windPattern = Pattern.compile("(\\d+)");
  Integer[] fahrenheit, celsius;
  Date date;
  DayView viewType;
  List<HourlyPeriod> currentForecast;
  VBox component;

  public List<HourlyPeriod> getForecast() {
    return this.currentForecast;
  }

  public Date getDate() {
    return this.date;
  }

  TempUnit unit;

  public void setViewType(DayView.DayViewType view) {
    this.viewType.type = view;
  }

  public class DayView {
    public DayViewType type = DayViewType.OneDay;

    public enum DayViewType {
      OneDay, ThreeDay,
    }

    public DayView(DayViewType type) {
      this.type = type;
    }

    @Override
    public String toString() {
      switch (this.type) {
        case OneDay:
          return "one-day";
        case ThreeDay:
          return "three-day";
        default:
          return "";
      }

    }

  }

  @SuppressWarnings("deprecation")
  public Day(ArrayList<HourlyPeriod> data, Date day) {
    this.date = day;
    this.currentForecast =
        data.stream().filter(hperiod -> hperiod.startTime.getDate() == day.getDate()).toList();
    this.fahrenheit = getMinMaxTemp(false);
    this.celsius = getMinMaxTemp(true);
    this.unit = TempUnit.Fahrenheit;
  }

  private Integer[] getMinMaxTemp(boolean celsius) {
    Integer[] minMax = new Integer[] {null, null};
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

  public VBox component(DayView.DayViewType viewType) {
    this.viewType = new DayView(viewType);
    HBox title = dayTitle();
    BorderPane icon = getIcon();
    TextField temperature = getTemperature();
    VBox statistics = getStatistics();
    this.component = new VBox(title, icon, temperature, statistics);
    component.getStyleClass().add("day-backdrop-" + this.viewType.toString());

    component.setOnMouseClicked(e -> {
      select();
      component.fireEvent(new DaySelectionEvent(this));
    });

    return component;
  }

  public void select() {
    component.getStyleClass().remove("day-backdrop-" + this.viewType.toString());
    component.getStyleClass().add("day-backdrop-" + this.viewType.toString() + "-clicked");
  }

  public void deselect() {
    component.getStyleClass().add("day-backdrop-" + this.viewType.toString());
    component.getStyleClass().remove("day-backdrop-" + this.viewType.toString() + "-clicked");
  }

  public VBox getStatistics() {
    int[] stats = parseStats();
    String precipitation_str = "Precipitation: " + stats[0] + "%";
    String humidity_str = "Humidity: " + stats[1] + "%";
    String wind_str = "Wind: " + stats[2] + " mph";

    TextField precipitation = TextUtils.staticTextField(precipitation_str);
    TextField humidity = TextUtils.staticTextField(humidity_str);
    TextField wind = TextUtils.staticTextField(wind_str);

    precipitation.setAlignment(Pos.CENTER);
    humidity.setAlignment(Pos.CENTER);
    wind.setAlignment(Pos.CENTER);

    precipitation.getStyleClass().add("day-statistics-" + this.viewType.toString());
    humidity.getStyleClass().add("day-statistics-" + this.viewType.toString());
    wind.getStyleClass().add("day-statistics-" + this.viewType.toString());

    VBox statbox = new VBox(precipitation, humidity, wind);
    statbox.setPadding(new Insets(0, 0, 5, 0));
    return statbox;
  }

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
    return new int[] {maxPrep, totalRelHumid / size, totalWindspeed / size};
  }

  public TextField getTemperature() {
    String text = null;
    switch (this.unit) {
      case Celsius:
        text = String.format("%d-%d°C", this.celsius[0], this.celsius[1]);
        break;
      case Fahrenheit:
        text = String.format("%d-%d°F", this.fahrenheit[0], this.fahrenheit[1]);
        break;
    }

    TextField tempText = TextUtils.staticTextField(text);
    tempText.getStyleClass().add("day-temperature-text-" + this.viewType.toString());

    return tempText;
  }

  public BorderPane getIcon() {
    Image icon;
    ImageView weatherIcon = new ImageView();
    Pair<String, Boolean> commonForecast = getCommonForecast();
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

  // ok this is kind of cursed
  private Pair<String, Boolean> getCommonForecast() {
    Hashtable<String, Pair<Integer, Boolean>> table = new Hashtable<>();
    for (HourlyPeriod p : currentForecast) {
      if (table.keySet().contains(p.shortForecast)) {
        Pair<Integer, Boolean> pair = table.get(p.shortForecast);
        table.put(p.shortForecast, new Pair<>(pair.getKey() + 1, p.isDaytime));
      } else {
        table.put(p.shortForecast, new Pair<>(1, p.isDaytime));
      }
    }

    Pair<String, Boolean> common = new Pair<>("Sunny", true);
    int commonCount = -1;
    for (Entry<String, Pair<Integer, Boolean>> pair : table.entrySet()) {
      if (pair.getValue().getKey() > commonCount) {
        commonCount = pair.getValue().getKey();
        common = new Pair<>(pair.getKey(), pair.getValue().getValue());
      }
    }

    return common;
  }

  @SuppressWarnings("deprecation")
  public HBox dayTitle() {
    String text = null;
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

    Text title = new Text(text);
    title.getStyleClass().add("day-title-" + this.viewType.toString());
    HBox box = new HBox(title);
    box.setAlignment(Pos.CENTER);
    box.setPadding(new Insets(10, 0, 0, 0));
    return box;
  }

}
