package views.components;

import javafx.geometry.Insets;
import javafx.scene.control.TextField;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.SVGPath;
import views.util.TextUtils;

public class CompassBox {
  private static final double SIZE = 200;
  String windSpeed, windDir;

  // components
  TextField compassTitle, compassText;
  StackPane svgStack;
  Region bodyRegion;
  SVGPath compassBody;
  Region markRegion;
  SVGPath compassMarks;
  Region northRegion;
  SVGPath compassNorth;
  Region needleRegion;
  SVGPath compassNeedle;

  public CompassBox(String windSpeed, String windDir) {
    this.windDir = windDir;
    this.windSpeed = windSpeed;
  }

  public VBox component() {
    VBox comp = new VBox();
    comp.getStyleClass().addAll("small-graph", "compass-box");

    compassTitle = TextUtils.staticTextField("Wind Direction");
    compassTitle.setPadding(new Insets(20, 0, 10, 0));
    compassTitle.getStyleClass().add("font-bold");
    compassText = TextUtils.staticTextField(windSpeed + " " + windDir);
    compassText.setPadding(new Insets(10, 0, 0, 0));
    compassText.getStyleClass().add("font-reg");

    assembleSVG();
    comp.getChildren().setAll(compassTitle, svgStack, compassText);

    return comp;
  }

  private void assembleSVG() {
    compassBody = new SVGPath();
    compassBody.setContent("M 0 5 A 1 1 0 0 0 10 5 A 1 1 0 0 0 0 5");
    bodyRegion = new Region();
    bodyRegion.setShape(compassBody);

    compassMarks = new SVGPath();
    compassMarks.setContent("M 0 5 A 1 1 0 0 0 10 5 A 1 1 0 0 0 0 5 M 5 1 l 0 1 M 1 5 l 1 0 M 9 5 l -1 0 M 5 9 l 0 -1 M 7.12132 7.12132 L 7.82843 7.82843 M 2.87868 2.87868 L 2.17157 2.17157 M 2.17157 7.82843 L 2.87868 7.12132 M 7.12132 2.87868 L 7.82843 2.17157 M 7.77164 6.14805 L 8.69552 6.53073 M 2.22836 3.85195 L 1.30448 3.46927 M 2.22836 3.85195 L 1.30448 3.46927 M 2.22836 6.14805 L 1.30448 6.53073 M 7.77164 3.85195 L 8.69552 3.46927 M 6.53073 8.69552 L 6.14805 7.77164 M 3.46927 1.30448 L 3.85195 2.22836 M 3.46927 8.69552 L 3.85195 7.77164 M 6.53073 1.30448 L 6.14805 2.22836");
    markRegion = new Region();
    markRegion.setShape(compassMarks);

    compassNorth = new SVGPath();
    compassNorth.setContent("M 0 5 A 1 1 0 0 0 10 5 A 1 1 0 0 0 0 5 M 5 1 l 0 1");
    northRegion = new Region();
    northRegion.setShape(compassNorth);

    compassNeedle = new SVGPath();
    compassNeedle.setContent("M 4.5 5 C 4.333 6.5 5.666 6.5 5.5 5 L 5 2 z");
    needleRegion = new Region();
    needleRegion.setShape(compassNeedle);

    setRegionSize(bodyRegion);
    bodyRegion.getStyleClass().add("compass-body");
    setRegionSize(markRegion);
    markRegion.getStyleClass().add("compass-marks");
    setRegionSize(northRegion);
    northRegion.getStyleClass().add("compass-north");
    double needleW = CompassBox.SIZE / 10.;
    double needleH = CompassBox.SIZE * .42;
    needleRegion.setMinSize(needleW, needleH);
    needleRegion.setPrefSize(needleW, needleH);
    needleRegion.setMaxSize(needleW, needleH);
    needleRegion.getStyleClass().add("compass-needle");
    needleRegion.setRotate(getRotation());

    svgStack = new StackPane(markRegion, northRegion, bodyRegion, needleRegion);
    svgStack.setPadding(new Insets(10, 0, 0, 0));
  }

  private void setRegionSize(Region r) {
    double size = CompassBox.SIZE * .8;
    r.setMinSize(size, size);
    r.setPrefSize(size, size);
    r.setMaxSize(size, size);
  }

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
