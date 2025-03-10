package weather_observations.stations;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * Features of a weather station.
 * For use with {@code Stations}
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class Features {
  public Geometry geometry;
  public Properties properties;
}
