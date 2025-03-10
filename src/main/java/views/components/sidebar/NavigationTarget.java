package views.components.sidebar;

import javafx.geometry.Insets;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import views.DayScene;

/**
 * A {@code Scene} and name pair for use with a {@code Sidebar}.
 * Allows for navigation between scenes.
 */
public class NavigationTarget {
  public String displayName;
  public DayScene targetScene;

  /**
   * create a new {@code NavigationTarget} for use with a {@code Sidebar}
   *
   * @param target a {@code DayScene} to set as the scene when clicked
   * @param display the display name of the button
   */
  public NavigationTarget(DayScene target, String display) {
    this.displayName = display;
    this.targetScene = target;
  }

  /**
   * create a clickable component for scene switching
   *
   * @return a {@code VBox} as the component
   */
  public VBox component() {
    Text label = new Text(displayName);
    label.getStyleClass().add("navigation-target");
    label.setOnMouseEntered(e -> {
      label.setUnderline(true);
    });
    label.setOnMouseExited(e -> {
      label.setUnderline(false);
    });
    label.setOnMouseClicked(e -> {
      label.fireEvent(new NavigationEvent(this.targetScene));
    });
    VBox root = new VBox(label);
    root.setPadding(new Insets(10));
    return root;
  }
}
