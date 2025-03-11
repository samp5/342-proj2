
package views.components.events;

import javafx.event.Event;
import javafx.event.EventType;

/**
 * an {@code Event} that fires when the set location of the weather has changed
 */
public class LocationChangeEvent extends Event {
  public static final EventType<LocationChangeEvent> LOCATIONCHANGE = new EventType<>(Event.ANY, "LOCATIONCHANGE");

  final double lat;
  final double lon;
  final String name;

  /**
   * create a new {@code LocationChangeEvent} for selecting a new location
   *
   * @param lat the latitude point
   * @param lon the longitude point
   */
  public LocationChangeEvent(double lat, double lon, String cityName) {
    super(LOCATIONCHANGE);

    this.lat = lat;
    this.lon = lon;
    this.name = cityName;
  }

  /**
   * get the position as an array of two points
   *
   * @return a size two array of doubles in the form of {lat, lon}
   */
  public final double[] getPosition() {
    return new double[] { lat, lon };
  }

  /**
   * get the latitude point
   *
   * @return the new latitude point
   */
  public final double getLat() {
    return lat;
  }

  /**
   * get the longitude point
   *
   * @return the new longitude point
   */
  public final double getLon() {
    return lon;
  }

  /**
   * get the name of the city point
   *
   * @return the name of the city
   */
  public final String getName() {
    return name;
  }
}
