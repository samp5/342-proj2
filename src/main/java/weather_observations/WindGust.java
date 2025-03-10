package weather_observations;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * Wind gust weather observation.
 * For use with {@code Observations}
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class WindGust {
  public double value;
  public String unitCode;
}
