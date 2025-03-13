package views.components.widgets;

import javafx.geometry.Insets;
import javafx.scene.control.TextField;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import views.util.TextUtils;

/**
 * A {@code SmallBox} used for showing a missing data point.
 * Typically shown when the {@code WeatherObservations} API returns a null value.
 */
public class EmptyBox extends SmallBox {
  String title;

  // components
  Region blackRegion, questionRegion;

  /**
   * Create a new {@code EmptyBox} with the given title
   *
   * @param titleName the display title for the box
   */
  public EmptyBox(String titleName) {
    this.title = titleName;
  }

	public VBox component() {
    // set the title
    titleText.setText(title);

    // create the svg
    assembleSVG();

    // create and style the error text
    TextField explain = TextUtils.staticTextField("It would seem as the API has");
    TextField explain2 = TextUtils.staticTextField("not filled this data at this time.");
    explain.setPadding(new Insets(0));
    explain2.setPadding(new Insets(0));
    explain.getStyleClass().add("font-reg");
    explain2.getStyleClass().add("font-reg");

    // populate component
    comp.getChildren().setAll(titleText, svgStack, explain, explain2);
    return comp;
	}


  /**
   * builds the SVG question mark icon
   */
	protected void assembleSVG() {
    // create all the SVGs from a path
    blackRegion = newSVG("M 0 5 A 1 1 0 0 0 10 5 A 1 1 0 0 0 0 5");
    questionRegion = newSVG("M 0 5 A 1 1 0 0 0 10 5 A 1 1 0 0 0 0 5 M 5 7.5 A 0.25 0.25 0 0 1 5 8.5 A 0.25 0.25 0 0 1 5 7.5 M 3 3.5 A 1 1 0 0 1 7 3.5 C 7 5.5 5.5 5 5.5 6.5 L 4.5 6.5 C 4.5 4.5 6 4.75 6 3.5 A 1 1 0 0 0 4 3.5 z");

    // add style classes
    blackRegion.getStyleClass().add("blank-black");
    questionRegion.getStyleClass().add("blank-mark");

    // create the combined svg by stacking the components
    svgStack.getChildren().addAll(blackRegion, questionRegion);
	}
}
