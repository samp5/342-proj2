package views.util;

import java.util.ArrayList;
import java.util.Optional;

import endpoints.my_weather.data.GridPoint;
import endpoints.my_weather.data.HourlyPeriod;
import endpoints.my_weather.data.Period;
import endpoints.weather_observations.data.Observations;
import views.util.UnitHandler.TemperatureUnit;

/**
 * Hold all the relevant collections for a change of location.
 *
 * This really should only be constructed via {@code JavaFX.changeLocation}
 */
public class LocationChangeData {
  public ArrayList<HourlyPeriod> periods;
  public Observations observations;
  public GridPoint point;
  public String name;
  public Period today;
  public DetailedForecasts detailedForecasts;

  /**
   * Holds detailed forecasts for each unit
   */
  public static class DetailedForecasts {
    public String USdetailedForecast;
    public String SIdetailedForecast;

    public DetailedForecasts(String us, String si) {
      this.USdetailedForecast = us;
      this.SIdetailedForecast = si;
    }

    /**
     * Get the appropriate forecast based on the given {@code TemperatureUnit}
     *
     * @param unit
     * @return detailed forecast in {@code unit}
     */
    public String getDetailedForecast(TemperatureUnit unit) {
      switch (unit) {
        case Celsius:
          return this.SIdetailedForecast;
        case Fahrenheit:
          return this.USdetailedForecast;
        default:
          return "";
      }
    }
  }

  public LocationChangeData(
      ArrayList<HourlyPeriod> periods,
      Observations observations,
      GridPoint point, String name, DetailedForecasts detailed) {

    this.periods = periods;
    this.observations = observations;
    this.point = point;
    this.name = name;
    this.detailedForecasts = detailed;
  }
}
