package weather_observations;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import views.util.UnitHandler;
import views.util.UnitHandler.TemperatureUnit;

/**
 * Wind chill weather observation.
 * For use with {@code Observations}
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class WindChill {
  public double value;
  public String unitCode;

  public int getTemperature() {
    if (UnitHandler.getUnit() == TemperatureUnit.Celsius) return (int) value;
    return (int) (value * 9. / 5.) + 32;
  }
}
