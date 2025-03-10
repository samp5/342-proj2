package my_weather;

import java.util.Date;

/**
 * A one hour period of weather forecast data.
 * Typically created by use of {@code MyWeatherAPI}
 */
public class HourlyPeriod {
  public int number;
  public String name;
  public Date startTime;
  public Date endTime;
  public boolean isDaytime;
  public int temperature;
  public String temperatureUnit;
  public String temperatureTrend;
  public ProbabilityOfPrecipitation probabilityOfPrecipitation;
  public String windSpeed;
  public String windDirection;
  public String icon;
  public String shortForecast;
  public String detailedForecast;
  public DewPoint dewpoint;
  public RelativeHumidity relativeHumidity;
    
  public class ProbabilityOfPrecipitation {
    public String unitCode;
    public int value;
  }
  
  public class DewPoint {
    public String unitCode;
    public double value;
  }
  
  public class RelativeHumidity {
    public String unitCode;
    public int value;
  }
}
