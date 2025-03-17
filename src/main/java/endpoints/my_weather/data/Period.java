package endpoints.my_weather.data;

import java.util.Date;

/**
 * A period of weather forecast data.
 * Typically created by use of {@code MyWeatherAPI}
 */
public class Period {
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

  public class ProbabilityOfPrecipitation {
    public String unitCode;
    public int value;
  }
}
