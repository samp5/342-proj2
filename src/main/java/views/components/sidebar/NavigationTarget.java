package views.components.sidebar;

import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import views.DayScene;
import views.components.events.NavigationEvent;
import views.util.TextUtils;

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
    root.getStyleClass().add("navigation-target-box");
    root.setMaxWidth(TextUtils.computeTextWidth(new Font(20), this.displayName, 0));
    return root;
  }
}
