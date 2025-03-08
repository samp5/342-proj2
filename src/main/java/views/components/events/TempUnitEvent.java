package views.components.events;

import javafx.event.Event;
import javafx.event.EventType;
import views.util.UnitHandler.TemperatureUnit;

public class TempUnitEvent extends Event {
  public static final EventType<TempUnitEvent> TEMPUNITCHANGE = new EventType<>(Event.ANY, "TEMPUNITCHANGE");

  private final TemperatureUnit unit;

  public TempUnitEvent(TemperatureUnit unit) {
    super(TEMPUNITCHANGE);

    this.unit = unit;
  }

  public TemperatureUnit getUnit() {
    return unit;
  }
}
