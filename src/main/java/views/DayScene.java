package views;

import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import views.components.sidebar.Sidebar;

public abstract class DayScene {
  protected Sidebar sidebar;
  protected VBox sidebarBox;
  protected VBox mainView;
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
    sceneBox = new HBox(sidebarBox, mainView, focusVoid);
    scene = new Scene(sceneBox, 1440, 800);

    // styling
    // - the sidebar
    sidebarBox.setMinWidth(256);
    sidebarBox.setStyle("-fx-background-color: #D9D9D9");
    // - main view
    mainView.setMinWidth(1440 - 256);
    mainView.setStyle("-fx-background-color: #FFFFFF");
    mainView.setPadding(new Insets(20));

    scene.getStylesheets().add("css/baseScene.css");
    scene.getStylesheets().add("css/sidebar.css");
    scene.getStylesheets().add("css/sidebarHeader.css");
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
   * always call {@code super()}, as this is needed for the sidebar to function properly
   */
  public void setActiveScene() {
    this.sidebarBox.getChildren().setAll(sidebar.component());
    voidFocus();
  }
}
