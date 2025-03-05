import info.debatty.java.stringsimilarity.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;
import javafx.scene.image.Image;

class IconResolver {
  private static NormalizedLevenshtein stringComp = new NormalizedLevenshtein();

  public IconResolver() {}

  public Image getIcon(String short_forecast) {

    Path path = getFilePath(short_forecast);

    try {
      System.err.println(path.toAbsolutePath().toString());
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


  private Path getFilePath(String short_forecast) {

    try {

      Path dirPath =
          Paths.get(getClass().getResource("icons").toURI());

      List<Path> paths =
          Files.walk(dirPath).collect(Collectors.toList());

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

      return bestMatch;

    } catch (Exception e) {
      e.printStackTrace();
      System.exit(1);
    }

    return null;
  }
}
