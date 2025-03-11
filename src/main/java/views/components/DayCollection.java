package views.components;

import java.util.ArrayList;
import java.util.Date;

import endpoints.my_weather.data.HourlyPeriod;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import views.components.Day.DayView.DayViewType;
import views.components.events.DaySelectionEvent;

/**
 * Creates a collection of {@code Day} objects, for use in various
 * {@code DayScene}s.
 * Allows for collection of any number of days, given the appropriate
 * constructor call.
 */
public class DayCollection {
  ArrayList<Day> days;
  DayViewType viewType;
  Region component = null;

  /**
   * create a new group of {@code Day}s, given a count, large forecast, and a view
   * type
   *
   * @param number_days the count of days to put in the collection
   * @param data        a forecast containing at least enough data for the
   *                    {@code number_days} given
   * @param viewType    a viewType for styling. @see DayViewType
   */
  @SuppressWarnings("deprecation")
  public DayCollection(int number_days, ArrayList<HourlyPeriod> data, DayViewType viewType) {
    this.days = new ArrayList<>();
    this.viewType = viewType;

    Date previousDay = data.getFirst().startTime;
    days.add(new Day(data, previousDay));

    int collected_days = 1;
    for (HourlyPeriod h : data) {

      if (collected_days >= number_days) {
        break;
      }

      if (h.startTime.getDate() == previousDay.getDate()) {
        continue;
      } else {
        previousDay = h.startTime;
        days.add(new Day(data, previousDay));
        collected_days += 1;
      }
    }
  }

  /**
   * get the component for this {@code DayCollection}
   *
   * @return the {@code Region} containing each {@code Day} in this
   *         {@code DayCollection}
   */
  public Region component() {
    switch (this.viewType) {
      case TenDay:
        component = new VBox();
        ((VBox) component).getChildren().addAll(days.stream().map(d -> d.component(this.viewType)).toList());
        component.setPadding(new Insets(50));
        ((VBox) component).setSpacing(60);
        break;
      case ThreeDay:
        component = new HBox();
        ((HBox) component).getChildren().addAll(days.stream().map(d -> d.component(this.viewType)).toList());
        component.setPadding(new Insets(50));
        ((HBox) component).setSpacing(60);
        ((HBox) component).setAlignment(Pos.CENTER);

        break;
      default:
        break;
    }
    this.component.addEventHandler(DaySelectionEvent.DAY_SELECTION, selection -> {
      days.forEach(d -> {
        d.deselect();
      });
      selection.selection().select();
    });
    return component;
  }

  public ArrayList<Day> getDays() {
    return this.days;
  }
}
