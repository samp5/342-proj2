package views.util;

import java.util.concurrent.CompletableFuture;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;

/**
 * A collection of data corresponding to real-life cities.
 * Reads a local SQL database to create the data.
 */
public class CityData {
  // list of cities
  CompletableFuture<ObservableList<String>> cityList;

  /**
   * Contains data related to cities, such as location and name
   */
  public class City {
    public String state, shortState, cityName, county;
    public double lon, lat;
    public String display;

    /**
     * Create a new {@code City} with each of its stored data points.
     *
     * @param state the state the city is in
     * @param shortState the state abbreviation for {@code state}
     * @param cityName the name of the city
     * @param county the county of this city instance
     * @param lat the latitude of the city
     * @param lon the longitude of the city
     */
    public City(
        String state, String shortState,
        String cityName, String county,
        double lat, double lon) {
      this.state = state;
      this.shortState = shortState;
      this.cityName = cityName;
      this.county = county;
      this.lat = lat;
      this.lon = lon;
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

    // parse the data from the db
    try (Connection conn = DriverManager.getConnection("jdbc:sqlite:" + url)) {
      System.out.println("Connection to SQL has been established");
      Statement stmt = conn.createStatement();
      ResultSet rs = stmt.executeQuery(sql);

      while (rs.next()) {
        l.add(
          new City(
            rs.getString("STATE_NAME"),
            rs.getString("STATE_CODE"),
            rs.getString("CITY"),
            rs.getString("COUNTY"),
            rs.getDouble("LATITUDE"),
            rs.getDouble("LONGITUDE")
          )
        );
      }
    } catch (SQLException e) {
      System.err.println(e.getMessage());
    }
    return l;
  }
}
