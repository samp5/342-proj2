package views.components.widgets;

import javafx.geometry.Insets;
import javafx.scene.control.TextField;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import views.util.SVGHelper;
import views.util.TextUtils;
import views.util.UnitHandler;
import views.util.UnitHandler.TemperatureUnit;

/**
 * A {@code SmallBox} used for displaying information about the current dew point.
 * The graphic and data show a raindrop filled to a percent relative to standard dew point temp.
 * Dew point shown in current temperature unit.
 */
public class DewPointBox extends SmallBox {
  double dewpointF;
  double dewpointC;

  // components
  Region meterRegion;
  Region fillRegion;
  
  /**
   * create a new {@code DewPointBox} for a given dewpoint in °C
   *
   * @param windSpeed the {@code String} speed of the wind
   * @param windDir the 16-point compass direction of the wind
   */
  public DewPointBox(double dewpoint) {
    this.dewpointC = dewpoint;
    this.dewpointF = dewpoint * 9 / 5 + 32;
  }

  /**
   * get the {@code VBox} component
   *
   * @return the {@code VBox} component
   */
	public VBox component() {
    // set the title
    titleText.setText("Dew Point");

    // get the temp to use for subtitle
    double temp;
    if (UnitHandler.getUnit() == TemperatureUnit.Celsius) temp = dewpointC;
    else temp = dewpointF;

    // make the subtitle
    TextField subTitle = TextUtils.staticTextField("Dew at " + String.format("%.1f°", temp) + UnitHandler.getUnitChar());
    subTitle.getStyleClass().add("font-reg");

    // create svg, populate component
    assembleSVG();
    svgStack.setPadding(new Insets(15, 0, 0, 0));
    comp.getChildren().setAll(titleText, svgStack, subTitle);

    return comp;
	}

  /**
   * builds the SVG raindrop based on current dew point
   */
	protected void assembleSVG() {
    // create all the SVGs from a path
    meterRegion = newSVG("M 0 5 A 1 1 0 0 0 10 5 A 1 1 0 0 0 0 5 M 5 1 L 6.666 5.25 C 8.333 10 1.666 10 3.333 5.25 z");
    fillRegion = newSVG("M 0 0 L 0 1 L 1 1 L 1 0 z");

    // set raindrop fill to correct location and size
    double percent = getHeightPercent();
    double height = SIZE * .8 * percent;
    double halfSize = SIZE / 2;
    SVGHelper.setSize(fillRegion, SIZE * .4, height);
    fillRegion.setTranslateY((-halfSize * percent + halfSize) * .8);

    // add style classes
    meterRegion.getStyleClass().add("dewpoint-body");
    fillRegion.getStyleClass().add("dewpoint-fill");

    // create the combined svg by stacking the components
    svgStack.getChildren().setAll(fillRegion, meterRegion);
	}

  /**
   * calculate the raindrop fill height, in percent, based on the current dew point
   */
  private double getHeightPercent() {
    // change bounds based on unit.
    // though min happens to be the same.
    double min, max, point;
    min = -40;
    if (UnitHandler.getUnit() == TemperatureUnit.Celsius) {
      max = 35;
      point = dewpointC;
    } else {
      max = 95;
      point = dewpointF;
    }
    
    return (point - min) / (max - min);
  }
}
