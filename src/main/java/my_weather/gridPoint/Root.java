package my_weather.gridPoint;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * Root object for use with {@code GridPoint}s
 * Typically created by {@code MyWeatherAPI}
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class Root {
  public Properties properties;
}
