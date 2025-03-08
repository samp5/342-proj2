package views.components;

import java.util.Date;
import java.util.Vector;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.chart.AreaChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.layout.Background;
import javafx.scene.layout.Border;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.util.StringConverter;
import my_weather.HourlyPeriod;

/**
 * TempGraph holds state for a temperature
 * line graph. The component can be obtained via {@code TempGraph.component()}
 */
public class TempGraph {
  Vector<DataPoint> data;
  TemperatureLimits temp_limits;
  Date min_time;
  static long MILLISECONDS_IN_THREE_HOURS = 60 * 60 * 3 * 1000;

  public enum TempUnit {
    Fahrenheit, Celsius,
  }

  /**
   * Minimum and maximum {@code Temperature}s
   */
  public class TemperatureLimits {
    Temperature min_temp;
    Temperature max_temp;
    static final double PAD_PERCENT = 0.45;
    static final double PAD_CONSTANT = 5;

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
      return (int) Math.floor(PAD_PERCENT * min() + PAD_CONSTANT);
    }

    /**
     * The vertical padding that should be included above and below
     * the minimum and maximum values to ensure "breathing room"
     * for the graph.
     *
     * @return integer value between {@code 0} and the value returned by
     *         {@code TemperatureLimits.min()}
     */
    public int padDown() {
      return (int) Math.floor(PAD_PERCENT * min() - PAD_CONSTANT);
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
    private Date date;

    public DataPoint(Date day, int t) {
      this.date = day;
      this.temp = new Temperature(t);
    }

    /**
     * @return {@code int} value of the temperature
     */
    public int temperature() {
      return this.temp.value();
    }

    /**
     * @return {@code long} UNIX-time value of this date
     */
    public long time() {
      return this.date.getTime();
    }

    // NOTE: This is the recommended way to do this,
    // see the tutorial for LineCharts here:
    // https://docs.oracle.com/javafx/2/charts/line-chart.htm
    @SuppressWarnings({"rawtypes", "unchecked"})
    public XYChart.Data asPoint() {
      XYChart.Data data = new XYChart.Data(this.time(), this.temperature());
      return data;
    }

    @Override
    public int compareTo(TempGraph.DataPoint arg0) {
      if (this.time() < arg0.time()) {
        return -1;
      } else if (this.time() > arg0.time()) {
        return 1;
      } else {
        return 0;
      }
    }
  }

  /**
   * @param data {@code Iterable} container for {@code HourlyPeriod}
   * @param day {@code Date} object representing the target day to generate the
   *        graph
   * @param unit {@code TempUnit} which unit to use for the axis
   */
  public <T extends Iterable<HourlyPeriod>> TempGraph(T data, TempUnit unit) {
    initializeFromData(data, unit);
  };

  /**
   * Build container around {@code HourlyPeriod} extracting {@code DataPoint}s
   *
   * @param data {@code Iterable} container for {@code HourlyPeriod}
   * @param day {@code Date} object representing the target day to generate the
   *        graph
   * @param unit {@code TempUnit} which unit to use for the axis
   */
  private <T extends Iterable<HourlyPeriod>> void initializeFromData(T data, TempUnit unit) {
    if (this.data != null) {
      this.data.clear();
    } else {
      this.data = new Vector<>();
    }

    int min_temp = Integer.MAX_VALUE;
    int max_temp = Integer.MIN_VALUE;

    // we should only display the next 18 hours in 3 hour increments
    int point_to_collect = 18;
    int temperature;
    for (HourlyPeriod h : data) {
      if (unit == TempUnit.Celsius) {
        temperature = (h.temperature - 32) * 5 / 9;
      } else {
        temperature = h.temperature;
      }

      if (temperature < min_temp) {
        min_temp = temperature;
      }
      if (temperature > max_temp) {
        max_temp = temperature;
      }

      this.data.add(new DataPoint(h.startTime, temperature));

      if (this.data.size() >= point_to_collect) {
        break;
      }
    }

    this.min_time = data.iterator().next().startTime;
    this.temp_limits = new TemperatureLimits(new Temperature(min_temp), new Temperature(max_temp));
  }

  /**
   * Get a {@code javafx.scene.chart.LineChart} representing this
   * {@code TempGraph}
   *
   */
  @SuppressWarnings("unchecked")
  public VBox component() {

    // NOTE: This is the recommended way to do this,
    // see the tutorial for LineCharts here:
    // https://docs.oracle.com/javafx/2/charts/line-chart.htm
    NumberAxis hourAxis = new NumberAxis(data.firstElement().time(), data.lastElement().time(),
        MILLISECONDS_IN_THREE_HOURS);
    NumberAxis tempAxis;
    if (temp_limits.max() < 0) {
      tempAxis = new NumberAxis(temp_limits.min() + temp_limits.padDown(),
          temp_limits.max() - temp_limits.padDown(),
          10);
    } else {
      tempAxis = new NumberAxis(temp_limits.min() - temp_limits.pad(),
          temp_limits.max() + temp_limits.pad(),
          10);
    }

    this.styleTimeAxis(hourAxis);
    this.styleTempAxis(tempAxis);

    AreaChart<Number, Number> areaChart = new AreaChart<>(hourAxis, tempAxis);

    XYChart.Series<Number, Number> series = new XYChart.Series<>();

    for (DataPoint d : this.data) {
      series.getData().add(d.asPoint());
    }

    areaChart.getData().addAll(series);

    TempGraph.styleChart(areaChart);

    Text title = new Text("Temperature");
    title.getStyleClass().add("chart-title");

    HBox titleBox = new HBox(title);
    titleBox.setAlignment(Pos.TOP_LEFT);
    titleBox.setPadding(new Insets(40, 0, 0, 40));

    VBox box = new VBox(titleBox, areaChart);
    box.setMaxHeight(300);
    box.getStyleClass().add("chart-backdrop");

    return box;
  };


  /**
   * Update the contained data. Subsequent calls to {@code TempGraph.component()}
   * will represent this state.
   *
   * @param data {@code Iterable} container for {@code HourlyPeriod}
   *
   */
  public <T extends Iterable<HourlyPeriod>> void update(T data, TempUnit unit) {
    initializeFromData(data, unit);
  };

  /**
   * Style the given {@code NumberAxis}
   */
  public void styleTimeAxis(NumberAxis hours) {

    hours.setMinorTickVisible(false);
    hours.setLabel("");
    hours.setBorder(Border.EMPTY);
    hours.setTickLabelFont(new Font("Atkinson Hyperlegible Bold", 20));
    hours.setTickLabelFormatter(new StringConverter<Number>() {

      @SuppressWarnings("deprecation")
      @Override
      public String toString(Number object) {
        Date time = new Date(object.longValue());
        if (time.getTime() == min_time.getTime()) {
          return "Now";
        } else if (time.getHours() == 12) {
          return String.format("%d pm", 12);
        } else if (time.getHours() == 0) {
          return String.format("%d am", 12);
        } else if (time.getHours() > 12) {
          return String.format("%d pm", time.getHours() % 12);
        } else {
          return String.format("%d am", time.getHours());
        }
      }

      @Override
      public Number fromString(String string) {
        throw new UnsupportedOperationException("Unimplemented method 'fromString'");
      }
    });
  }

  /**
   * Style the given {@code NumberAxis}
   */
  public void styleTempAxis(NumberAxis temp) {

    temp.setMinorTickVisible(false);
    temp.setLabel("");
    temp.setTickLabelFont(new Font("Atkinson Hyperlegible Bold", 18));
    temp.setBorder(Border.EMPTY);
    temp.setTickLabelFormatter(new StringConverter<Number>() {
      @Override
      public String toString(Number object) {
        return String.format("%d °", object.intValue());
      }

      @Override
      public Number fromString(String string) {
        throw new UnsupportedOperationException("Unimplemented method 'fromString'");
      }
    });
  }

  /**
   * Style the given {@code LineChart}
   */
  static public void styleChart(AreaChart<Number, Number> component) {
    component.getStyleClass().addAll("temp-graph");
    component.setHorizontalGridLinesVisible(false);
    component.setVerticalGridLinesVisible(false);
    component.setLegendVisible(false);
    component.setBackground(Background.EMPTY);
    component.setBorder(Border.EMPTY);
    component.setCreateSymbols(false);
  }

}
