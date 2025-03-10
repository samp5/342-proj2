package weather_observations.stations;

import java.util.ArrayList;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * Base element for a station's data
 * For use with {@code Stations}
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class Root {
  public ArrayList<Features> features;
}

