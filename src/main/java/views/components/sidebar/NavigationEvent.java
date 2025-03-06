package views.components.sidebar;

import javafx.event.Event;
import javafx.event.EventType;
import javafx.scene.Scene;

public class NavigationEvent extends Event {
  public static final EventType<NavigationEvent> NAVIGATE = new EventType<>(Event.ANY, "NAVIGATE");

  private final Scene targetScene;

  public NavigationEvent(Scene targetScene) {
    super(NAVIGATE);
    this.targetScene = targetScene;
  }

  public Scene getTargetScene() {
    return targetScene;
  }
}
