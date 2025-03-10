package weather_observations;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * Data containing a weather observation.
 * Typically created by {@code WeatherObservations}
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class Observations {
  public WindGust windGust;
  public BarometricPressure barometricPressure;
  public SeaLevelPressure seaLevelPressure;
  public Visibility visibility;
  public WindChill windChill;
  public HeatIndex heatIndex;
}
