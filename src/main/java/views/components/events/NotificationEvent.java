package views.components.events;

import javafx.event.Event;
import javafx.event.EventType;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;

public class NotificationEvent extends Event {
  public static final EventType<NotificationEvent> NOTIFCATION = new EventType<>(Event.ANY, "NOTIFCATION");

  private final VBox innerComponent;

  public NotificationEvent(VBox element) {
    super(NOTIFCATION);

    this.innerComponent = element;
  }

  public VBox component() {
    return innerComponent;
  }
}
