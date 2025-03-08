package weather_observations;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Properties {
  public WindGust windGust;
  public BarometricPressure barometricPressure;
  public SeaLevelPressure seaLevelPressure;
  public Visibility visibility;
  public WindChill windChill;
  public HeatIndex heatIndex;
}
