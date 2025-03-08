package weather_observations;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class HeatIndex {
  public double value;
  public String unitCode;
}
