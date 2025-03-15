package views;

import java.io.IOException;
import java.util.ArrayList;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.css.CssParser;
import javafx.css.Rule;
import javafx.css.Stylesheet;
import javafx.css.converter.ColorConverter;
import javafx.geometry.Insets;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.Stop;
import javafx.util.Duration;
import settings.Settings;

/**
 * A loading scene which should not be seen for particularly long.
 * This is used as an in between during certain periods of loading.
 */
public class LoadingScene extends DayScene {
  // scene blocking
  HBox headerContainer, graphContainer;
  Pane forecastBox;
  HBox smallCharts;

  HBox sidebarHeader;
  HBox sidebarInput;
  HBox sidebarSection;
  HBox sidebarSearch;
  VBox fakeSidebar;

  ArrayList<Timeline> animations;

  /**
   * create a new loading scene
   */
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

    sidebarHeader = new HBox();
    sidebarInput = new HBox();
    sidebarSection = new HBox();
    sidebarSearch = new HBox();
    fakeSidebar = new VBox();
    fakeSidebar.getChildren().addAll(sidebarHeader, sidebarInput, sidebarSection, sidebarSearch);
    fakeSidebar.setPadding(new Insets(10));

    mainView.getChildren().addAll(headerContainer, forecastBox, graphContainer, smallCharts);
    sidebarBox.getChildren().setAll(fakeSidebar);

    animations = new ArrayList<>();

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

    sidebarHeader.getStyleClass().add("load-box");
    sidebarHeader.setMaxWidth(256);
    sidebarHeader.setMinHeight(100);

    sidebarInput.getStyleClass().add("load-box");
    sidebarInput.setMaxWidth(256);
    sidebarInput.setMinHeight(50);

    sidebarSection.getStyleClass().add("load-box");
    sidebarSection.setMaxWidth(256);
    sidebarSection.setMinHeight(100);

    sidebarSearch.getStyleClass().add("load-box");
    sidebarSearch.setMaxWidth(256);
    sidebarSearch.setMinHeight(20);
    fakeSidebar.setSpacing(20);

    animateAll();
  }

  /**
   * Animate all regions
   */
  private void animateAll() {
    for (Timeline animation : animations) {
      animation.stop();
    }
    animations.clear();

    Region[] boxes = {graphContainer, smallCharts, headerContainer, forecastBox};
    // get the colors for the animation from CSS
    Color[] colors = getAnimationColors();

    for (Region box : boxes) {
      animations.add(buildAnimation(box, colors[0], colors[1]));
    }

    Region[] sidebarBoxes = {sidebarHeader, sidebarInput, sidebarSection, sidebarSearch};

    for (Region box : sidebarBoxes) {
      animations.add(buildAnimation(box, colors[0], colors[1]));
    }

    for (Timeline animation : animations) {
      animation.play();
    }
  }

  /**
   * gets colors for the {@code animateAll()} method from the current theme's css
   * file
   *
   * @return an array of 4 {@code Colors} for animation use
   */
  @SuppressWarnings("unchecked")
  private Color[] getAnimationColors() {
    // init color list and name const
    Color[] colors = new Color[4];
    final String[] names = new String[] {
        "main-box-start-color",
        "main-box-end-color",
        "side-box-start-color",
        "side-box-end-color"
    };

    try {
      // create a CSS parser, and get the root rule
      ClassLoader cl = Thread.currentThread().getContextClassLoader();
      CssParser parser = new CssParser();
      Stylesheet css = parser.parse(cl.getResource(Settings.getThemeFile()));
      final Rule rootRule = css.getRules().get(0);

      // for each name, get the color
      for (int i = 0; i < 4; ++i) {
        final String name = names[i];
        colors[i] = rootRule.getDeclarations().stream()
            .filter(d -> d.getProperty().equals(name))
            .findFirst()
            .map(d -> ColorConverter.getInstance().convert(d.getParsedValue(), null))
            .get();
      }
    } catch (IOException e) {
      e.printStackTrace();
    }

    return colors;
  }

  /**
   * Build a "loading" animation for a given region
   */
  private Timeline buildAnimation(Region box, Color start, Color to) {

    Timeline timeline = new Timeline();

    for (double i = 0; i <= 1; i += 0.05) {
      double startX = i;
      double endX = i + 1; // Move gradient from left to right
      timeline.getKeyFrames().add(new KeyFrame(Duration.seconds(i + 0.5), e -> {

        box.setBackground(
            new Background(
                new BackgroundFill(
                    new LinearGradient(startX, 0, endX, 0, true, CycleMethod.NO_CYCLE,
                        new Stop(0, start),
                        new Stop(1, to)),
                    new CornerRadii(20), Insets.EMPTY)));
      }));
    }

    timeline.setCycleCount(Timeline.INDEFINITE);
    timeline.setAutoReverse(true);

    return timeline;
  }

  /**
   * A loading scene has no forecast to apply, thus is empty
   */
  @Override
  protected void applyForecast() {
    return;
  }

  @Override
  public void setTheme(String filename) {
    super.setTheme(filename);

    animateAll();
  }
}
