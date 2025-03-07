package my_weather.gridPoint;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Properties {
  public String cwa;
  public int gridX;
  public int gridY;
  public RelativeLocation relativeLocation;
}
