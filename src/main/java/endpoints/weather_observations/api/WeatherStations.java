package endpoints.weather_observations.api;

import java.net.ConnectException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import endpoints.weather_observations.data.Stations;

/**
 * Used for getting weather stations statically.
 * Likely should not be instantiated.
 *
 * most useful methods:
 * - {@code getNearestStation}
 * - {@code getStations}
 */
public class WeatherStations {
  /**
   * Find the nearest weather station in a grid to latitude and longitude points
   *
   * @param region the weather region. typically found from a
   *               {@code my_weather.gridPoint} object
   * @param gridX  the x value for the grid point found similarly to above.
   * @param gridY  the y value for the grid point found similarly to above.
   * @param lat    the latitude value to compare with
   * @param lon    the longitude value to compare with
   * @return the {@code String} code for the weather station
   */
  public static String getNearestStation(String region, int gridX, int gridY, double lat, double lon)
      throws ConnectException {
    // get all stations for the grid point
    ArrayList<Stations> features = getStations(region, gridX, gridY);

    double minDist = Double.MAX_VALUE;
    String id = "NO_STATIONS";

    if (features == null) {
      return id;
    }

    // calculate the closest station to lat and lon
    double dist, distLat, distLon;
    for (Stations feature : features) {
      distLat = feature.geometry.coordinates.getFirst() - lat;
      distLon = feature.geometry.coordinates.getLast() - lon;
      dist = Math.pow(distLat * distLat + distLon * distLon, .5);

      if (dist < minDist) {
        minDist = dist;
        id = feature.properties.stationIdentifier;
      }
    }

    return id;
  }

  /**
   * get all stations for a given region and grid point
   *
   * @param region the weather region. typically found from a
   *               {@code my_weather.gridPoint} object
   * @param gridX  the x value for the grid point found similarly to above.
   * @param gridY  the y value for the grid point found similarly to above.
   * @return a list of stations stored by their {@code Features}
   */
  public static ArrayList<Stations> getStations(String region, int gridX, int gridY) throws ConnectException {
    // form API request
    HttpRequest request = HttpRequest.newBuilder()
        .uri(URI.create("https://api.weather.gov/gridpoints/" + region + "/" + String.valueOf(gridX)
            + "," + String.valueOf(gridY) + "/stations"))
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
    StationJson r = getObject(response.body());
    if (r == null) {
      System.err.println("Failed to parse JSon");
      return null;
    }

    // turn the response into an arraylist to return
    ArrayList<Stations> stations = new ArrayList<>();
    if (r.features == null) {
      return null;
    }

    r.features.iterator().forEachRemaining(station -> stations.add((Stations) station));
    return stations;
  }

  /**
   * parse a json string into a {@code Root} object
   *
   * @param json the json to parse in string form
   * @return the {@code Root} object parsed
   */
  @SuppressWarnings("unused")
  public static StationJson getObject(String json) {
    ObjectMapper om = new ObjectMapper();
    StationJson toRet = null;
    try {
      toRet = om.readValue(json, StationJson.class);
      ArrayList<Stations> o = toRet.features;

    } catch (JsonProcessingException e) {
      e.printStackTrace();
    }
    return toRet;
  }

  /**
   * Base element for a station's data
   * return features for use
   */
  @JsonIgnoreProperties(ignoreUnknown = true)
  public static class StationJson {
    public ArrayList<Stations> features;
  }
}
