package views.components.sidebar;

import javafx.event.Event;
import javafx.event.EventType;
import views.DayScene;

/**
 * an {@code Event} that fires when changing the view to a new {@code Scene}
 */
public class NavigationEvent extends Event {
  public static final EventType<NavigationEvent> NAVIGATE = new EventType<>(Event.ANY, "NAVIGATE");

  private final DayScene targetScene;

  /**
   * create a new {@code NavigationEvent} for selecting a new {@code Scene}
   *
   * @param selectedDay the day to select
   */
  public NavigationEvent(DayScene targetScene) {
    super(NAVIGATE);
    this.targetScene = targetScene;
  }

  /**
   * get the target {@code Scene}
   *
   * @return the target {@code Scene}
   */
  public DayScene getTargetScene() {
    return targetScene;
  }
}
