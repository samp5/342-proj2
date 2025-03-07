package views.util;

import java.util.Optional;

import javafx.scene.Node;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import views.components.events.NotificationEvent;

public class NotificationBuilder {

  Optional<String> imagePath = Optional.empty();
  NotificationType type = NotificationType.Info;
  String message = "";
  Integer showDuration = 2;

  public NotificationBuilder() {}

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
        this.imagePath = Optional.of("/notification_icons/wifi.png");
        break;
      default:
        this.imagePath = Optional.empty();
        break;
    }
  }

}
