package weather_observations.stations;

import java.net.ConnectException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class Stations {
  public static String getNearestStation(String region, int gridX, int gridY, double lat, double lon) throws ConnectException {
    ArrayList<Features> features = getStations(region, gridX, gridY);

    double minDist = Double.MAX_VALUE;
    String id = "NO STATIONS";

    double dist, distLat, distLon;
    for (Features feature : features) {
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

  public static ArrayList<Features> getStations(String region, int gridX, int gridY) throws ConnectException {
    HttpRequest request = HttpRequest.newBuilder()
        .uri(URI.create("https://api.weather.gov/gridpoints/" + region + "/" + String.valueOf(gridX)
            + "," + String.valueOf(gridY) + "/stations"))
        .build();
    HttpResponse<String> response = null;
    try {
      response = HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());
    } catch (ConnectException ce) {
      throw new ConnectException("Failed to connect to the internet");
    } catch (Exception e) {
      e.printStackTrace();
    }
    Root r = getObject(response.body());
    if (r == null) {
      System.err.println("Failed to parse JSon");
      return null;
    }
    ArrayList<Features> stations = new ArrayList<>();
    r.features.iterator().forEachRemaining(station -> stations.add((Features) station));
    return stations;
  }

  public static Root getObject(String json) {
    ObjectMapper om = new ObjectMapper();
    Root toRet = null;
    try {
      toRet = om.readValue(json, Root.class);
      ArrayList<Features> o = toRet.features;

    } catch (JsonProcessingException e) {
      e.printStackTrace();
    }
    return toRet;

  }
}
