
package views.components.events;

import javafx.event.Event;
import javafx.event.EventType;

public class LocationChangeEvent extends Event {
  public static final EventType<LocationChangeEvent> LOCATIONCHANGE = new EventType<>(Event.ANY, "LOCATIONCHANGE");

  final double lat;
  final double lon;

  public LocationChangeEvent(double lat, double lon) {
    super(LOCATIONCHANGE);

    this.lat = lat;
    this.lon = lon;
  }

  public final double[] getPosition() {
    return new double[] {lat, lon};
  }
  
  public final double getLat() {
    return lat;
  }

  public final double getLon() {
    return lon;
  }
}
