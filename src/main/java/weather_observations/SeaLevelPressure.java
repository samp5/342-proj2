package weather_observations;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * Sea level pressure weather observation.
 * For use with {@code Observations}
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class SeaLevelPressure {
  public double value;
  public String unitCode;
}

