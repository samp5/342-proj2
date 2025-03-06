package views.components.sidebar;

import java.util.ArrayList;
import java.util.Collection;
import java.util.stream.Collectors;
import javafx.scene.layout.VBox;

public class Section {
  String title;
  ArrayList<NavigationTarget> navTargerts;

  VBox box;

  public <T extends Collection<NavigationTarget>> Section(String title, T nav_targets) {
    this.title = title;
    this.navTargerts = new ArrayList<>(nav_targets);
    box = new VBox();
    box.getChildren()
        .addAll(this.navTargerts.stream().map(e -> e.component()).collect(Collectors.toList()));
  }

  public VBox component() {
    if (box == null) {
      return new VBox();
    } else {
      return box;
    }
  }
}
