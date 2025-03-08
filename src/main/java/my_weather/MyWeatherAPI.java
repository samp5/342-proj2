package my_weather;

import java.net.ConnectException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.concurrent.CompletableFuture;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import my_weather.gridPoint.GridPoint;

public class MyWeatherAPI {
  private static int MAX_RETRIES = 5;

  public static CompletableFuture<ArrayList<HourlyPeriod>> getHourlyForecastAsync(String region,
      int gridx, int gridy) {
    return CompletableFuture
        .supplyAsync(() -> {
          try {
            return MyWeatherAPI.getHourlyForecast(region, gridx, gridy);
          } catch (ConnectException ce) {
            throw new RuntimeException("Failed to connect");
          }
        });
  }

  public static CompletableFuture<GridPoint> getGridPointAsync(double lat, double lon) {
    return CompletableFuture.supplyAsync(() -> {
      try {
        return MyWeatherAPI.getGridPoint(lat, lon);
      } catch (ConnectException ce) {
        throw new RuntimeException("Failed to connect");
      }
    });
  }

  public static ArrayList<HourlyPeriod> getHourlyForecast(String region, int gridx, int gridy)
      throws ConnectException {
    HttpRequest request = HttpRequest.newBuilder()
        .uri(URI.create("https://api.weather.gov/gridpoints/" + region + "/" + String.valueOf(gridx)
            + "," + String.valueOf(gridy) + "/forecast/hourly"))
        .build();
    HttpResponse<String> response = null;
    try {
      response = HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());

    } catch (ConnectException ce) {
      throw new ConnectException("Failed to connect to the internet");
    } catch (Exception e) {
      e.printStackTrace();
      return null;
    }

    if (response.statusCode() < 200 || response.statusCode() > 299) {
      System.err.println("Response was: " + response.toString() + "\n"
          + "Request was: " + request.toString());
      return null;
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

  public static GridPoint getGridPoint(double lat, double lon) throws ConnectException {
    HttpRequest request = HttpRequest.newBuilder()
        .uri(URI.create("https://api.weather.gov/points/" + String.valueOf(lat)
            + "," + String.valueOf(lon)))
        .build();
    HttpResponse<String> response = null;
    try {
      response = HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());
    } catch (ConnectException e) {
      throw new ConnectException("Failed to connect to the internet");
    } catch (Exception e) {
      e.printStackTrace();
    }

    int retries = 0;
    while (response.statusCode() == 301) {
      if (retries >= MAX_RETRIES) break;
      retries++;

      request = HttpRequest.newBuilder()
        .uri(URI.create("https://api.weather.gov" + response.headers().firstValue("location").get()))
        .build();

      try {
        response = HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());
      } catch (ConnectException e) {
        throw new ConnectException("Failed to connect to the internet");
      } catch (Exception e) {
        e.printStackTrace();
      }
    }

    if (response.statusCode() < 200 || response.statusCode() > 299) {
      System.err.println("Response was: " + response.toString() + "\n"
          + "Request was: " + request.toString());
      return null;
    }

    my_weather.gridPoint.Root r = getGridPointRoot(response.body());
    if (r == null) {
      System.err.println("Failed to parse JSon");
      return null;
    }

    return new GridPoint(r.properties.gridX, r.properties.gridY, r.properties.cwa,
        r.properties.relativeLocation.properties.city + ", "
            + r.properties.relativeLocation.properties.state);
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
