package views.components.events;

import javafx.event.Event;
import javafx.event.EventType;
import views.util.UnitHandler.TemperatureUnit;

/**
 * an {@code Event} that fires when the {@code TemperatureUnit} changes
 */
public class TempUnitEvent extends Event {
  public static final EventType<TempUnitEvent> TEMPUNITCHANGE = new EventType<>(Event.ANY, "TEMPUNITCHANGE");

  private final TemperatureUnit unit;

  /**
   * create a new {@code TempUnitEvent} for changing the {@code TemperatureUnit}
   *
   * @param selectedDay the day to select
   */
  public TempUnitEvent(TemperatureUnit unit) {
    super(TEMPUNITCHANGE);

    this.unit = unit;
  }

  /**
   * get the new {@code TemperatureUnit}
   *
   * @return the new {@code TemperatureUnit}
   */
  public TemperatureUnit getUnit() {
    return unit;
  }
}
