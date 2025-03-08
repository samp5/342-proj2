package views;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;

public class LoadingScene extends DayScene {
  // scene blocking
  HBox headerContainer, graphContainer;
  Pane forecastBox;
  HBox smallCharts;

  public LoadingScene() {
    initComponents();

    // add styles now that all elements exist
    styleComponents();

    // void any focus that may exist
    voidFocus();
  }

  /**
   * initialize all components. most components will be added to or modified later
   * during initialization
   */
  protected void initComponents() {
    super.initComponents();
    headerContainer = new HBox();
    graphContainer = new HBox();
    forecastBox = new Pane();
    smallCharts = new HBox();

    mainView.getChildren().addAll(headerContainer, forecastBox, graphContainer, smallCharts);
  }

  /**
   * styles all components, sorted in groups
   */
  private void styleComponents() {
    scene.getStylesheets().add("css/loadingScene.css");

    // CONTAINERS
    // - header box
    headerContainer.getStyleClass().add("load-box");
    headerContainer.setMaxWidth(554);
    headerContainer.setMinHeight(200);


    // - forecast box
    forecastBox.getStyleClass().add("load-box");
    forecastBox.setMaxWidth(554);
    forecastBox.setMinHeight(75);

    // - small charts container
    smallCharts.setSpacing(20);
    smallCharts.getStyleClass().add("load-box");
    smallCharts.setMaxWidth(1000);
    smallCharts.setMinHeight(200);

    // - graph container
    graphContainer.setSpacing(20);
    graphContainer.getStyleClass().add("load-box");
    graphContainer.setMaxWidth(1000);
    graphContainer.setMinHeight(300);
  }

  @Override
  protected void applyForecast() {
    return;
  }

}
