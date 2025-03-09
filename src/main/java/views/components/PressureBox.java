package views.components;

import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;

public class PressureBox extends SmallBox {
  double pressure;

  // components
  Region bodyRegion, meterRegion, needleRegion, labelRegion;
  Label pressureReading, pressureUnit;

  public PressureBox(double pressure) {
    this.pressure = pascalToMmHg(pressure);
  }

  public VBox component() {
    titleText.setText("Air Pressure");

    assembleSVG();
    svgStack.setPadding(new Insets(15, 0, 0, 0));
    comp.getChildren().setAll(titleText, svgStack);

    return comp;
  }

  protected void assembleSVG() {
    bodyRegion = newSVG("M 5 0 A 1 1 0 0 0 5 10 A 1 1 0 0 0 5 0 M 1 4.5 A 1.1 1 0 0 1 9 4.5 A 0.1 0.1 0 0 1 8 4.5 A 1 0.9 0 0 0 2 4.5 A 0.1 0.1 0 0 1 1 4.5");
    meterRegion = newSVG("M 5 0 A 1 1 0 0 0 5 10 A 1 1 0 0 0 5 0 M 5 1 l 0 1 M 1 5 l 1 0  M 9 5 l -1 0  M 2.87868 2.87868 L 2.17157 2.17157  M 7.12132 2.87868 L 7.82843 2.17157  M 2.22836 3.85195 L 1.30448 3.46927  M 2.22836 3.85195 L 1.30448 3.46927  M 7.77164 3.85195 L 8.69552 3.46927  M 3.46927 1.30448 L 3.85195 2.22836  M 6.53073 1.30448 L 6.14805 2.22836");
    needleRegion = newSVG("M 4.5 5 C 4.333 6.5 5.666 6.5 5.5 5 L 5 2 z");
    labelRegion = newSVG("M 0 0 L 0 1 L 1 1 L 1 0 z");
    pressureReading = new Label(String.format("%.2f", pressure));
    pressureUnit = new Label("mmHg");

    double needleW = CompassBox.SIZE / 5.;
    double needleH = CompassBox.SIZE;
    setSize(needleRegion, needleW, needleH);
    needleRegion.setRotate(getRotation());
    setSize(labelRegion, 70, 50);
    labelRegion.setTranslateY(10);
    pressureUnit.setTranslateY(20);

    bodyRegion.getStyleClass().add("pressure-body");
    meterRegion.getStyleClass().add("pressure-meter");
    needleRegion.getStyleClass().add("pressure-needle");
    labelRegion.getStyleClass().add("pressure-label");
    pressureReading.getStyleClass().add("font-reg");
    pressureUnit.getStyleClass().add("font-reg");

    svgStack.getChildren().setAll(meterRegion, needleRegion, bodyRegion, labelRegion, pressureReading, pressureUnit);
  }

  private double pascalToMmHg(double pascal) {
    return pascal / 133.3;
  }

  private static final double MAX_MMHG = 900;
  private static final double MIN_MMHG = 620;
  private double getRotation() {
    if (pressure > MAX_MMHG) {
      return 85;
    } else if (pressure < MIN_MMHG) {
      return -85;
    }

    return ((pressure - MIN_MMHG) / (MAX_MMHG - MIN_MMHG)) * 170 - 85;
  }
}
