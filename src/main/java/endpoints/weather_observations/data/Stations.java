package endpoints.weather_observations.data;

import java.util.ArrayList;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * Features of a weather station.
 * For use with {@code Stations}
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class Stations {
  public Geometry geometry;
  public Properties properties;

  @JsonIgnoreProperties(ignoreUnknown = true)
  public class Geometry {
    public ArrayList<Double> coordinates;
  }

  @JsonIgnoreProperties(ignoreUnknown = true)
  public class Properties {
    public String stationIdentifier;
  }
}
