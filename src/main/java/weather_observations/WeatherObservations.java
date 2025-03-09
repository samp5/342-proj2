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

public class WeatherObservations {
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

  public static Observations getWeatherObservations(String region, int gridX, int gridY, double lat, double lon) throws ConnectException {
    String station = Stations.getNearestStation(region, gridX, gridY, lat, lon);
    HttpRequest request = HttpRequest.newBuilder()
        .uri(URI.create("https://api.weather.gov/stations/" + station + "/observations/latest"))
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
    return r.properties;
  }

  public static Root getObject(String json) {
    ObjectMapper om = new ObjectMapper();
    Root toRet = null;
    try {
      toRet = om.readValue(json, Root.class);
      Observations p = toRet.properties;

    } catch (JsonProcessingException e) {
      e.printStackTrace();
    }
    return toRet;

  }
}
