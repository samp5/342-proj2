package views.components;

import javafx.geometry.Insets;
import javafx.scene.control.TextField;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.shape.SVGPath;
import views.util.TextUtils;

/**
 * A {@code SmallBox} used for displaying information about the current wind speed and direction.
 * The graphic and data has a 16-point compass resolution, with an included measure of wind speed.
 * Wind speeds are always shown in mph.
 */
public class CompassBox extends SmallBox {
  String windSpeed, windDir;

  // components
  TextField compassText;
  Region bodyRegion;
  SVGPath compassBody;
  Region markRegion;
  SVGPath compassMarks;
  Region northRegion;
  SVGPath compassNorth;
  Region needleRegion;
  SVGPath compassNeedle;

  /**
   * create a new {@code CompassBox} for a given windspeed and direction
   *
   * @param windSpeed the {@code String} speed of the wind
   * @param windDir the 16-point compass direction of the wind
   */
  public CompassBox(String windSpeed, String windDir) {
    this.windDir = windDir;
    this.windSpeed = windSpeed;
  }

  /**
   * get the {@code VBox} component
   *
   * @return the {@code VBox} component
   */
  public VBox component() {
    // set the title
    titleText.setText("Wind Direction");

    // add reading text
    compassText = TextUtils.staticTextField(windSpeed + " " + windDir);
    compassText.setPadding(new Insets(10, 0, 0, 0));
    compassText.getStyleClass().add("font-reg");

    // create svg, populate component
    assembleSVG();
    comp.getChildren().setAll(titleText, svgStack, compassText);

    return comp;
  }

  /**
   * builds the SVG compass based on current wind direction
   */
  protected void assembleSVG() {
    // create all the SVGs from a path
    bodyRegion = newSVG("M 0 5 A 1 1 0 0 0 10 5 A 1 1 0 0 0 0 5");
    markRegion = newSVG("M 0 5 A 1 1 0 0 0 10 5 A 1 1 0 0 0 0 5 M 5 1 l 0 1 M 1 5 l 1 0 M 9 5 l -1 0 M 5 9 l 0 -1 M 7.12132 7.12132 L 7.82843 7.82843 M 2.87868 2.87868 L 2.17157 2.17157 M 2.17157 7.82843 L 2.87868 7.12132 M 7.12132 2.87868 L 7.82843 2.17157 M 7.77164 6.14805 L 8.69552 6.53073 M 2.22836 3.85195 L 1.30448 3.46927 M 2.22836 3.85195 L 1.30448 3.46927 M 2.22836 6.14805 L 1.30448 6.53073 M 7.77164 3.85195 L 8.69552 3.46927 M 6.53073 8.69552 L 6.14805 7.77164 M 3.46927 1.30448 L 3.85195 2.22836 M 3.46927 8.69552 L 3.85195 7.77164 M 6.53073 1.30448 L 6.14805 2.22836");
    northRegion = newSVG("M 0 5 A 1 1 0 0 0 10 5 A 1 1 0 0 0 0 5 M 5 1 l 0 1");
    needleRegion = newSVG("M 4.5 5 C 4.333 6.5 5.666 6.5 5.5 5 L 5 2 z");

    // resize the needle
    double needleW = CompassBox.SIZE / 8.;
    double needleH = CompassBox.SIZE / 1.9;
    setSize(needleRegion, needleW, needleH);
    needleRegion.setRotate(getRotation());

    // add style classes
    bodyRegion.getStyleClass().add("compass-body");
    markRegion.getStyleClass().add("compass-marks");
    northRegion.getStyleClass().add("compass-north");
    needleRegion.getStyleClass().add("compass-needle");

    // create the combined svg by stacking the components
    svgStack.getChildren().setAll(markRegion, northRegion, bodyRegion, needleRegion);
    svgStack.setPadding(new Insets(10, 0, 0, 0));
  }

  /**
   * get the rotation in degrees from stored wind direction
   *
   * @return rotation in degrees
   */
  private double getRotation() {
    switch (this.windDir) {
      case "N":
        return 0;
      case "NNE":
        return 22.5;
      case "NE":
        return 45;
      case "ENE":
        return 67.5;
      case "E":
        return 90;
      case "ESE":
        return 112.5;
      case "SE":
        return 135;
      case "SSE":
        return 157.5;
      case "S":
        return 180;
      case "SSW":
        return 202.5;
      case "SW":
        return 225;
      case "WSW":
        return 247.5;
      case "W":
        return 270;
      case "WNW":
        return 292.5;
      case "NW":
        return 315;
      case "NNW":
        return 337.5;
      default:
        return 0;
    }
  }
}
