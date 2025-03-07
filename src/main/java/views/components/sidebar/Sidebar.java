package views.components.sidebar;

import java.util.ArrayList;
import java.util.Collection;
import java.util.stream.Collectors;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.util.Pair;
import views.DayScene;
import views.components.events.LocationChangeEvent;

public class Sidebar {
  // tracks lat/lon validity
  boolean latValid = true;
  boolean lonValid = true;

  // header
  Header header;

  // sections
  ArrayList<Section> sections;

  public class Header {
    static final int titleFontSize = 40;
    VBox container;

    // text fields
    Text title;

    VBox coordInputBox;
    // text input
    HBox latBox, lonBox;
    Text latLabel, lonLabel;
    TextField latInput, lonInput;

    public Header() {

      buildTextInput();

      title = new Text(getTitle());
      styleTitle();

      container = new VBox(title, latBox, lonBox);
      container.setPadding(new Insets(10, 0, 10, 10));
    }

    public VBox component() {
      return this.container;
    }

    public void styleTitle() {
      title.getStyleClass().add("side-bar-header");
    }

    private void setInputOnAction() {
      latInput.textProperty().addListener((observable, oldvalue, newvalue) -> {
        latValid = inputHighlighter(latInput, newvalue);
      });

      latInput.setOnAction(e -> {
        latValid = inputHighlighter(latInput, latInput.getText());

        if (latValid && lonValid) {
          latInput.fireEvent(new LocationChangeEvent(Double.parseDouble(latInput.getText()), Double.parseDouble(lonInput.getText())));
        }
      });

      latInput.focusedProperty().addListener((observable, oldState, newState) -> {
        if (newState) return;

        latValid = inputHighlighter(latInput, latInput.getText());

        if (latValid && lonValid) {
          latInput.fireEvent(new LocationChangeEvent(Double.parseDouble(latInput.getText()), Double.parseDouble(lonInput.getText())));
        }
      });

      lonInput.textProperty().addListener((observable, oldvalue, newvalue) -> {
        lonValid = inputHighlighter(lonInput, newvalue);
      });

      lonInput.setOnAction(e -> {
        lonValid = inputHighlighter(lonInput, lonInput.getText());

        if (latValid && lonValid) {
          lonInput.fireEvent(new LocationChangeEvent(Double.parseDouble(latInput.getText()), Double.parseDouble(lonInput.getText())));
        }
      });
    }

    private boolean inputHighlighter(TextField tf, String newVal) {
      try {
        Double.parseDouble(newVal);
      } catch (NumberFormatException numException) {
        tf.getStyleClass().removeIf(s -> s.equals("coord-input"));
        tf.getStyleClass().add("invalid-input");
        return false;
      }
      tf.getStyleClass().removeIf(s -> s.equals("invalid-input"));
      tf.getStyleClass().add("coord-input");
      return true;
    }

    private void buildTextInput() {
      latInput = new TextField("41.8781");
      lonInput = new TextField("-87.6298");

      setInputOnAction();

      latLabel = new Text("lat:");
      lonLabel = new Text("lon:");

      latInput.getStyleClass().add("coord-input");
      lonInput.getStyleClass().add("coord-input");

      latLabel.getStyleClass().addAll("coord-label");
      lonLabel.getStyleClass().addAll("coord-label");

      latBox = new HBox(latLabel, latInput);
      lonBox = new HBox(lonLabel, lonInput);

      latBox.setPadding(new Insets(0, 0, 0, 10));
      lonBox.setPadding(new Insets(0, 0, 0, 10));

      latBox.setAlignment(Pos.CENTER_LEFT);
      lonBox.setAlignment(Pos.CENTER_LEFT);

      coordInputBox = new VBox(latBox, lonBox);
    }
  }

  // main vbox
  VBox container;
  String title = "Chicago, IL";

  public <T extends Collection<Section>> Sidebar(T sections) {
    this.sections = new ArrayList<>(sections);
    this.header = new Header();
    container = new VBox(header.component());
    container.getChildren()
        .addAll(sections.stream().map(section -> section.component()).collect(Collectors.toList()));
  }

  @SafeVarargs
  public static Sidebar fromScenes(Pair<String, DayScene>... namedScenes) {
    ArrayList<Section> sections = new ArrayList<>();
    ArrayList<NavigationTarget> forecastNavTargets = new ArrayList<>();

    for (Pair<String, DayScene> namedScene : namedScenes) {
      forecastNavTargets.add(new NavigationTarget(namedScene.getValue(), namedScene.getKey()));
    }

    Section forecasts = new Section("Forecast", forecastNavTargets);
    sections.add(forecasts);

    Sidebar sb = new Sidebar(sections);
    for (Pair<String, DayScene> namedScene : namedScenes) {
      namedScene.getValue().setSidebar(sb);
    }

    return sb;
  }

  public void addSection(Section section) {
    this.sections.add(section);
  }

  public void setTitle(String title) {
    this.title = title;
  }

  public String getTitle() {
    return this.title;
  }

  public VBox component() {
    return this.container;
  }

  public void recievedInvalidLocation() {
    header.lonInput.getStyleClass().removeIf(s -> s.equals("coord-input"));
    header.lonInput.getStyleClass().add("invalid-input");
    header.latInput.getStyleClass().removeIf(s -> s.equals("coord-input"));
    header.latInput.getStyleClass().add("invalid-input");
  }

  public void recievedValidLocation() {
    header.lonInput.getStyleClass().removeIf(s -> s.equals("invalid-input"));
    header.lonInput.getStyleClass().add("coord-input");
    header.latInput.getStyleClass().removeIf(s -> s.equals("invalid-input"));
    header.latInput.getStyleClass().add("coord-input");
  }

}
