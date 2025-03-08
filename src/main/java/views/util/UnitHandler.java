package views.util;

import javafx.event.Event;
import javafx.event.EventTarget;
import views.components.events.TempUnitEvent;

public class UnitHandler {
  public static enum TemperatureUnit {
    Fahrenheit,
    Celsius,
  }

  private static TemperatureUnit currentUnit;
  private static EventTarget emitter;

  public static void setEmitter(EventTarget emitter) {
    UnitHandler.emitter = emitter;
  }

  public static TemperatureUnit getUnit() {
    return currentUnit;
  }

  public static void setUnit(TemperatureUnit unit) {
    currentUnit = unit;
    Event.fireEvent(emitter, new TempUnitEvent(unit));
  }
}
