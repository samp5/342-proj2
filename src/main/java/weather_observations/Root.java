package weather_observations;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Root {
  public String type;
  public Observations properties;
}
