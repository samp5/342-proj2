package endpoints.weather_observations.data;

import views.util.UnitHandler;
import views.util.UnitHandler.TemperatureUnit;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
  public class Observations {
    public WindGust windGust;
    public BarometricPressure barometricPressure;
    public SeaLevelPressure seaLevelPressure;
    public Visibility visibility;
    public WindChill windChill;
    public HeatIndex heatIndex;
    public WindSpeed windSpeed;
    public WindDirection windDirection;
    
    @JsonIgnoreProperties(ignoreUnknown = true)
    public class BarometricPressure {
      public Double value;
      public String unitCode;
    }
    
    @JsonIgnoreProperties(ignoreUnknown = true)
    public class HeatIndex {
      public Double value;
      public String unitCode;
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public class SeaLevelPressure {
      public Double value;
      public String unitCode;
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public class Visibility {
      public Double value;
      public String unitCode;
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public class WindSpeed {
      public Double value;
      public String unitCode;

      /**
       * gives the windspeed in mph instead of kmph
       */
      public Double mph() {
        return value * 0.621371;
      }
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public class WindChill {
      public Double value;
      public String unitCode;

      public Double getTemperature() {
        if (UnitHandler.getUnit() == TemperatureUnit.Celsius) return value;
        return (value * 9. / 5.) + 32;
      }
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public class WindGust {
      public Double value;
      public String unitCode;
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public class WindDirection {
      public Double value;
      public String unitCode;
    }
  }
