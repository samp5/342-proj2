package views.components;

import javafx.geometry.Insets;
import javafx.scene.control.TextField;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.SVGPath;
import views.util.TextUtils;

public abstract class SmallBox {
  protected static final double SIZE = 160;

  protected VBox comp;
  protected TextField titleText;
  protected StackPane svgStack;

  public SmallBox() {
    comp = new VBox();
    comp.getStyleClass().add("small-box");

    svgStack = new StackPane();

    titleText = TextUtils.staticTextField("");
    titleText.getStyleClass().add("font-bold");
    titleText.setPadding(new Insets(20, 0, 10, 0));
  }

  public abstract VBox component();
  protected abstract void assembleSVG();

  protected Region newSVG(String path) {
    SVGPath svg = new SVGPath();
    svg.setContent(path);

    Region region = new Region();
    region.setShape(svg);

    setStandardSize(region);

    return region;
  }

  protected void setStandardSize(Region r) {
    setSize(r, SIZE, SIZE);
  }

  protected void setSize(Region r, double w, double h) {
    r.setMinSize(w, h);
    r.setPrefSize(w, h);
    r.setMaxSize(w, h);
  }
}
