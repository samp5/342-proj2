package weather_observations.stations;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * Basic properties of a weather station.
 * For use with {@code Stations}
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class Properties {
  public String stationIdentifier;
}
