package my_weather.gridPoint;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class RelativeLocation {
  @JsonIgnoreProperties(ignoreUnknown = true)
  public class Properties {
    public String city;
    public String state;
  }

  public Properties properties;
}
