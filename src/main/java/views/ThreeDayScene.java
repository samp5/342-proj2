package views;

import java.util.ArrayList;
import java.util.Collection;

import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import my_weather.HourlyPeriod;
import views.components.sidebar.Sidebar;

public class ThreeDayScene {
  Sidebar sidebar;
  VBox sidebarBox;
  VBox mainView;
  HBox sceneBox;
  Scene scene;

  public ThreeDayScene(ArrayList<HourlyPeriod> forecast) {
    initComponents();
    styleComponents();
  }

  private void initComponents() {
    sidebarBox = new VBox(); // mostly ignored for now
    mainView = new VBox();
    sceneBox = new HBox(sidebarBox, mainView);
    scene = new Scene(sceneBox, 1440, 1024);
  }


  public Scene getScene() {
    return scene;
  }
  
  public void setSidebar(Sidebar sidebar) {
    this.sidebar = sidebar;
  }

  public void setActiveScene() {
    this.sidebarBox.getChildren().add(sidebar.component());
  }

  private void styleComponents() {
    // CONTAINERS
    // - the sidebar
    sidebarBox.setMinWidth(256);
    sidebarBox.setStyle("-fx-background-color: #D9D9D9");
    // - main view
    mainView.setMinWidth(1440 - 256);
    mainView.setStyle("-fx-background-color: #FFFFFF");
    mainView.setPadding(new Insets(20));

  }
}
