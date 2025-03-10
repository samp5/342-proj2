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
import settings.Settings;
import views.DayScene;
import views.components.events.LocationChangeEvent;
import views.util.NotificationBuilder;
import views.util.NotificationType;

/**
 * A sidebar meant for navigation between {@code Scene}s.
 * When calling the {@code Sidebar.fromScenes()} method, creates a sidebar which
 * navigates
 * between any of the given scenes.
 * Also allows for input and changing of the app's current location for weather
 * readings.
 */
public class Sidebar {
  // tracks lat/lon validity
  boolean latValid = true;
  boolean lonValid = true;

  // header
  Header header;

  // City searcher
  //
  CitySearch search;

  // sections
  ArrayList<Section> sections;

  /**
   * a {@code Header} for the {@code Sidebar}
   */
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

    /**
     * get the {@code VBox} component for the {@code Header}
     */
    public VBox component() {
      return this.container;
    }

    /**
     * style the title for the {@code Header}
     */
    public void styleTitle() {
      title.getStyleClass().add("side-bar-header");
      title.setWrappingWidth(256);
    }

    /**
     * add listeners for typing in input boxes to style invalid inputs or send
     * location changes
     */
    private void setInputOnAction() {
      latInput.textProperty().addListener((observable, oldvalue, newvalue) -> {
        latValid = inputHighlighter(latInput, newvalue);
      });

      latInput.setOnAction(e -> {
        latValid = inputHighlighter(latInput, latInput.getText());

        if (latValid && lonValid) {
          latInput.fireEvent(
              new LocationChangeEvent(Double.parseDouble(latInput.getText()),
                  Double.parseDouble(lonInput.getText())));
        }
      });

      lonInput.textProperty().addListener((observable, oldvalue, newvalue) -> {
        lonValid = inputHighlighter(lonInput, newvalue);
      });

      lonInput.setOnAction(e -> {
        lonValid = inputHighlighter(lonInput, lonInput.getText());

        if (latValid && lonValid) {
          lonInput.fireEvent(
              new LocationChangeEvent(Double.parseDouble(latInput.getText()),
                  Double.parseDouble(lonInput.getText())));
        }
      });
    }

    /**
     * highlight a {@code TextField} based on its input. invalid input is
     * highlighted red
     *
     * @param tf     the {@code TextField} to check
     * @param newVal the new string value
     * @return {@code true} if input is valid, {@code false} otherwise
     */
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

    /**
     * create text inputs to input latitude and longitude
     */
    private void buildTextInput() {
      double[] location = Settings.getLastLoc();
      latInput = new TextField(Double.toString(location[0]));
      lonInput = new TextField(Double.toString(location[1]));

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
  String title;

  /**
   * create a new {@code Sidebar} with given {@code Section}s
   *
   * @param sections the sections to add to the {@code Sidebar}
   */
  public <T extends Collection<Section>> Sidebar(T sections) {
    this.sections = new ArrayList<>(sections);
    this.header = new Header();
    container = new VBox(header.component());
    container.getChildren()
        .addAll(sections.stream().map(section -> section.component()).collect(Collectors.toList()));
    container.getChildren().add(new CitySearch().component());
  }

  /**
   * create a new {@code Sidebar} with navigation targets pre-made for the given
   * {@code DayScene}s
   *
   * @param namedScenes an amount of {@code Pair} scenes. given in the format of
   *                    {{@code String} display name, {@code DayScene} scene}
   */
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

  /**
   * add a new {@code Section} to the {@code Sidebar}
   *
   * @param section the new {@code Section} to add
   */
  public void addSection(Section section) {
    this.sections.add(section);
  }

  /**
   * set the title of the sidebar
   *
   * @param title the {@code String} title to set
   */
  public void setTitle(String title) {
    this.title = title;
    this.header.title.setText(this.title);
  }

  /**
   * get the title of the {@code Sidebar}
   *
   * @return the title of the {@code Sidebar}
   */
  public String getTitle() {
    return this.title;
  }

  /**
   * get the component for the {@code Sidebar}
   *
   * @return the component for the {@code Sidebar}
   */
  public VBox component() {
    return this.container;
  }

  /**
   * create and fire a notification for recieving an invalid location on the
   * latitude, longitude inputs.
   * set the style of the inputs to invalid
   */
  public void recievedInvalidLocation() {

    header.lonInput.getStyleClass().removeIf(s -> s.equals("coord-input"));
    header.lonInput.getStyleClass().add("invalid-input");
    header.latInput.getStyleClass().removeIf(s -> s.equals("coord-input"));
    header.latInput.getStyleClass().add("invalid-input");
  }

  /**
   * create and fire a notification for recieving a valid location on the
   * latitude, longitude inputs.
   * set the style of the inputs to valid
   */
  public void recievedValidLocation() {

    new NotificationBuilder("Changed location to " + this.title).ofType(NotificationType.Info)
        .fire(this.container);

    header.lonInput.getStyleClass().removeIf(s -> s.equals("invalid-input"));
    header.lonInput.getStyleClass().add("coord-input");
    header.latInput.getStyleClass().removeIf(s -> s.equals("invalid-input"));
    header.latInput.getStyleClass().add("coord-input");
  }

}
