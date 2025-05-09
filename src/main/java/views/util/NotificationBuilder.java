package views.util;

import java.util.Optional;

import javafx.animation.PauseTransition;
import javafx.scene.Node;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.Duration;
import settings.Settings;
import views.components.events.NotificationEvent;

/**
 * Allows for simple building and sending of notifications to the current view.
 * Typically used for sending quick messages to the user for an error or an
 * update on their actions.
 */
public class NotificationBuilder {

  Optional<String> imagePath = Optional.empty();
  NotificationType type = NotificationType.Info;
  String message = "";
  Integer showDuration = 2;

  public NotificationBuilder() {
  }

  /**
   * Build a notification with {@code message}
   */
  public NotificationBuilder(String message) {
    this.message = message;
  }

  /**
   * Build a notification that shows for {@code seconds}
   */
  public NotificationBuilder showFor(int seconds) {
    this.showDuration = seconds;
    return this;
  }

  /**
   * Build a notification with type {@code type}
   */
  public NotificationBuilder ofType(NotificationType type) {
    this.type = type;
    setIconByType();
    return this;
  }

  /**
   * Build a notification with {@code message}
   */
  public NotificationBuilder withMessage(String message) {
    this.message = message;
    return this;
  }

  /**
   * Build a notification with an icon with path {@code path}
   */
  public NotificationBuilder withIcon(String path) {
    this.imagePath = Optional.of(path);
    return this;
  }

  /**
   * Send the notification as an event with origin {@code origin}
   */
  public void fire(Node origin) {
    VBox component = component();
    origin.fireEvent(new NotificationEvent(component, this.showDuration));
  }

  /**
   * Send the notification as an event
   *
   * @apiNote This method should only be called if it is not
   *          possible to get a reference to a currently attached
   *          {@code Node}
   */
  public void fire() {
    VBox component = component();
    Stage.getWindows().getFirst().sceneProperty().getValue().getRoot()
        .fireEvent(new NotificationEvent(component, this.showDuration));
  }

  /**
   * Send the notification as an event after some amount of delay
   *
   * This is used when an error is caused at a point in time where
   * a {@code Scene} may not be loaded, or more control around
   * notification timing is desired
   *
   * @apiNote This method should only be called if it is not
   *          possible to get a reference to a {@code Node}
   *          attached to the scene graph
   *          see {@code fireAfter(Duration, Node)}
   *
   * @apiNote This is a non blocking call
   * 
   * 
   */
  public void fireAfter(Duration d) {
    PauseTransition delay = new PauseTransition(d);
    delay.setOnFinished(event -> {
      this.fire();
    });

    delay.play();
  }

  /**
   * Send the notification as an event after some amount of delay
   *
   * This is used when an error is caused at a point in time where
   * a {@code Scene} may not be loaded, or more control around
   * notification timing is desired
   *
   * @apiNote This method should only be called if it is not
   *          possible to get a reference to a currently attached
   *          {@code Node}
   */
  public void fireAfter(Duration d, Node origin) {
    // We have to delay here because otherwise the scene will not be loaded, there
    // will be no windows
    PauseTransition delay = new PauseTransition(d);
    delay.setOnFinished(event -> {
      this.fire(origin);
    });

    delay.play();
  }

  /**
   * Get the component for the notification
   */
  private VBox component() {
    HBox innerBox = this.imagePath.map(path -> {
      Image img;
      try {
        img = new Image(path);
      } catch (Exception e) {
        e.printStackTrace();
        return null;
      }
      ImageView imgView = new ImageView();
      imgView.setImage(img);
      imgView.setPreserveRatio(true);
      imgView.setFitWidth(30);
      imgView.setFitHeight(30);
      imgView.setSmooth(true);

      Text message = new Text(this.message);
      message.setWrappingWidth(300.0);
      message.getStyleClass().add(textStyleClass());

      BorderPane pane = new BorderPane();
      pane.setCenter(imgView);
      return new HBox(pane, message);

    }).orElseGet(() -> {
      Text message = new Text(this.message);
      message.getStyleClass().add(textStyleClass());
      return new HBox(message);
    });
    innerBox.setSpacing(10);
    VBox component = new VBox(innerBox);
    component.getStyleClass().add(styleClass());
    return component;
  }

  /**
   * Get the {@code StyleClass} string name for this type of notification's text
   *
   * @return styleClass ths {@code String} that contains the style for this
   *         notification's text
   */
  private String textStyleClass() {
    switch (this.type) {
      case Error:
        return "error-notification-text";
      case Info:
        return "info-notification-text";
      case ConnectionError:
        return "error-notification-text";
      default:
        throw new MatchException("Unimplemented notification type", null);
    }
  }

  /**
   * Get the {@code StyleClass} string name for this type of notification
   *
   * @return styleClass ths {@code String} that contains the style for this
   *         notification
   */
  private String styleClass() {
    switch (this.type) {
      case Error:
        return "error-notification";
      case Info:
        return "info-notification";
      case ConnectionError:
        return "error-notification";
      default:
        throw new MatchException("Unimplemented notification type", null);
    }
  }

  /**
   * Set this notification's icon based on its type
   */
  private void setIconByType() {
    if (this.imagePath.isPresent()) {
      return;
    }

    switch (this.type) {
      case Error:
        this.imagePath = Optional.of("/notification_icons/error.png");
        break;
      case Info:
        this.imagePath = Optional.of("/notification_icons/info.png");
        break;
      case ConnectionError:
        switch (Settings.getTheme()) {
          case Dark:
            this.imagePath = Optional.of("/notification_icons/wifi_dark.png");
            break;
          case Light:
            this.imagePath = Optional.of("/notification_icons/wifi_light.png");
          default:
            break;
        }
        break;
      default:
        this.imagePath = Optional.empty();
        break;
    }
  }

}
