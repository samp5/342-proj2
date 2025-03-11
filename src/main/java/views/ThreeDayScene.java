package views;

import java.util.ArrayList;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import my_weather.HourlyPeriod;
import views.components.DayCollection;
import views.components.Day.DayView.DayViewType;
import views.components.Day;

import views.components.TempGraph;
import views.components.events.DaySelectionEvent;
import views.components.events.TempUnitEvent;
import views.util.UnitHandler;
import views.components.HumidityGraph;

/**
 * A three day scene for more days of information.
 * This is the secondary view of the app.
 * Allows for seeing of multiple days data, and selecting of days to show
 * temperature and humidity graphs.
 */
public class ThreeDayScene extends DayScene {
  // scene blocking
  HBox dayCollectionBox, graphContainer;

  // currently stored forecast
  ArrayList<HourlyPeriod> currentForecast;

  // visual elements
  TempGraph tempGraph;
  HumidityGraph humidGraph;
  VBox tempChart, humidChart;

  /**
   * create a new {@code ThreeDayScene} to show the forecast for the next 3 days
   *
   * @param forecast the forecast to use to make the view. must contain at least 3
   *                 days worth of data
   */
  public ThreeDayScene(ArrayList<HourlyPeriod> forecast) {
    initComponents();

    addEventHandlers();
    currentForecast = forecast;
    applyForecast();

    styleComponents();
  }

  /**
   * apply the {@code currentForecast} to the view, updating elements
   */
  protected void applyForecast() {
    DayCollection collection = new DayCollection(3, currentForecast, DayViewType.ThreeDay);
    dayCollectionBox.getChildren().setAll(collection.component());
    graphContainer.getChildren().setAll();

    // always select the first day (I feel like this makes it clearer that you can
    // click on them?)
    showGraphs(collection.getDays().getFirst());
    collection.getDays().getFirst().select();
  }

  /**
   * initialize all components
   */
  protected void initComponents() {
    super.initComponents();

    graphContainer = new HBox();
    dayCollectionBox = new HBox();
    mainView.getChildren().addAll(dayCollectionBox, graphContainer);
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
    graphContainer.setSpacing(40);
    graphContainer.setAlignment(Pos.CENTER);
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

  /**
   * updates the temperature graph with a new one from the stored forecast.
   * used for changing units while in this view
   */
  private void updateTempGraph() {
    // find the location of the current chart
    int chartNdx = graphContainer.getChildren().indexOf(tempChart);

    // get the new graph
    tempGraph.update(currentForecast, UnitHandler.getUnit());
    tempChart = tempGraph.component();

    // replace the old graph with the new one
    graphContainer.getChildren().remove(chartNdx);
    graphContainer.getChildren().add(chartNdx, tempChart);

    // re-set the max width again
    tempChart.setMaxWidth(1000);
  }

  /**
   * create and display graphs related to a certain {@code Day}
   *
   * @param d the day to show graphs for
   */
  public void showGraphs(Day d) {
    tempGraph = new TempGraph(d.getForecast(), UnitHandler.getUnit());
    tempChart = tempGraph.component();
    humidGraph = new HumidityGraph(d.getForecast(), d.getDate());
    humidChart = humidGraph.component();

    graphContainer.getChildren().setAll(tempChart, humidChart);
  }

  /**
   * add all event handlers in this view
   */
  private void addEventHandlers() {
    this.scene.addEventHandler(DaySelectionEvent.DAY_SELECTION, day -> {
      showGraphs(day.selection());
    });
    scene.addEventHandler(TempUnitEvent.TEMPUNITCHANGE, event -> {
      System.out.println("3 rec");
    });
  }
}
