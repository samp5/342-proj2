package views.util;

import java.util.ArrayList;

import my_weather.HourlyPeriod;
import my_weather.gridPoint.GridPoint;
import weather_observations.Observations;

public class LocationChangeData {
  public ArrayList<HourlyPeriod> periods;
  public Observations observations;
  public GridPoint point;

  public LocationChangeData(
      ArrayList<HourlyPeriod> periods,
      Observations observations,
      GridPoint point) {

    this.periods = periods;
    this.observations = observations;
    this.point = point;
  }
}
