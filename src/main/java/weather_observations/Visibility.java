package weather_observations;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Visibility {
  public double value;
  public String unitCode;
}
