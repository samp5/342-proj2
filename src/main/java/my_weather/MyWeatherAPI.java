package my_weather;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import my_weather.gridPoint.GridPoint;

public class MyWeatherAPI {

  public static ArrayList<HourlyPeriod> getHourlyForecast(String region, int gridx, int gridy) {
    HttpRequest request = HttpRequest.newBuilder()
        .uri(URI.create("https://api.weather.gov/gridpoints/" + region + "/" + String.valueOf(gridx)
            + "," + String.valueOf(gridy) + "/forecast/hourly"))
        .build();
    HttpResponse<String> response = null;
    try {
      response = HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());
    } catch (Exception e) {
      e.printStackTrace();
    }
    Root r = getObject(response.body());
    if (r == null) {
      System.err.println("Failed to parse JSon");
      return null;
    }
    ArrayList<HourlyPeriod> periods = new ArrayList<>();
    r.properties.periods.iterator().forEachRemaining(period -> periods.add((HourlyPeriod) period));
    return periods;
  }

  public static GridPoint getGridPoint(double lat, double lon) {
    HttpRequest request = HttpRequest.newBuilder()
        .uri(URI.create("https://api.weather.gov/points/" + String.valueOf(lat)
            + "," + String.valueOf(lon)))
        .build();
    HttpResponse<String> response = null;
    try {
      response = HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());
    } catch (Exception e) {
      e.printStackTrace();
    }
    my_weather.gridPoint.Root r = getGridPointRoot(response.body());
    if (r == null) {
      System.err.println("Failed to parse JSon");
      return null;
    }
    return new GridPoint(r.properties.gridX, r.properties.gridY, r.properties.cwa);
  }

  public static my_weather.gridPoint.Root getGridPointRoot(String json) {
    ObjectMapper om = new ObjectMapper();
    my_weather.gridPoint.Root toRet = null;
    try {
      toRet = om.readValue(json, my_weather.gridPoint.Root.class);
    } catch (JsonProcessingException e) {
      e.printStackTrace();
    }
    return toRet;
  }

  public static Root getObject(String json) {
    ObjectMapper om = new ObjectMapper();
    Root toRet = null;
    try {
      toRet = om.readValue(json, Root.class);
      ArrayList<HourlyPeriod> p = toRet.properties.periods;

    } catch (JsonProcessingException e) {
      e.printStackTrace();
    }
    return toRet;

  }
}
