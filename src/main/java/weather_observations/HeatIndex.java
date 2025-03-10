package weather_observations;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * Heat index weather observation.
 * For use with {@code Observations}
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class HeatIndex {
  public double value;
  public String unitCode;
}
