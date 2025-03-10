package views;

import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.ScrollPane.ScrollBarPolicy;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import views.components.sidebar.Sidebar;

/**
 * A generic and abstract class for all scenes to extend.
 * Shares a sidebar will all other scenes, transfered automatically if given during sidebar initialization.
 * Has functionality to void focus and is otherwise a base scene.
 */
public abstract class DayScene {
  protected Sidebar sidebar;
  protected ScrollPane mainScrollable;
  protected VBox mainView, sidebarBox;
  protected HBox sceneBox;
  protected Scene scene;
  private Canvas focusVoid = new Canvas();

  /**
   * initialize all components. most components will be added to or modified later
   * during initialization
   */
  protected void initComponents() {
    sidebarBox = new VBox();
    mainView = new VBox();
    mainScrollable = new ScrollPane(mainView);

    mainScrollable.setHbarPolicy(ScrollBarPolicy.NEVER);
    mainScrollable.setFitToWidth(true);
    mainScrollable.setVbarPolicy(ScrollBarPolicy.AS_NEEDED);

    sceneBox = new HBox(sidebarBox, mainScrollable, focusVoid);
    scene = new Scene(sceneBox, 1440, 900);

    // listen for click events
    scene.setOnMouseClicked(event -> {
      voidFocus();
    });

    // styling
    // - the sidebar
    sidebarBox.setMinWidth(256);
    sidebarBox.getStyleClass().add("sidebar");

    // - main view
    mainView.setMinWidth(1440 - 256 - 28);
    mainView.setMinHeight(900);
    mainView.getStyleClass().add("main-view");
    mainView.setPadding(new Insets(20));
    mainView.setSpacing(20);

    // - scrollable
    mainScrollable.setMinWidth(1440 - 256);

    // add always used stylesheets
    scene.getStylesheets().add("css/baseScene.css");
    scene.getStylesheets().add("css/sidebar.css");
    scene.getStylesheets().add("css/sidebarHeader.css");
    scene.getStylesheets().add("css/notifications.css");
  }

  /**
   * removes focus from the scene
   */
  protected void voidFocus() {
    focusVoid.requestFocus();
  }

  /**
   * @return a {@code Scene} with the current weather state set by
   *         {@code applyForecast}
   */
  public Scene getScene() {
    return scene;
  }

  /**
   * sets the stored sidebar to the parameter
   *
   * @param sidebar a sidebar object, shared with other {@code DayScene}s
   */
  public void setSidebar(Sidebar sidebar) {
    this.sidebar = sidebar;
  }

  /**
   * sets this {@code DayScene} to the active scene.
   * always call {@code super()}, as this is needed for the sidebar to function
   * properly
   */
  public void setActiveScene() {
    this.sidebarBox.getChildren().setAll(sidebar.component());
    voidFocus();
  }

  /**
   * updates the {@code DayScene} with a new set of data
   *
   * @param forecast the forecast to update from
   */
  protected abstract void applyForecast();
}
