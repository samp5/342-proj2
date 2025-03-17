package views.util;

import javafx.event.Event;
import javafx.scene.Node;
import settings.Settings;
import views.components.events.TempUnitEvent;

/**
 * A global unit manager and handler.
 * Tracks the current state of and allows for the firing of events relating to
 * the currently used
 * units.
 */
public class UnitHandler {
  /**
   * allowed temperature units
   */
  public static enum TemperatureUnit {
    Fahrenheit, Celsius,
  }

  private static TemperatureUnit currentUnit; // current unit
  private static Node emitter; // the element to fire events
  private static boolean changed = false; // whether or not the unit has changed

  /**
   * set the event emitter
   *
   * @param emitter the {@code Node} to fire events
   */
  public static void setEmitter(Node emitter) {
    UnitHandler.emitter = emitter;
  }

  /**
   * get the current unit
   *
   * @return the unit currently used
   */
  public static TemperatureUnit getUnit() {
    return currentUnit;
  }

  /**
   * get the current unit as a character
   *
   * @return the {@code char} representation of the current temperature unit
   */
  public static char getUnitChar() {
    if (currentUnit == TemperatureUnit.Fahrenheit)
      return 'F';
    return 'C';
  }

  /**
   * set the current unit
   *
   * @param unit the unit to set to
   */
  public static void setUnit(TemperatureUnit unit) {
    currentUnit = unit;
    Settings.setTempUnit(unit);
    changed = true;

    if (emitter != null)
      Event.fireEvent(emitter, new TempUnitEvent(unit));
  }

  /**
   * clear the {@code hasChanged} flag
   */
  public static void recognizeChange() {
    changed = false;
  }

  /**
   * whether or not the temperature unit has changed
   *
   * @return {@code true} if the temperature has changed, {@code false} otherwise
   */
  public static boolean hasChanged() {
    return changed;
  }
}
