package weather_observations;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import weather_observations.stations.Stations;

public class WeatherObservations {
  public static Properties getWeatherObservations(String region, int gridX, int gridY, double lat, double lon) {
    String station = Stations.getNearestStation(region, gridX, gridY, lat, lon);
    HttpRequest request = HttpRequest.newBuilder()
        .uri(URI.create("https://api.weather.gov/stations/" + station + "/observations/latest"))
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
    return r.properties;
  }

  public static Root getObject(String json) {
    ObjectMapper om = new ObjectMapper();
    Root toRet = null;
    try {
      toRet = om.readValue(json, Root.class);
      Properties p = toRet.properties;

    } catch (JsonProcessingException e) {
      e.printStackTrace();
    }
    return toRet;

  }
}
