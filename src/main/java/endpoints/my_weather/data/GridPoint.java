package endpoints.my_weather.data;

/**
 * A gridpoint containing a region, location, and grid coordinates.
 * Typically for use with {@code MyWeatherAPI}
 */
public class GridPoint {
  public int gridX, gridY;
  public String region;
  public String location;

  public GridPoint(int gridX, int gridY, String region, String location) {
    this.gridX = gridX;
    this.gridY = gridY;
    this.region = region;
    this.location = location;
  }
}
