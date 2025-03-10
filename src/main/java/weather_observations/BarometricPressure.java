package weather_observations;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * Barometric pressure weather observation.
 * For use with {@code Observations}
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class BarometricPressure {
  public double value;
  public String unitCode;
}
