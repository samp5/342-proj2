package weather_observations;

import java.net.ConnectException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.concurrent.CompletableFuture;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import weather_observations.stations.Stations;

/** 
 * used for getting weather observations statically.
 * likely should not be instantiated.
 *
 * most useful methods:
 *  - {@code getWeatherObservationsAsync}
 *  - {@code getWeatherObservations}
 */
public class WeatherObservations {
  /**
   * asynchronously gather weather observations closest to a given latitude and longitude in a region and its grid points
   *
   * @param region the weather region. typically found from a {@code my_weather.gridPoint} object
   * @param gridX the x value for the grid point found similarly to above.
   * @param gridY the y value for the grid point found similarly to above.
   * @param lat the latitude value to compare with
   * @param lon the longitude value to compare with
   * @return a {@code CompletableFuture} containing the weather {@code Observations} found
   */
  public static CompletableFuture<Observations> getWeatherObservationsAsync(String region, int gridX, int gridY, double lat, double lon) {
    return CompletableFuture
      .supplyAsync(() -> {
        try {
          return getWeatherObservations(region, gridX, gridY, lat, lon);
        } catch (ConnectException ce) {
          throw new RuntimeException("Failed to connect");
        }
      });
  }

  /**
   * gather weather observations closest to a given latitude and longitude in a region and its grid points
   *
   * @param region the weather region. typically found from a {@code my_weather.gridPoint} object
   * @param gridX the x value for the grid point found similarly to above.
   * @param gridY the y value for the grid point found similarly to above.
   * @param lat the latitude value to compare with
   * @param lon the longitude value to compare with
   * @return the weather {@code Observations} found
   */
  public static Observations getWeatherObservations(String region, int gridX, int gridY, double lat, double lon) throws ConnectException {
    // get the nearset weather station to the latitude and longitude given
    String station = Stations.getNearestStation(region, gridX, gridY, lat, lon);

    // form api request
    HttpRequest request = HttpRequest.newBuilder()
        .uri(URI.create("https://api.weather.gov/stations/" + station + "/observations/latest"))
        .build();
    HttpResponse<String> response = null;

    // send the request, fail gracefully if needed
    try {
      response = HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());
    } catch (ConnectException ce) {
      throw new ConnectException("Failed to connect to the internet");
    } catch (Exception e) {
      e.printStackTrace();
    }

    // parse the response body into an object
    ObservationJson r = getObject(response.body());
    if (r == null) {
      System.err.println("Failed to parse JSon");
      return null;
    }

    return r.properties;
  }

  /**
   * parse a json string into a {@code Root} object
   *
   * @param json the json to parse in string form
   * @return the {@code Root} object parsed
   */
  public static ObservationJson getObject(String json) {
    ObjectMapper om = new ObjectMapper();
    ObservationJson toRet = null;
    try {
      toRet = om.readValue(json, ObservationJson.class);
      Observations p = toRet.properties;

    } catch (JsonProcessingException e) {
      e.printStackTrace();
    }
    return toRet;

  }
}
