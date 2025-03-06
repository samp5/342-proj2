package views.components.sidebar;

import javafx.event.Event;
import javafx.event.EventType;
import views.DayScene;

public class NavigationEvent extends Event {
  public static final EventType<NavigationEvent> NAVIGATE = new EventType<>(Event.ANY, "NAVIGATE");

  private final DayScene targetScene;

  public NavigationEvent(DayScene targetScene) {
    super(NAVIGATE);
    this.targetScene = targetScene;
  }

  public DayScene getTargetScene() {
    return targetScene;
  }
}
