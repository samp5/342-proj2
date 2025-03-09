package views.util;

import java.util.concurrent.CompletableFuture;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;

public class CityData {
  CompletableFuture<ObservableList<String>> cityList;

  public class City {
    public String state, shortState, cityName, county;
    public double lon, lat;
    public String display;

    public City(
        String state, String shortState,
        String cityName, String county,
        double lon,
        double lat) {
      this.state = state;
      this.shortState = shortState;
      this.cityName = cityName;
      this.county = county;
      this.lon = lon;
      this.lat = lat;
      this.display = cityName + ", " + shortState;
    }
  }

  public CityData() {}

  public ObservableList<City> getCityList() {

    ObservableList<City> l = FXCollections.observableArrayList();

    // get path to our db
    String url = getClass().getResource("/db/us_cities.db").toExternalForm();
    String sql =
        "select CITY, STATE_NAME, STATE_CODE, COUNTY, LATITUDE, LONGITUDE from US_CITIES inner join US_STATES on US_CITIES.ID_STATE = US_STATES.ID";

    try (Connection conn = DriverManager.getConnection("jdbc:sqlite:" + url)) {
      System.out.println("Connection  to SQL has been established");
      Statement stmt = conn.createStatement();
      ResultSet rs = stmt.executeQuery(sql);

      while (rs.next()) {
        l.add(new City(
            rs.getString("STATE_NAME"),
            rs.getString("STATE_CODE"),
            rs.getString("CITY"),
            rs.getString("COUNTY"),
            rs.getDouble("LONGITUDE"),
            rs.getDouble("LATITUDE")));
      }

    } catch (SQLException e) {
      System.err.println(e.getMessage());
    }
    return l;
  }
}
