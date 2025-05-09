package views.components.sidebar;

import java.util.ArrayList;
import java.util.Collection;
import java.util.stream.Collectors;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.util.Pair;
import settings.Settings;
import views.DayScene;
import views.components.events.LocationChangeEvent;
import views.components.events.ThemeChangeEvent;
import views.util.NotificationBuilder;
import views.util.NotificationType;
import views.util.SVGHelper;

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
  CitySearch search;

  // sections
  ArrayList<Section> sections;

  // theme change button
  HBox themeButtonBox;
  Button themeButton;
  static final Region sunBtn, moonBtn;

  static {
    sunBtn = SVGHelper.newSVG(
        "M 2 0 A 1 1 0 0 0 -2 0 A 1 1 0 0 0 2 0 M 0.26168 4.99315 L -0.26168 4.99315 L -0.1887 3.60061 L 0.1887 3.60061 z M 0.26168 -4.99315 L -0.26168 -4.99315 L -0.1887 -3.60061 L 0.1887 -3.60061 z M 4.99315 0.26168 L 4.99315 -0.26168 L 3.60061 -0.1887 L 3.60061 0.1887 z M -4.99315 0.26168 L -4.99315 -0.26168 L -3.60061 -0.1887 L -3.60061 0.1887 z M 3.34565 3.71572 L 3.71572 3.34565 L 2.67945 2.41258 L 2.41258 2.67945 z M -3.34565 3.71572 L -3.71572 3.34565 L -2.67945 2.41258 L -2.41258 2.67945 z M 3.34565 -3.71572 L 3.71572 -3.34565 L 2.67945 -2.41258 L 2.41258 -2.67945 z M -3.34565 -3.71572 L -3.71572 -3.34565 L -2.67945 -2.41258 L -2.41258 -2.67945 z",
        40);
    sunBtn.getStyleClass().add("sun-btn");
    moonBtn = SVGHelper.newSVG(
        "M 5 0 C 5 3 3 5 0 5 C -3 5 -5 3 -5 0 C -5 -3 -3 -5 0 -5 C -3.25 -3.95 -3.05 0 -1.5 1.5 C 0 3.05 3.95 3.25 5 0",
        40);
    moonBtn.getStyleClass().add("moon-btn");
  }

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
                  Double.parseDouble(lonInput.getText()), null));
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
                  Double.parseDouble(lonInput.getText()), null));
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
      tf.getStyleClass().remove("invalid-input");
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

      latBox.getStyleClass().addAll("coord-box");
      lonBox.getStyleClass().addAll("coord-box");

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

    themeButton = new Button();
    themeButtonBox = new HBox(themeButton);
    themeButtonBox.getStyleClass().add("theme-button-box");

    container.getChildren().addAll(new CitySearch().component(), themeButtonBox);

    setThemeButton();
  }

  /**
   * set the theme button to the correct button and functionality based on the
   * current theme
   */
  public void setThemeButton() {
    switch (Settings.getTheme()) {
      case Light:
        // set theme button
        themeButtonBox.getChildren().setAll(moonBtn);
        // fire theme change event
        moonBtn.setOnMouseClicked(e -> {
          moonBtn.fireEvent(new ThemeChangeEvent("css/themes/dark.css"));
        });
        break;
      case Dark:
        // set theme button
        themeButtonBox.getChildren().setAll(sunBtn);
        // fire theme change event
        sunBtn.setOnMouseClicked(e -> {
          sunBtn.fireEvent(new ThemeChangeEvent("css/themes/light.css"));
        });
        break;
      case Kanagawa:
        // set theme button
        themeButtonBox.getChildren().setAll(sunBtn);
        // fire theme change event
        sunBtn.setOnMouseClicked(e -> {
          sunBtn.fireEvent(new ThemeChangeEvent("css/themes/light.css"));
        });
        break;
      default:
        break;
    }
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

    header.lonInput.getStyleClass().remove("coord-input");
    header.lonInput.getStyleClass().add("invalid-input");
    header.latInput.getStyleClass().remove("coord-input");
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

    header.lonInput.getStyleClass().remove("invalid-input");
    header.lonInput.getStyleClass().add("coord-input");
    header.latInput.getStyleClass().remove("invalid-input");
    header.latInput.getStyleClass().add("coord-input");
  }

  /**
   * set the displaying latitude and longitude displayed on the sidebar
   */
  public void setLatLon(double lat, double lon) {
    this.header.latInput.setText(String.valueOf(lat));
    this.header.lonInput.setText(String.valueOf(lon));
  }

}
