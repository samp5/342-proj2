package views.components.events;

import javafx.event.Event;
import javafx.event.EventType;
import views.components.Day;

/**
 * an {@code Event} that fires when the {@code Day} in a {@code ThreeDayScene} is clicked
 */
public class DaySelectionEvent extends Event {
  public static final EventType<DaySelectionEvent> DAY_SELECTION =
      new EventType<>(Event.ANY, "DAY_SELECTION");

  private final Day selection;

  /**
   * create a new {@code DaySelectionEvent} for selecting a new {@code Day}
   *
   * @param selectedDay the day to select
   */
  public DaySelectionEvent(Day selectedDay) {
    super(DAY_SELECTION);

    this.selection = selectedDay;
  }

  /**
   * get the selected {@code Day}
   *
   * @return the selected {@code Day}
   */
  public Day selection() {
    return selection;
  }

}
