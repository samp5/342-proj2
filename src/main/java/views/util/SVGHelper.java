package views.util;

import javafx.scene.layout.Region;
import javafx.scene.shape.SVGPath;

public class SVGHelper {
  public static Region newSVG(String path, double width, double height) {
    SVGPath svg = new SVGPath();
    svg.setContent(path);

    Region region = new Region();
    region.setShape(svg);

    setSize(region, width, height);

    return region;
  }

  public static Region newSVG(String path, double size) {
    SVGPath svg = new SVGPath();
    svg.setContent(path);

    Region region = new Region();
    region.setShape(svg);

    setSize(region, size, size);

    return region;
  }

  public static Region newSVG(String path) {
    SVGPath svg = new SVGPath();
    svg.setContent(path);

    Region region = new Region();
    region.setShape(svg);

    return region;
  }

  /**
   * set the size of a region to the given width and height
   *
   * @param r the region to resize
   * @param w width of the region
   * @param h height of the region
   */
  public static void setSize(Region r, double w, double h) {
    r.setMinSize(w, h);
    r.setPrefSize(w, h);
    r.setMaxSize(w, h);
  }
}
