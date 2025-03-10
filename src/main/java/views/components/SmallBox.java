package views.components;

import javafx.geometry.Insets;
import javafx.scene.control.TextField;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.SVGPath;
import views.util.TextUtils;

/**
 * A simple and small container for certain information graphs or icons.
 * Always has some title and image made from SVGs.
 * Also has some helper functions for private use, such as setting standard size.
 * All {@code SmallBox}es can have their component gathered from the {@code SmallBox.component()} method.
 */
public abstract class SmallBox {
  // default size of internal icons
  protected static final double SIZE = 160;

  // component, title, and svg that are universal
  protected VBox comp;
  protected TextField titleText;
  protected StackPane svgStack;

  /**
   * constructor of an abstract class is implicicly called by superclass constructors.
   * as such this code is always called.
   *
   * initializes the component, svgStack, and the title
   */
  public SmallBox() {
    comp = new VBox();
    comp.getStyleClass().add("small-box");

    svgStack = new StackPane();

    titleText = TextUtils.staticTextField("");
    titleText.getStyleClass().add("font-bold");
    titleText.setPadding(new Insets(20, 0, 10, 0));
  }

  /**
   * get the {@code VBox} component for this element
   *
   * @return the {@code VBox} component for this element
   */
  public abstract VBox component();
  /**
   * create the SVG and place it into the {@code svgStack}
   */
  protected abstract void assembleSVG();

  /**
   * create a new {@code Region} containing an {@code SVG} just from a path
   */
  protected Region newSVG(String path) {
    SVGPath svg = new SVGPath();
    svg.setContent(path);

    Region region = new Region();
    region.setShape(svg);

    setStandardSize(region);

    return region;
  }

  /**
   * set the size of a region to the standard 160x160px
   *
   * @param r the region to resize
   */
  protected void setStandardSize(Region r) {
    setSize(r, SIZE, SIZE);
  }

  /**
   * set the size of a region to the given width and height
   *
   * @param r the region to resize
   * @param w width of the region
   * @param h height of the region
   */
  protected void setSize(Region r, double w, double h) {
    r.setMinSize(w, h);
    r.setPrefSize(w, h);
    r.setMaxSize(w, h);
  }
}
