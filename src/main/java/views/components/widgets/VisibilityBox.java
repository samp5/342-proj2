package views.components.widgets;

import javafx.geometry.Insets;
import javafx.scene.control.TextField;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import views.util.SVGHelper;
import views.util.TextUtils;

/**
 * A {@code SmallBox} used for displaying information about the current
 * visibility.
 * Visibility is given in miles.
 */
public class VisibilityBox extends SmallBox {
  double visibility;

  // components
  TextField subtitle;
  Region backgroundRegion, pupilRegion, squintRegionTop, squintRegionBottom, eyeRegion;

  /**
   * create a new {@code VisibilityBox} for a given visibility range
   *
   * @param visibility the visibility range in meters
   */
  public VisibilityBox(double visibility) {
    this.visibility = mToMi(visibility);
  }

  /**
   * get the {@code VBox} component
   *
   * @return the {@code VBox} component
   */
  public VBox component() {
    // set the title
    titleText.setText("Visibility");

    // create the SVG
    assembleSVG();

    // make the subtitle
    subtitle = TextUtils.staticTextField(String.format("Visibility up to %.2f miles", visibility));
    subtitle.setPadding(new Insets(10));
    subtitle.getStyleClass().add("font-reg");

    // populate the component
    svgStack.setPadding(new Insets(15, 0, 0, 0));
    comp.getChildren().setAll(titleText, svgStack, subtitle);
    return comp;
  }

  /**
   * builds the SVG eye based on visibility
   */
  protected void assembleSVG() {
    // create all the SVGs from a path
    backgroundRegion = newSVG("M 0 5 A 1 1 0 0 0 10 5 A 1 1 0 0 0 0 5");
    pupilRegion = newSVG(
        "M 0 5 A 1 1 0 0 0 10 5 A 1 1 0 0 0 0 5 M 0.1 5 A 1 1 0 0 1 9.9 5 A 1 1 0 0 1 0.1 5 M 5 4 A 1 1 0 0 0 5 6 A 1 1 0 0 0 5 4");
    squintRegionTop = newSVG("M 0 0 L 0 1 L 1 1 L 1 0");
    squintRegionBottom = newSVG("M 0 0 L 0 1 L 1 1 L 1 0");
    eyeRegion = newSVG(
        "M 0 5 A 1 1 0 0 0 10 5 A 1 1 0 0 0 0 5 M 0.5 5 C 1.25 4 2.5 3 5 3 C 7.5 3 8.75 4 9.5 5 C 8.75 6 7.75 7 5 7 C 2.5 7 1.25 6 0.5 5 z");

    // add style classes
    backgroundRegion.getStyleClass().add("visibility-background");
    pupilRegion.getStyleClass().add("visibility-pupil");
    squintRegionTop.getStyleClass().add("visibility-squint");
    squintRegionBottom.getStyleClass().add("visibility-squint");
    eyeRegion.getStyleClass().add("visibility-eye");

    // set squint region sizes
    double squintPercent = getSquintPercent();
    double height = SIZE * .2 * squintPercent;
    double halfSize = SIZE / 2;
    double qrtrSize = SIZE / 4;
    SVGHelper.setSize(squintRegionTop, SIZE * .9, height);
    SVGHelper.setSize(squintRegionBottom, SIZE * .9, height);
    squintRegionTop.setTranslateY(.4 * (qrtrSize * squintPercent - halfSize));
    squintRegionBottom.setTranslateY(.4 * -(qrtrSize * squintPercent - halfSize));

    // create the combined svg by stacking the components
    svgStack.getChildren().setAll(backgroundRegion, pupilRegion, squintRegionBottom, squintRegionTop, eyeRegion);
  }

  /**
   * convert a distance in meters to a distance in miles
   *
   * @param m distance in meters
   * @return distance in miles
   */
  private double mToMi(double m) {
    return m * 0.621371 / 1000;
  }

  static final double MAX_SQUINT_DIST = 0;
  static final double MIN_SQUINT_DIST = 10;

  /**
   * get the amount of squint based on the visibility
   */
  private double getSquintPercent() {
    // dont have negative squint
    if (visibility >= MIN_SQUINT_DIST) {
      return 0;
    }

    return (MIN_SQUINT_DIST - visibility) / (MIN_SQUINT_DIST - MAX_SQUINT_DIST);
  }
}
