package views.util;

import java.util.ArrayList;
import java.util.Optional;

import endpoints.my_weather.data.GridPoint;
import endpoints.my_weather.data.HourlyPeriod;
import endpoints.weather_observations.data.Observations;

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

  public LocationChangeData(
      ArrayList<HourlyPeriod> periods,
      Observations observations,
      GridPoint point, String name) {

    this.periods = periods;
    this.observations = observations;
    this.point = point;
    this.name = name;
  }
}
