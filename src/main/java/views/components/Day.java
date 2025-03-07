package views.components;

import views.util.IconResolver;
import views.util.TextUtils;
import views.components.TempGraph.TempUnit;

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
import my_weather.HourlyPeriod;

public class Day {
  static Pattern windPattern = Pattern.compile("(\\d+)");
  Integer[] fahrenheit, celsius;
  Date date;
  DayView viewType;
  List<HourlyPeriod> currentForecast;

  TempUnit unit;

  public void setViewType(DayView.DayViewType view) {
    this.viewType.type = view;
  }

  public class DayView {
    public DayViewType type = DayViewType.OneDay;

    public enum DayViewType {
      OneDay,
      ThreeDay,
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
    this.currentForecast = data.stream().filter(hperiod -> hperiod.startTime.getDate() == day.getDate()).toList();
    this.fahrenheit = getMinMaxTemp(false);
    this.celsius = getMinMaxTemp(true);
    this.unit = TempUnit.Fahrenheit;
  }

  private Integer[] getMinMaxTemp(boolean celsius) {
    Integer[] minMax = new Integer[] {null, null};
    int temperature;
    for (HourlyPeriod p : currentForecast) {
      if (celsius) {
        temperature = (int)((p.temperature - 32.) * 5. / 9.);
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
    VBox vbox = new VBox(title, icon, temperature, statistics);
    vbox.getStyleClass().add("day-backdrop-" + this.viewType.toString());

    return vbox;
  }

  public VBox getStatistics() {
    String precipitation_str = "Precipitation: " + avgPrecipitation() + "%";
    String humidity_str = "Humidity: " + avgRelHumidity() + "%";
    String wind_str = "Wind: " + avgWindSpeed() + " mph";

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

  private int avgPrecipitation() {
    int total = 0;
    for (HourlyPeriod p : currentForecast) total += p.probabilityOfPrecipitation.value;
    return total / currentForecast.size();
  }

  private int avgRelHumidity() {
    int total = 0;
    for (HourlyPeriod p : currentForecast) total += p.relativeHumidity.value;
    return total / currentForecast.size();
  }

  private int avgWindSpeed() {
    int total = 0;
    for (HourlyPeriod p : currentForecast) {
      Matcher m = Day.windPattern.matcher(p.windSpeed);
      if (m.find()) total += Integer.parseInt(m.group(1));
      else total += 0;
    }
    return total / currentForecast.size();
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
    String commonForecast = getCommonForecast();
    try {
      icon = new IconResolver().getIcon(commonForecast);
    } catch (FileNotFoundException e) {
      icon = new Image("/icons/drizzle.png");
    }
    weatherIcon.setImage(icon);

    weatherIcon.setPreserveRatio(true);
    
    BorderPane pane = new BorderPane();
    pane.setCenter(weatherIcon);
    return pane;
  }

  private String getCommonForecast() {
    Hashtable<String, Integer> table = new Hashtable<>();
    for (HourlyPeriod p : currentForecast) {
      System.out.println(p.shortForecast);
      if (table.keySet().contains(p.shortForecast)) {
        table.put(p.shortForecast, table.get(p.shortForecast) + 1);
      } else {
        table.put(p.shortForecast, 1);
      }
    }

    String common = "Sunny";
    int commonCount = -1;
    for (Entry<String, Integer> pair : table.entrySet()) {
      if (pair.getValue() > commonCount) {
        System.out.println(common + " replaced with " + pair.getKey());
        System.out.println(commonCount + " of prev, " + pair.getValue() + " of new");
        commonCount = pair.getValue();
        common = pair.getKey();
      }
    }
    System.out.println(common);

    System.out.println();

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
