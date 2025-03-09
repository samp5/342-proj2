package views.util;

import javafx.event.Event;
import javafx.scene.Node;
import views.components.events.TempUnitEvent;

public class UnitHandler {
  public static enum TemperatureUnit {
    Fahrenheit,
    Celsius,
  }

  private static TemperatureUnit currentUnit;
  private static Node emitter;
  private static boolean changed = false;

  public static void setEmitter(Node emitter) {
    UnitHandler.emitter = emitter;
  }

  public static TemperatureUnit getUnit() {
    return currentUnit;
  }

  public static void setUnit(TemperatureUnit unit) {
    currentUnit = unit;
    changed = true;
    Event.fireEvent(emitter, new TempUnitEvent(unit));
  }

  public static void recognizeChange() {
    changed = false;
  }

  public static boolean hasChanged() {
    return changed;
  }
}
