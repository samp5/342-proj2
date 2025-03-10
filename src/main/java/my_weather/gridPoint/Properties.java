package my_weather.gridPoint;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * Properties of a {@code GridPoint}, contains grid and region data.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class Properties {
  public String cwa;
  public int gridX;
  public int gridY;
  public RelativeLocation relativeLocation;
}
