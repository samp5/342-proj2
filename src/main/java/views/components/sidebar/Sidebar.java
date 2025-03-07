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

public class Sidebar {

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
        double val;
        try {
          val = Double.parseDouble(newvalue);
        } catch (NumberFormatException numException) {
          latInput.getStyleClass().removeIf(s -> s.equals("coord-input"));
          latInput.getStyleClass().add("invalid-input");
          return;
        }
        latInput.getStyleClass().removeIf(s -> s.equals("invalid-input"));
        latInput.getStyleClass().add("coord-input");
      });

      latInput.setOnAction(e -> {
        String text = latInput.getText();
        double val;

        try {
          System.err.println("parsing " + text);
          val = Double.parseDouble(text);
        } catch (NumberFormatException numException) {
          latInput.getStyleClass().removeIf(s -> s.equals("coord-input"));
          latInput.getStyleClass().add("invalid-input");
          System.err.println("parsing failed added style");
          return;
        }

        System.err.println("parsing success removing invalid-input style");
        latInput.getStyleClass().removeIf(s -> s.equals("invalid-input"));
        latInput.getStyleClass().add("coord-input");
      });

      lonInput.textProperty().addListener((observable, oldvalue, newvalue) -> {
        double val;
        try {
          val = Double.parseDouble(newvalue);
        } catch (NumberFormatException numException) {
          lonInput.getStyleClass().removeIf(s -> s.equals("coord-input"));
          lonInput.getStyleClass().add("invalid-input");
          return;
        }
        lonInput.getStyleClass().removeIf(s -> s.equals("invalid-input"));
        lonInput.getStyleClass().add("coord-input");
      });

      lonInput.setOnAction(e -> {
        String text = lonInput.getText();
        double val;

        try {
          System.err.println("parsing " + text);
          val = Double.parseDouble(text);
        } catch (NumberFormatException numException) {
          lonInput.getStyleClass().removeIf(s -> s.equals("coord-input"));
          lonInput.getStyleClass().add("invalid-input");
          System.err.println("parsing failed added style");
          return;
        }
        lonInput.getStyleClass().removeIf(s -> s.equals("invalid-input"));
        lonInput.getStyleClass().add("coord-input");
        // TODO: Broadcast a ChangeLocation event
      });
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

}
