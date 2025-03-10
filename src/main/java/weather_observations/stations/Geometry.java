package weather_observations.stations;

import java.util.ArrayList;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * Geometry of a weather station.
 * For use with {@code Stations}
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class Geometry {
  public ArrayList<Double> coordinates;
}
