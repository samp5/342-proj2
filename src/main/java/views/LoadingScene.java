package views;

public class LoadingScene extends DayScene {
  public LoadingScene() {
    initComponents();

    // add styles now that all elements exist
    styleComponents();

    // void any focus that may exist
    voidFocus();
  }

  /**
   * initialize all components. most components will be added to or modified later
   * during initialization
   */
  protected void initComponents() {
    super.initComponents();
  }

  /**
   * styles all components, sorted in groups
   */
  private void styleComponents() {
  }

  @Override
  protected void applyForecast() {
    return;
  }
}
