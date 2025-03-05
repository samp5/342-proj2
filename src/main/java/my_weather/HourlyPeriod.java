package my_weather;

import java.util.Date;

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
