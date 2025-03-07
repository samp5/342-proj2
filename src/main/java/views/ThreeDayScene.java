package views;

import java.util.ArrayList;

import javafx.scene.layout.HBox;
import my_weather.HourlyPeriod;
import views.components.DayCollection;
import views.components.Day.DayView.DayViewType;

public class ThreeDayScene extends DayScene {
  HBox dayCollectionBox;
  ArrayList<HourlyPeriod> currentForecast;

  public ThreeDayScene(ArrayList<HourlyPeriod> forecast) {
    initComponents();

    currentForecast = forecast;
    applyForecast();

    styleComponents();
  }

  public void applyForecast() {
    DayCollection collection = new DayCollection(3, currentForecast, DayViewType.ThreeDay);
    dayCollectionBox.getChildren().setAll(collection.component());
  }

  protected void initComponents() {
    super.initComponents();

    dayCollectionBox = new HBox();
    mainView.getChildren().addAll(dayCollectionBox);
  }

  private void styleComponents() {
    scene.getStylesheets().add("css/day.css");
  }
}
