package views.components.sidebar;

import java.util.ArrayList;
import java.util.Collection;
import java.util.stream.Collectors;

import javafx.geometry.Insets;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;

/**
 * A portion of the {@code Sidebar}.
 * Combines {@code NavigationTarget}s into a single section for simple
 * navigation,
 * along with a title and header.
 */
public class Section {
  String title;
  ArrayList<NavigationTarget> navTargerts;

  VBox box;

  /**
   * create a new {@code Section} with a title and navigation targets
   *
   * @param title       the title of the section
   * @param nav_targets the navigation targets that can be clicked
   */
  public <T extends Collection<NavigationTarget>> Section(String title, T nav_targets) {
    this.title = title;
    this.navTargerts = new ArrayList<>(nav_targets);

    Text section_header = new Text(this.title);
    section_header.getStyleClass().add("section-header");
    box = new VBox(section_header);
    box.getChildren()
        .addAll(this.navTargerts.stream().map(e -> e.component()).collect(Collectors.toList()));
    box.setPadding(new Insets(0, 0, 0, 10));
    box.setSpacing(10);
  }

  /**
   * get the component for this {@code Section}
   *
   * @return the component for this {@code Section}
   */
  public VBox component() {
    if (box == null) {
      return new VBox();
    } else {
      return box;
    }
  }
}
