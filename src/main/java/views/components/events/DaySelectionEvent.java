package views.components.events;

import javafx.event.Event;
import javafx.event.EventType;
import javafx.scene.layout.VBox;
import views.components.Day;

public class DaySelectionEvent extends Event {
  public static final EventType<DaySelectionEvent> DAY_SELECTION =
      new EventType<>(Event.ANY, "DAY_SELECTION");

  private final Day selection;

  public DaySelectionEvent(Day selectedDay) {
    super(DAY_SELECTION);

    this.selection = selectedDay;
  }

  public Day selection() {
    return selection;
  }

}
