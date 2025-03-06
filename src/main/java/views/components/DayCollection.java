package views.components;

import java.util.ArrayList;
import java.util.Date;

import javafx.geometry.Insets;
import javafx.scene.layout.HBox;
import my_weather.HourlyPeriod;
import views.components.Day.DayView.DayViewType;

public class DayCollection {
  ArrayList<Day> days;
  DayViewType viewType;

  @SuppressWarnings("deprecation")
  public DayCollection(int number_days, ArrayList<HourlyPeriod> data, DayViewType viewType) {
    this.days = new ArrayList<>();
    this.viewType = viewType;

    Date last_day = data.getFirst().startTime;

    days.add(new Day(data, last_day));

    int collected_days = 1;

    for (HourlyPeriod h : data) {

      if (collected_days >= number_days) {
        break;
      }

      if (h.startTime.getDate() == last_day.getDate()) {
        continue;
      } else {
        last_day = h.startTime;
        days.add(new Day(data, last_day));
        collected_days += 1;
      }
    }
  }

  public HBox component() {
    HBox box = new HBox();
    box.getChildren().addAll(days.stream().map(d -> d.component(this.viewType)).toList());
    box.setPadding(new Insets(50));
    return box;
  }

}
