
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.Vector;
import java.util.stream.Collectors;

import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import my_weather.HourlyPeriod;

/**
 * TempGraph builds from the an {@code ArrayList<data>}
 *
 *
 */
class TempGraph {
  Vector<DataPoint> data;
  TemperatureLimits temp_limits;

  public class TemperatureLimits {
    Temperature min_temp;
    Temperature max_temp;

    public TemperatureLimits(Temperature min, Temperature max) {
      min_temp = min;
      max_temp = max;
    }

    public int max() {
      return max_temp.value();
    }

    public int min() {
      return min_temp.value();
    }

    public int padRange() {
      return (int) Math.floor(0.1 * min());
    }

    public int intervalWithSteps(int steps) {
      return Math.ceilDiv(max() - min(), steps);
    }
  }

  public class Hour {
    public int hour;

    public Hour(int h) {
      this.hour = h;
    }

    public int value() {
      return this.hour;
    }
  }

  private class Temperature {
    public int temp;

    public Temperature(int t) {
      this.temp = t;
    }

    public int value() {
      return this.temp;
    }
  }

  private class DataPoint implements Comparable<DataPoint> {
    private Temperature temp;
    private Hour hour;

    public DataPoint(int h, int t) {
      this.hour = new Hour(h);
      this.temp = new Temperature(t);
    }

    public int temperature() {
      return this.temp.value();
    }

    public int hour() {
      return this.hour.value();
    }

    // NOTE: This is the reccomeneded way to do this,
    // see the tutorial for LineCharts here:
    // https://docs.oracle.com/javafx/2/charts/line-chart.htm
    @SuppressWarnings({ "rawtypes", "unchecked" })
    public XYChart.Data asPoint() {
      return new XYChart.Data(this.hour.hour, this.temp.temp);
    }

    @Override
    public int compareTo(TempGraph.DataPoint arg0) {
      if (this.hour() < arg0.hour()) {
        return -1;
      } else if (this.hour() > arg0.hour()) {
        return 1;
      } else {
        return 0;
      }
    }
  }

  @SuppressWarnings("deprecation")
  public TempGraph(ArrayList<HourlyPeriod> data, Date day) {
    initializeFromData(data, day);
  };

  private <T extends Iterable<HourlyPeriod>> void initializeFromData(T data, Date day) {
    if (this.data != null) {
      this.data.clear();
    } else {
      this.data = new Vector<>();
    }

    int min_temp = Integer.MAX_VALUE;
    int max_temp = Integer.MIN_VALUE;

    for (HourlyPeriod h : data) {
      if (h.startTime.getDay() != day.getDay()) {
        continue;
      }

      if (h.temperature < min_temp) {
        min_temp = h.temperature;
      }
      if (h.temperature > max_temp) {
        max_temp = h.temperature;
      }

      this.data.add(new DataPoint(h.startTime.getHours(), h.temperature));
    }

    this.temp_limits = new TemperatureLimits(new Temperature(min_temp), new Temperature(max_temp));

    // Sort by hour data
    Collections.sort(this.data);
  }

  // NOTE: This is the recommended way to do this,
  // see the tutorial for LineCharts here:
  // https://docs.oracle.com/javafx/2/charts/line-chart.htm
  @SuppressWarnings("unchecked")
  public LineChart<Number, Number> component() {
    NumberAxis hourAxis = new NumberAxis(data.firstElement().hour(), data.lastElement().hour(), 1);
    NumberAxis tempAxis = new NumberAxis(temp_limits.min() - temp_limits.padRange(),

        temp_limits.max() + temp_limits.padRange(),
        temp_limits.intervalWithSteps(15));

    TempGraph.styleTimeAxis(hourAxis);
    TempGraph.styleTempAxis(tempAxis);

    LineChart<Number, Number> lineChart = new LineChart<>(hourAxis, tempAxis);

    XYChart.Series<Number, Number> series = new XYChart.Series<>();

    for (DataPoint d : this.data) {
      series.getData().add(d.asPoint());
    }
    lineChart.getData().addAll(series);
    TempGraph.styleChart(lineChart);

    return lineChart;
  };

  static public void styleTimeAxis(NumberAxis hours) {
    hours.setMinorTickVisible(false);
    hours.setLabel("hello");
  }

  static public void styleTempAxis(NumberAxis temp) {
    temp.setMinorTickVisible(false);
    temp.setLabel("hello ");
  }

  static public void styleChart(LineChart<Number, Number> component) {
    component.setHorizontalGridLinesVisible(false);
    component.setVerticalGridLinesVisible(false);
  }

  public void update(ArrayList<HourlyPeriod> data, Date day) {
    initializeFromData(data, day);
  };

}
