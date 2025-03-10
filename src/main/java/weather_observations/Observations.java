package weather_observations;

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
      public double value;
      public String unitCode;
    }
    
    @JsonIgnoreProperties(ignoreUnknown = true)
    public class HeatIndex {
      public double value;
      public String unitCode;
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public class SeaLevelPressure {
      public double value;
      public String unitCode;
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public class Visibility {
      public double value;
      public String unitCode;
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public class WindSpeed {
      public double value;
      public String unitCode;

      /**
       * gives the windspeed in mph instead of kmph
       */
      public double mph() {
        return value * 0.621371;
      }
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public class WindChill {
      public double value;
      public String unitCode;

      public int getTemperature() {
        if (UnitHandler.getUnit() == TemperatureUnit.Celsius) return (int) value;
        return (int) (value * 9. / 5.) + 32;
      }
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public class WindGust {
      public double value;
      public String unitCode;
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public class WindDirection {
      public double value;
      public String unitCode;
    }
  }
