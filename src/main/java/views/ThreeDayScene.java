package views;

import java.util.ArrayList;
import java.util.Collection;
import my_weather.HourlyPeriod;
//import views.components.DaySummary;

public class ThreeDayScene {
  Integer focusedDay = 0;
  //DaySummary threeDays;

  private void initComponents() {
  }

  private <T extends Collection<HourlyPeriod>> void initialized_days(T data) {
    // threeDays = new N_DaySummary(3, data);
  }

  public <T extends Collection<HourlyPeriod>> ThreeDayScene(T data) {
    initialized_days(data);
  }
}
