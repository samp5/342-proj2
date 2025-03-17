package views.components.events;

import javafx.event.Event;
import javafx.event.EventType;
import javafx.scene.layout.VBox;

/**
 * an {@code Event} that fires when a {@code Notification} is sent
 */
public class NotificationEvent extends Event {
  public static final EventType<NotificationEvent> NOTIFCATION = new EventType<>(Event.ANY, "NOTIFCATION");

  private final VBox innerComponent;
  private final int duration;

  /**
   * create a new {@code NotificationEvent} with an element and duration of 2
   * seconds
   *
   * @param element the element to use as a notification
   */
  public NotificationEvent(VBox element) {
    super(NOTIFCATION);

    this.innerComponent = element;
    this.duration = 2;
  }

  /**
   * create a new {@code NotificationEvent} with an element and duration
   *
   * @param element  the element to use as a notification
   * @param duration the length of time in seconds to display the notification
   */
  public NotificationEvent(VBox element, int duration) {
    super(NOTIFCATION);

    this.innerComponent = element;
    this.duration = duration;
  }

  /**
   * get the stored component
   *
   * @return the {@code VBox} component stored
   */
  public VBox component() {
    return innerComponent;
  }

  /**
   * get the stored duration
   *
   * @return the duration stored in seconds
   */
  public int duration() {
    return duration;
  }
}
