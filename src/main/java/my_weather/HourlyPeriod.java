package my_weather;

import java.util.Date;

/**
 * An hourly period of data. 
 * Contains most forecast information needed for app functionality.
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
}
