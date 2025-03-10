package views.util;
 
import javafx.geometry.Pos;
import javafx.scene.control.TextField;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextBoundsType;

/**
 * Utilities based around text and {@code TextField}s. 
 * Allows for measurement and re-sizing of text, as well as creating new {@code Textfield}s.
 * Some code, particularly for text measurement, was taken directly from {@link https://stackoverflow.com/a/18608568}.
 */
public class TextUtils {
  static final Text helper;
  static final double DEFAULT_WRAPPING_WIDTH;
  static final double DEFAULT_LINE_SPACING;
  static final String DEFAULT_TEXT;
  static final TextBoundsType DEFAULT_BOUNDS_TYPE;
  static {
    helper = new Text();
    DEFAULT_WRAPPING_WIDTH = helper.getWrappingWidth();
    DEFAULT_LINE_SPACING = helper.getLineSpacing();
    DEFAULT_TEXT = helper.getText();
    DEFAULT_BOUNDS_TYPE = helper.getBoundsType();
  }

  /**
   * calculates the width of a given text element
   *
   * @param font the {@code Font} for the text
   * @param text the text to compute the width of
   * @param help0 value to compare with for finding the preffered width. typically 0.
   */
  public static double computeTextWidth(Font font, String text, double help0) {
    helper.setText(text);
    helper.setFont(font);

    helper.setWrappingWidth(0.0D);
    helper.setLineSpacing(0.0D);
    double d = Math.min(helper.prefWidth(-1.0D), help0);
    helper.setWrappingWidth((int) Math.ceil(d));
    d = Math.ceil(helper.getLayoutBounds().getWidth());

    helper.setWrappingWidth(DEFAULT_WRAPPING_WIDTH);
    helper.setLineSpacing(DEFAULT_LINE_SPACING);
    helper.setText(DEFAULT_TEXT);
    return d;
  }

  /**
   * computes then sets the text field's width to the width of its content
   *
   * @param t the {@code TextField} to resize
   * @param padding any additional padding to add to the width
   */
  public static void setFitWidth(TextField t, double padding) {
    double width = TextUtils.computeTextWidth(t.getFont(), t.getText(), 0.0D) + padding;
    t.setMaxWidth(width);
    t.setMinWidth(width);
    t.setPrefWidth(width);
  }

  /**
   * computes then sets the text field's width to the width of its content with a padding of 10
   *
   * @param t the {@code TextField} to resize
   */
  public static void setFitWidth(TextField t) {
    setFitWidth(t, 10);
  }

  /**
   * creates a new textfield that cannot be editied and is automatically centered
   *
   * @param s the text for the {@code TextField}
   * @return a new {@code TextField}
   */
  public static TextField staticTextField(String s) {
    TextField tf = new TextField(s);
    tf.setAlignment(Pos.CENTER);
    tf.setEditable(false);

    return tf;
  }
}
