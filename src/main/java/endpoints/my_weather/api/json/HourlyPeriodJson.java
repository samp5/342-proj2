package endpoints.my_weather.api.json;

import java.util.ArrayList;
import java.util.Date;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import endpoints.my_weather.data.HourlyPeriod;

/**
 * Root object of data gathered from {@code MyWeatherAPI} calls.
 * Typically created by use of {@code MyWeatherAPI}
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class HourlyPeriodJson {
  public String type;
  public Geometry geometry;
  public Properties properties;

  public class Geometry {
    public String type;
    public ArrayList<ArrayList<ArrayList<Double>>> coordinates;
  }

  public class Properties {
    public String units;
    public String forecastGenerator;
    public Date generatedAt;
    public Date updateTime;
    public String validTimes;
    public Elevation elevation;
    public ArrayList<HourlyPeriod> periods;

    public class Elevation {
      public String unitCode;
      public double value;
    }
  }
}
