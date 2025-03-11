package views;

import java.util.ArrayList;

import endpoints.my_weather.data.HourlyPeriod;
import javafx.scene.Scene;
import javafx.scene.layout.VBox;
import views.components.Day.DayView.DayViewType;
import views.components.DayCollection;
import views.components.TempGraph;
import views.util.UnitHandler;

/**
 * A three day scene for more days of information.
 * This is the secondary view of the app.
 * Allows for seeing of multiple days data, and selecting of days to show
 * temperature and humidity graphs.
 */
public class TenDayScene extends DayScene {
  // scene blocking
  VBox dayCollectionBox;

  TempGraph tempGraph;

  // currently stored forecast
  ArrayList<HourlyPeriod> currentForecast;

  /**
   * create a new {@code ThreeDayScene} to show the forecast for the next 3 days
   *
   * @param forecast the forecast to use to make the view. must contain at least 3
   *                 days worth of data
   */
  public TenDayScene(ArrayList<HourlyPeriod> forecast) {
    initComponents();
    currentForecast = forecast;
    applyForecast();

    styleComponents();
  }

  /**
   * apply the {@code currentForecast} to the view, updating elements
   */
  protected void applyForecast() {
    DayCollection collection = new DayCollection(10, currentForecast, DayViewType.TenDay);
    dayCollectionBox.getChildren().setAll(collection.component());
  }

  /**
   * initialize all components
   */
  protected void initComponents() {
    super.initComponents();

    dayCollectionBox = new VBox();
    mainView.getChildren().addAll(dayCollectionBox);
  }

  /**
   * returns the scene.
   * also updates the view if the temperature has changed.
   */
  @Override
  public Scene getScene() {
    if (UnitHandler.hasChanged()) {
      applyForecast();
      UnitHandler.recognizeChange();
    }

    return scene;
  }

  /**
   * style all view components
   */
  private void styleComponents() {
    scene.getStylesheets().add("css/day.css");
    scene.getStylesheets().add("css/tempGraph.css");
  }

  /**
   * update the view to use a new forecast
   */
  public void update(ArrayList<HourlyPeriod> forecast) {
    currentForecast = forecast;
    applyForecast();
    // updateTempGraph();
  }

}
