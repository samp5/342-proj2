package my_weather;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * Root object of data gathered from {@code MyWeatherAPI} calls.
 * Typically created by use of {@code MyWeatherAPI}
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class Root {
  public String type;
  public Geometry geometry;
  public Properties properties;
}
