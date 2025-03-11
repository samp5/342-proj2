package views.components;

import javafx.geometry.Insets;
import javafx.scene.control.TextField;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import views.util.TextUtils;

public class EmptyBox extends SmallBox {
  String title;

  Region blackRegion;
  Region questionRegion;

  public EmptyBox(String titleName) {
    this.title = titleName;
  }

	public VBox component() {
    titleText.setText(title);

    assembleSVG();

    TextField explain = TextUtils.staticTextField("It would seem as the API has");
    TextField explain2 = TextUtils.staticTextField("not filled this data at this time.");
    explain.setPadding(new Insets(0));
    explain2.setPadding(new Insets(0));
    explain.getStyleClass().add("font-reg");
    explain2.getStyleClass().add("font-reg");

    comp.getChildren().setAll(titleText, svgStack, explain, explain2);
    return comp;
	}

	protected void assembleSVG() {
    blackRegion = newSVG("M 0 5 A 1 1 0 0 0 10 5 A 1 1 0 0 0 0 5");
    questionRegion = newSVG("M 0 5 A 1 1 0 0 0 10 5 A 1 1 0 0 0 0 5 M 5 7.5 A 0.25 0.25 0 0 1 5 8.5 A 0.25 0.25 0 0 1 5 7.5 M 3 3.5 A 1 1 0 0 1 7 3.5 C 7 5.5 5.5 5 5.5 6.5 L 4.5 6.5 C 4.5 4.5 6 4.75 6 3.5 A 1 1 0 0 0 4 3.5 z");

    blackRegion.getStyleClass().add("blank-black");
    questionRegion.getStyleClass().add("blank-mark");

    svgStack.getChildren().addAll(blackRegion, questionRegion);
	}
}
