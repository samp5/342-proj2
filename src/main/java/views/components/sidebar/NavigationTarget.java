package views.components.sidebar;

import javafx.geometry.Insets;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import views.DayScene;

public class NavigationTarget {
  public String displayName;
  public DayScene targetScene;

  public NavigationTarget(DayScene target, String display) {
    this.displayName = display;
    this.targetScene = target;
  }

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
