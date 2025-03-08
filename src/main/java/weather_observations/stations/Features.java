package weather_observations.stations;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Features {
  public Geometry geometry;
  public Properties properties;
}
