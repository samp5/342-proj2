import info.debatty.java.stringsimilarity.*;
import java.io.FileNotFoundException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import javafx.scene.image.Image;

/**
 * Get a {@code javafx.scene.image} based on the closest match to a {@code string}
 * and the resource {@code .png} in {@code /resouces/icons/}
 */
class IconResolver {
  private final static NormalizedLevenshtein stringComp = new NormalizedLevenshtein();

  public IconResolver() {}

  /**
   * @param short_forecast a string representing the forecast
   * @return A {@code javafx.scene.image} from the closest matching file in {@code /resouces/icons/}
   */
  public Image getIcon(String short_forecast) throws FileNotFoundException {

    Path path = getFilePath(short_forecast);

    try {
      Image image =
          new Image(path.subpath(path.getNameCount() - 2, path.getNameCount()).toString());
      return image;
    } catch (NullPointerException e) {
      e.printStackTrace();
      System.exit(1);
    }
    return null;
  }

  private double distance(String png, String short_forecast) {
    return stringComp.distance(png, short_forecast);
  }


  /**
   * @param short_forecast {@code String} representing the pattern to match against
   */
  private Path getFilePath(String short_forecast) throws FileNotFoundException {

    try {

      // get the path to icons
      Path dirPath =
          Paths.get(getClass().getResource("icons").toURI());

      // walk the path and graph all *.pngs
      List<Path> paths =
          Files.walk(dirPath).filter(path -> path.getFileName().toString().endsWith(".png"))
              .collect(Collectors.toList());

      Path bestMatch = null;
      double bestScore = 0;

      for (Path img_name : paths) {
        double distance =
            distance(img_name.getFileName().toString().replace(".png", ""), short_forecast);
        if (1.0 - distance > bestScore) {
          bestMatch = img_name;
          bestScore = 1.0 - distance;
        }
      }

      if (bestMatch == null) {
        throw new FileNotFoundException();
      }

      return bestMatch;

    } catch (Exception e) {

      e.printStackTrace();

      throw new FileNotFoundException();
    }

  }
}
