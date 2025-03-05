package views.components;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.Vector;

import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import my_weather.HourlyPeriod;

/**
 * TempGraph holds state for a temperature
 * line graph. The component can be obtained via {@code TempGraph.component()}
 */
class TempGraph {
  Vector<DataPoint> data;
  TemperatureLimits temp_limits;

  /**
   * Minimum and maximum {@code Temperature}s
   */
  public class TemperatureLimits {
    Temperature min_temp;
    Temperature max_temp;
    static final double PAD_PERCENT = 0.1;

    public TemperatureLimits(Temperature min, Temperature max) {
      min_temp = min;
      max_temp = max;
    }

    /**
     * @return {@code int} value of the maximum temperature
     */
    public int max() {
      return max_temp.value();
    }

    /**
     * @return {@code int} value of the minimum temperature
     */
    public int min() {
      return min_temp.value();
    }

    /**
     * The vertical padding that should be included above and below
     * the minimum and maximum values to ensure "breathing room"
     * for the graph.
     *
     * @return integer value between {@code 0} and the value returned by
     *         {@code TemperatureLimits.min()}
     */
    public int pad() {
      return (int) Math.floor(PAD_PERCENT * min());
    }

    /**
     * The vertical padding that should be included above and below
     * the minimum and maximum values to ensure "breathing room"
     * for the graph.
     */
    public int intervalWithSteps(int steps) {
      return Math.ceilDiv(max() - min(), steps);
    }
  }

  public class Hour {
    private int hour;

    public Hour(int h) {
      this.hour = h;
    }

    /**
     * @return {@code int} value of the hour
     */
    public int value() {
      return this.hour;
    }
  }

  private class Temperature {
    private int temp;

    public Temperature(int t) {
      this.temp = t;
    }

    /**
     * @return {@code int} value of the temperature
     */
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

    /**
     * @return {@code int} value of the temperature
     */
    public int temperature() {
      return this.temp.value();
    }

    /**
     * @return {@code int} value of the hour
     */
    public int hour() {
      return this.hour.value();
    }

    // NOTE: This is the recommended way to do this,
    // see the tutorial for LineCharts here:
    // https://docs.oracle.com/javafx/2/charts/line-chart.htm
    @SuppressWarnings({"rawtypes", "unchecked"})
    public XYChart.Data asPoint() {
      XYChart.Data data = new XYChart.Data(this.hour.hour, this.temp.temp);
      return data;
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

  /**
   * @param data {@code Iterable} container for {@code HourlyPeriod}
   * @param day {@code Date} object representing the target day to generate the graph
   */
  public <T extends Iterable<HourlyPeriod>> TempGraph(T data, Date day) {
    initializeFromData(data, day);
  };

  /**
   * Build container around {@code HourlyPeriod} extracting {@code Hour} and {@code Temperature}
   * data
   *
   * @param data {@code Iterable} container for {@code HourlyPeriod}
   * @param day {@code Date} object representing the target day to generate the graph
   */
  @SuppressWarnings("deprecation")
  private <T extends Iterable<HourlyPeriod>> void initializeFromData(T data, Date day) {
    if (this.data != null) {
      this.data.clear();
    } else {
      this.data = new Vector<>();
    }

    int min_temp = Integer.MAX_VALUE;
    int max_temp = Integer.MIN_VALUE;

    for (HourlyPeriod h : data) {

      if (h.startTime.getDate() != day.getDate()) {
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

  /**
   * Get a {@code javafx.scene.chart.LineChart} representing this {@code TempGraph}
   *
   */
  @SuppressWarnings("unchecked")
  public LineChart<Number, Number> component() {

    // NOTE: This is the recommended way to do this,
    // see the tutorial for LineCharts here:
    // https://docs.oracle.com/javafx/2/charts/line-chart.htm
    NumberAxis hourAxis = new NumberAxis(data.firstElement().hour(), data.lastElement().hour(), 1);
    NumberAxis tempAxis = new NumberAxis(temp_limits.min() - temp_limits.pad(),
        temp_limits.max() + temp_limits.pad(),
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

  /**
   * Update the contained data. Subsequent calls to {@code TempGraph.component()}
   * will represent this state.
   *
   * @param data {@code Iterable} container for {@code HourlyPeriod}
   * @param day {@code Date} object representing the target day to generate the graph
   *
   */
  public <T extends Iterable<HourlyPeriod>> void update(T data, Date day) {
    initializeFromData(data, day);
  };

  /**
   * Style the given {@code NumberAxis}
   */
  static public void styleTimeAxis(NumberAxis hours) {

    // TODO: Finish this styling
    hours.setMinorTickVisible(false);
    hours.setLabel("");
  }

  /**
   * Style the given {@code NumberAxis}
   */
  static public void styleTempAxis(NumberAxis temp) {

    // TODO: Finish this styling
    temp.setMinorTickVisible(false);
    temp.setLabel("");
  }

  /**
   * Style the given {@code LineChart}
   */
  static public void styleChart(LineChart<Number, Number> component) {

    // TODO: Finish this styling
    component.setHorizontalGridLinesVisible(false);
    component.setVerticalGridLinesVisible(false);
  }


}
