package endpoints.my_weather.api;

import java.net.ConnectException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.concurrent.CompletableFuture;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import endpoints.my_weather.api.json.GridPointJson;
import endpoints.my_weather.api.json.HourlyPeriodJson;
import endpoints.my_weather.data.GridPoint;
import endpoints.my_weather.data.HourlyPeriod;

/** 
 * used for getting weather forecasts statically.
 * likely should not be instantiated.
 *
 * most useful methods:
 *  - {@code getHourlyForecastAsync}
 *  - {@code getHourlyForecast}
 */
public class MyWeatherAPI {
  // amount of retries to attempt if status code 301 is read
  private static int MAX_RETRIES = 5;


  /**
   * asynchronously gather hourly forecasts given a region and gridpoints
   *
   * @param region the weather region. typically found from a {@code my_weather.gridPoint} object
   * @param gridx the x value for the grid point found similarly to above.
   * @param gridy the y value for the grid point found similarly to above.
   * @return a {@code CompletableFuture} of a list of {@code HourlyPeriod}s containing the weather forecasts
   */
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

  /**
   * asynchronously gather a grid point based on a latitude, longitude pair
   *
   * @param lat the latitude of the position
   * @param lon the longitude of the position
   * @return a {@code CompletableFuture} of a {@code GridPoint}
   */
  public static CompletableFuture<GridPoint> getGridPointAsync(double lat, double lon) {
    return CompletableFuture.supplyAsync(() -> {
      try {
        return MyWeatherAPI.getGridPoint(lat, lon);
      } catch (ConnectException ce) {
        throw new RuntimeException("Failed to connect");
      }
    });
  }

  /**
   * gather hourly forecasts given a region and gridpoints
   *
   * @param region the weather region. typically found from a {@code my_weather.gridPoint} object
   * @param gridx the x value for the grid point found similarly to above.
   * @param gridy the y value for the grid point found similarly to above.
   * @return a list of {@code HourlyPeriod}s containing the weather forecasts
   */
  public static ArrayList<HourlyPeriod> getHourlyForecast(String region, int gridx, int gridy)
      throws ConnectException {
    // form the api request
    HttpRequest request = HttpRequest.newBuilder()
        .uri(URI.create("https://api.weather.gov/gridpoints/" + region + "/" + String.valueOf(gridx)
            + "," + String.valueOf(gridy) + "/forecast/hourly"))
        .build();
    HttpResponse<String> response = null;

    // send the request, fail gracefully if needed
    try {
      response = HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());
    } catch (ConnectException ce) {
      throw new ConnectException("Failed to connect to the internet");
    } catch (Exception e) {
      e.printStackTrace();
      return null;
    }

    // if a bad status code was recieved, fail gracefully, log to system err
    if (response.statusCode() < 200 || response.statusCode() > 299) {
      System.err.println("Response was: " + response.toString() + "\n"
          + "Request was: " + request.toString());
      return null;
    }

    // parse the response body json into an object
    HourlyPeriodJson r = getObject(response.body());
    if (r == null) {
      System.err.println("Failed to parse JSon");
      return null;
    }

    // parse the object into a list of periods
    ArrayList<HourlyPeriod> periods = new ArrayList<>();
    r.properties.periods.iterator().forEachRemaining(period -> periods.add((HourlyPeriod) period));
    return periods;
  }

  /**
   * gather a grid point based on a latitude, longitude pair
   *
   * @param lat the latitude of the position
   * @param lon the longitude of the position
   * @return a {@code CompletableFuture} of a {@code GridPoint}
   */
  public static GridPoint getGridPoint(double lat, double lon) throws ConnectException {
    // form the api request
    HttpRequest request = HttpRequest.newBuilder()
        .uri(URI.create("https://api.weather.gov/points/" + String.valueOf(lat)
            + "," + String.valueOf(lon)))
        .build();
    HttpResponse<String> response = null;

    // send the request, fail gracefully if needed
    try {
      response = HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());
    } catch (ConnectException e) {
      throw new ConnectException("Failed to connect to the internet");
    } catch (Exception e) {
      e.printStackTrace();
    }

    // if the response gave back a 301 (redirect), attempt to redirect up to 5 times
    int retries = 0;
    while (response.statusCode() == 301) {
      if (retries >= MAX_RETRIES) break;
      retries++;

      // form the new request
      request = HttpRequest.newBuilder()
        .uri(URI.create("https://api.weather.gov" + response.headers().firstValue("location").get()))
        .build();

      // send the request, fail gracefully if needed
      try {
        response = HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());
      } catch (ConnectException e) {
        throw new ConnectException("Failed to connect to the internet");
      } catch (Exception e) {
        e.printStackTrace();
      }
    }

    // if a bad status code was recieved, fail gracefully, log to system err
    if (response.statusCode() < 200 || response.statusCode() > 299) {
      System.err.println("Response was: " + response.toString() + "\n"
          + "Request was: " + request.toString());
      return null;
    }

    // parse the response body json into an object
    GridPointJson r = getGridPointRoot(response.body());
    if (r == null) {
      System.err.println("Failed to parse JSon");
      return null;
    }

    // parse the object into a new gridpoint
    return new GridPoint(r.properties.gridX, r.properties.gridY, r.properties.cwa,
        r.properties.relativeLocation.properties.city + ", "
            + r.properties.relativeLocation.properties.state);
  }

  /**
   * parse a json string into a {@code my_weather.gridPoint.Root} object
   *
   * @param json the json to parse in string form
   * @return the {@code Root} object parsed
   */
  public static GridPointJson getGridPointRoot(String json) {
    ObjectMapper om = new ObjectMapper();
    GridPointJson toRet = null;
    try {
      toRet = om.readValue(json, GridPointJson.class);
    } catch (JsonProcessingException e) {
      e.printStackTrace();
    }
    return toRet;
  }

  /**
   * parse a json string into a {@code Root} object
   *
   * @param json the json to parse in string form
   * @return the {@code Root} object parsed
   */
  @SuppressWarnings("unused")
  public static HourlyPeriodJson getObject(String json) {
    ObjectMapper om = new ObjectMapper();
    HourlyPeriodJson toRet = null;
    try {
      toRet = om.readValue(json, HourlyPeriodJson.class);
      ArrayList<HourlyPeriod> p = toRet.properties.periods;

    } catch (JsonProcessingException e) {
      e.printStackTrace();
    }
    return toRet;
  }
}
