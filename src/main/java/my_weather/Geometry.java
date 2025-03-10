package my_weather;

import java.util.ArrayList;

/**
 * Geometry property of a forecast.
 * Typically created by use of {@code MyWeatherAPI}
 */
public class Geometry {
  public String type;
  public ArrayList<ArrayList<ArrayList<Double>>> coordinates;
}
