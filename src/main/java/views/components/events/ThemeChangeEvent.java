package views.components.events;

import javafx.event.Event;
import javafx.event.EventType;

/**
 * an {@code Event} that fires when the active theme changes
 */
public class ThemeChangeEvent extends Event {
  public static final EventType<ThemeChangeEvent> THEMECHANGE = new EventType<>(Event.ANY, "THEMECHANGE");

  private final String fileLoc;

  /**
   * create a new {@code ThemeChangeEvent} for changing the theme
   *
   * @param selectedDay the day to select
   */
  public ThemeChangeEvent(String fileLoc) {
    super(THEMECHANGE);

    this.fileLoc = fileLoc;
  }

  /**
   * get the new themes file location
   *
   * @return the new file location
   */
  public String getUnit() {
    return fileLoc;
  }
}
