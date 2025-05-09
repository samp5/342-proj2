package views.components;

import java.util.Date;
import java.util.Vector;

import endpoints.my_weather.data.HourlyPeriod;
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

/**
 * {@code HumidityGraph} holds state for a humidity
 * line graph. The component can be obtained via
 * {@code HumidityGraph.component()}
 */
public class HumidityGraph {
  Vector<DataPoint> data;
  HumidityLimits humid_limits;
  Date min_time;
  static long MILLISECONDS_IN_THREE_HOURS = 60 * 60 * 3 * 1000;

  /**
   * Minimum and maximum {@code Temperature}s
   */
  public class HumidityLimits {
    Humidity min_humid;
    Humidity max_humid;
    static final double PAD_PERCENT = 0.2;

    public HumidityLimits(Humidity min, Humidity max) {
      min_humid = min;
      max_humid = max;
    }

    /**
     * @return {@code int} value of the maximum temperature
     */
    public int max() {
      return max_humid.value();
    }

    /**
     * @return {@code int} value of the minimum temperature
     */
    public int min() {
      return min_humid.value();
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

  /**
   * a class to store an hour
   */
  public class Hour {
    private int hour;

    /**
     * make a new hour
     *
     * @param h the hour
     */
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

  /**
   * a class to store a Humidity value
   */
  private class Humidity {
    private int temp;

    /**
     * make a new Humidity
     *
     * @param t the humidity
     */
    public Humidity(int t) {
      this.temp = t;
    }

    /**
     * @return {@code int} value of the temperature
     */
    public int value() {
      return this.temp;
    }
  }

  /**
   * a single {@code DataPoint} to be graphed. implements {@code Comparable} to
   * allow for propper plotting
   */
  private class DataPoint implements Comparable<DataPoint> {
    private Humidity temp;
    private Date date;

    /**
     * create a new {@code datapoint} given a day and humidity value
     *
     * @param day the {@code date} for the point
     * @param t   the humidity for the point
     */
    public DataPoint(Date day, int t) {
      this.date = day;
      this.temp = new Humidity(t);
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
    /**
     * get a datapoint for the {@code LineChart}
     */
    @SuppressWarnings({ "rawtypes", "unchecked" })
    public XYChart.Data asPoint() {
      XYChart.Data data = new XYChart.Data(this.time(), this.temperature());
      return data;
    }

    /**
     * part of {@code Comparable} implementation
     * compares another {@code DataPoint} to this one
     *
     * @return {@code 1} if {@code this > other}; {@code -1} if
     *         {@code this < other}; {@code 0} if {@code this == other}
     */
    @Override
    public int compareTo(HumidityGraph.DataPoint arg0) {
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
   * @param day  {@code Date} object representing the target day to generate the
   *             graph
   */
  public <T extends Iterable<HourlyPeriod>> HumidityGraph(T data, Date day) {
    initializeFromData(data, day);
  };

  /**
   * Build container around {@code HourlyPeriod} extracting {@code DataPoint}s
   *
   * @param data {@code Iterable} container for {@code HourlyPeriod}
   * @param day  {@code Date} object representing the target day to generate the
   *             graph
   */
  private <T extends Iterable<HourlyPeriod>> void initializeFromData(T data, Date day) {
    if (this.data != null) {
      this.data.clear();
    } else {
      this.data = new Vector<>();
    }

    int min_humid = Integer.MAX_VALUE;
    int max_humid = Integer.MIN_VALUE;

    // we should only display the next 18 hours in 3 hour increments
    int point_to_collect = 18;

    for (HourlyPeriod h : data) {

      if (h.relativeHumidity.value < min_humid) {
        min_humid = h.relativeHumidity.value;
      }
      if (h.relativeHumidity.value > max_humid) {
        max_humid = h.relativeHumidity.value;
      }

      this.data.add(new DataPoint(h.startTime, h.relativeHumidity.value));

      if (this.data.size() >= point_to_collect) {
        break;
      }
    }

    this.min_time = data.iterator().next().startTime;
    this.humid_limits = new HumidityLimits(new Humidity(min_humid), new Humidity(max_humid));
  }

  /**
   * Get a {@code javafx.scene.chart.LineChart} representing this
   * {@code TempGraph}
   *
   */
  @SuppressWarnings("unchecked")
  public VBox component() {

    // initialize the axes
    // NOTE: This is the recommended way to do this,
    // see the tutorial for LineCharts here:
    // https://docs.oracle.com/javafx/2/charts/line-chart.htm
    NumberAxis hourAxis = new NumberAxis(data.firstElement().time(), data.lastElement().time(),
        MILLISECONDS_IN_THREE_HOURS);
    NumberAxis humidAxis = new NumberAxis(humid_limits.min() - humid_limits.pad(),
        Math.min(humid_limits.max() + humid_limits.pad(), 100),
        10);

    // style the axes
    this.styleTimeAxis(hourAxis);
    this.styleHumidAxis(humidAxis);

    // create the chart and series
    AreaChart<Number, Number> areaChart = new AreaChart<>(hourAxis, humidAxis);
    XYChart.Series<Number, Number> series = new XYChart.Series<>();

    for (DataPoint d : this.data) {
      series.getData().add(d.asPoint());
    }
    areaChart.getData().addAll(series);

    // styling
    HumidityGraph.styleChart(areaChart);

    // add and style the title
    Text title = new Text("Relative Humidity");
    title.getStyleClass().add("chart-title");

    // add and style the title box
    HBox titleBox = new HBox(title);
    titleBox.setAlignment(Pos.TOP_LEFT);
    titleBox.setPadding(new Insets(40, 0, 0, 40));

    // place the title above the chart
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
   * @param day  {@code Date} object representing the target day to generate the
   *             graph
   *
   */
  public <T extends Iterable<HourlyPeriod>> void update(T data, Date day) {
    initializeFromData(data, day);
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

      /**
       * convert the tick label to proper AM/PM labels
       *
       * @return a {@code String} of the data label
       */
      @SuppressWarnings("deprecation")
      @Override
      public String toString(Number object) {
        Date time = new Date(object.longValue());
        if (time.getHours() == 12) {
          return String.format("%d pm", 12);
        } else if (time.getHours() == 0) {
          return String.format("%d am", 12);
        } else if (time.getHours() > 12) {
          return String.format("%d pm", time.getHours() % 12);
        } else {
          return String.format("%d am", time.getHours());
        }
      }

      /**
       * [IGNORE]
       */
      @Override
      public Number fromString(String string) {
        throw new UnsupportedOperationException("Unimplemented method 'fromString'");
      }

    });
  }

  /**
   * Style the given {@code NumberAxis}
   */
  public void styleHumidAxis(NumberAxis temp) {
    temp.setMinorTickVisible(false);
    temp.setLabel("");
    temp.setTickLabelFont(new Font("Atkinson Hyperlegible Bold", 18));
    temp.setBorder(Border.EMPTY);
    temp.setTickLabelFormatter(new StringConverter<Number>() {

      /**
       * convert the datapoint to proper percentage label
       *
       * @return a {@code String} of the percentage label
       */
      @Override
      public String toString(Number object) {
        return String.format("%d", object.intValue()) + "% ";
      }

      /**
       * [IGNORE]
       */
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
    component.getStyleClass().add("humid-graph");
    component.setHorizontalGridLinesVisible(false);
    component.setVerticalGridLinesVisible(false);
    component.setLegendVisible(false);
    component.setBackground(Background.EMPTY);
    component.setBorder(Border.EMPTY);
    component.setCreateSymbols(false);
  }
}
