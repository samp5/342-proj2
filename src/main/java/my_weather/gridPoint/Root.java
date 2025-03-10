package my_weather.gridPoint;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * Root object for use with {@code GridPoint}s
 * Typically created by {@code MyWeatherAPI}
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class Root {
  public Properties properties;

  @JsonIgnoreProperties(ignoreUnknown = true)
  public class Properties {
    public String cwa;
    public int gridX;
    public int gridY;
    public RelativeLocation relativeLocation;

    @JsonIgnoreProperties(ignoreUnknown = true)
    public class RelativeLocation {
      public LocationProperties properties;

      @JsonIgnoreProperties(ignoreUnknown = true)
      public class LocationProperties {
        public String city;
        public String state;
      }
    }
  }
}
