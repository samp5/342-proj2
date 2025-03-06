package views.components;

import views.util.IconResolver;
import views.components.TempGraph.TempUnit;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.function.Predicate;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Background;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import my_weather.HourlyPeriod;

public class Day {
  int fahrenheit, celsius;
  String shortForecast;
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
    this.fahrenheit = currentForecast.getFirst().temperature;
    this.celsius = (int) ((this.fahrenheit - 32) * 5.0 / 9.0);
    this.unit = TempUnit.Fahrenheit;
    this.shortForecast = this.currentForecast.getFirst().shortForecast;
  }

  public VBox component(DayView.DayViewType viewType) {
    this.viewType = new DayView(viewType);
    HBox title = dayTitle();
    ImageView icon = getIcon();
    Text temperature = getTemperature();
    VBox statistics = getStatistics();
    HBox hbox = new HBox(icon, temperature);
    VBox vbox = new VBox(title, hbox, statistics);
    vbox.getStyleClass().add("day-backdrop-" + this.viewType.toString());

    return vbox;
  }

  public VBox getStatistics() {
    HourlyPeriod forecast_now = this.currentForecast.getFirst();
    String precipitation_str = "Precipitation: " + forecast_now.probabilityOfPrecipitation.value + "%";
    String humidity_str = "Humidity: " + String.format("%d", forecast_now.relativeHumidity.value) + "%";
    String wind_str = "Wind: " + forecast_now.windSpeed + " " + forecast_now.windDirection;

    Text precipitation = new Text(precipitation_str);
    Text humidity = new Text(humidity_str);
    Text wind = new Text(wind_str);

    precipitation.getStyleClass().add("day-statistics-" + this.viewType.toString());
    humidity.getStyleClass().add("day-statistics-" + this.viewType + toString());
    wind.getStyleClass().add("day-statistics-" + this.viewType.toString());

    VBox statbox = new VBox(precipitation, humidity, wind);
    return statbox;
  }

  public Text getTemperature() {
    String text = null;
    switch (this.unit) {
      case Celsius:
        text = String.format("%d °", this.celsius);
        break;
      case Fahrenheit:
        text = String.format("%d °", this.fahrenheit);
        break;
    }

    Text tempText = new Text(text);
    tempText.getStyleClass().add("day-temperature-text-" + this.viewType.toString());

    return tempText;
  }

  public ImageView getIcon() {
    Image icon;
    ImageView weatherIcon = new ImageView();
    try {
      icon = new IconResolver().getIcon(this.shortForecast);
    } catch (FileNotFoundException e) {
      icon = new Image("/icons/drizzle.png");
    }
    weatherIcon.setImage(icon);

    weatherIcon.setPreserveRatio(true);
    return weatherIcon;
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
    return box;
  }

}
