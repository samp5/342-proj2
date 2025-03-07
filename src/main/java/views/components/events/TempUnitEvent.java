package views.components.events;

import javafx.event.Event;
import javafx.event.EventType;
import views.components.TempGraph.TempUnit;

public class TempUnitEvent extends Event {
  public static final EventType<TempUnitEvent> TEMPUNITCHANGE = new EventType<>(Event.ANY, "TEMPUNITCHANGE");

  private final TempUnit unit;

  public TempUnitEvent(TempUnit unit) {
    super(TEMPUNITCHANGE);

    this.unit = unit;
  }

  public TempUnit getUnit() {
    return unit;
  }
}
