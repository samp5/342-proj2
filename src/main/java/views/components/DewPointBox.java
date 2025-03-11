package views.components;

import javafx.geometry.Insets;
import javafx.scene.control.TextField;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import views.util.TextUtils;
import views.util.UnitHandler;
import views.util.UnitHandler.TemperatureUnit;

public class DewPointBox extends SmallBox {
  double dewpointF;
  double dewpointC;

  Region meterRegion;
  Region fillRegion;
  
  public DewPointBox(double dewpoint) {
    this.dewpointC = dewpoint;
    this.dewpointF = dewpoint * 9 / 5 + 32;
  }

	public VBox component() {
    // set the title
    titleText.setText("Dew Point");

    // add sub title
    double temp;
    if (UnitHandler.getUnit() == TemperatureUnit.Celsius) temp = dewpointC;
    else temp = dewpointF;

    TextField subTitle = TextUtils.staticTextField("Dew at " + String.format("%.1f°", temp) + UnitHandler.getUnitChar());
    subTitle.getStyleClass().add("font-reg");

    // create svg, populate component
    assembleSVG();
    svgStack.setPadding(new Insets(15, 0, 0, 0));
    comp.getChildren().setAll(titleText, svgStack, subTitle);

    return comp;
	}

	protected void assembleSVG() {
    meterRegion = newSVG("M 0 5 A 1 1 0 0 0 10 5 A 1 1 0 0 0 0 5 M 5 1 L 6.666 5.25 C 8.333 10 1.666 10 3.333 5.25 z");
    fillRegion = newSVG("M 0 0 L 0 1 L 1 1 L 1 0 z");

    double percent = getHeightPercent();
    double height = SIZE * .8 * percent;
    double halfSize = SIZE / 2;
    setSize(fillRegion, SIZE * .4, height);
    fillRegion.setTranslateY((-halfSize * percent + halfSize) * .8);

    meterRegion.getStyleClass().add("dewpoint-body");
    fillRegion.getStyleClass().add("dewpoint-fill");

    svgStack.getChildren().setAll(fillRegion, meterRegion);
	}

  private double getHeightPercent() {
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
