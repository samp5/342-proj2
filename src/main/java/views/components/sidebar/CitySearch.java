package views.components.sidebar;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.control.skin.ListViewSkin;
import javafx.scene.control.skin.VirtualFlow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.shape.SVGPath;
import javafx.scene.text.Text;
import views.components.events.LocationChangeEvent;
import views.util.CityData;
import views.util.NotificationBuilder;
import views.util.NotificationType;
import views.util.CityData.City;
import javafx.util.Callback;
import javafx.util.Duration;

/**
 * A component for allowing a user to search for cities.
 * Get the component via the {@code CitySearch.component()} method.
 */
public class CitySearch {

  // Frame for the entire section (search bar + results)
  BorderPane content;

  // results
  ListView<City> cityListView;

  // Underlying {@code City} collection
  ObservableList<City> cityList;
  FilteredList<City> filteredList;

  // Underlying SQL access class
  CityData cityData;

  // Search box elements
  BorderPane searchIcon;
  TextField searchInput;
  HBox searchBox;

  /**
   * {@link CitySearch}
   * Holds state for searching all US Cities.
   *
   */
  public CitySearch() {
    cityData = new CityData();
    initComponents();
  }

  /**
   * initialize all components. most components will be added to or modified later
   * during initialization
   */
  public void initComponents() {
    // Frame for the entire section (search bar + results)
    content = new BorderPane();

    // results
    cityListView = new ListView<>();

    // Search box elements
    searchIcon = new BorderPane();
    searchInput = new TextField();
    searchBox = new HBox();
  }

  /**
   * get the {@code BorderPane} component
   *
   * @return the {@code BorderPane} component where:
   *         The top is the search box
   *         The center is the serach results
   */
  public BorderPane component() {

    // Attempt to get the city list from {@code cityData}
    try {
      this.cityList = cityData.getCityList();
    } catch (Exception e) {

      // For any exception (SQLException or otherwise), notify the user and continue
      // with an empty (unusable) list.
      this.cityList = FXCollections.observableArrayList();

      // we have to delay here otherwise the scene won't be loaded!
      new NotificationBuilder().ofType(NotificationType.Error)
          .withMessage("Failed to load city data, city search will not be available").showFor(3)
          .fireAfter(Duration.seconds(2));;
    }

    // build our filtered list
    filteredList = new FilteredList<>(this.cityList);

    // Load our search icon and set dimensions
    Region imgView = new Region();
    SVGPath img = new SVGPath();
    img.setContent("M 0 5 A 1 1 0 0 0 5.85 5 A 1 1 0 0 0 0 5 M 5.8 5.5L 10 5.5 L 10 4.5L 5.8 4.5 z M 1 5 A 1 1 0 0 1 4.85 5 A 1 1 0 0 1 1 5");
    imgView.setShape(img);
    imgView.setMinSize(30, 18);
    imgView.setPrefSize(30, 18);
    imgView.setMaxSize(30, 18);
    imgView.setRotate(45);
    imgView.getStyleClass().add("search-icon");

    searchIcon.setCenter(imgView);

    // fill our search box
    searchBox.getChildren().setAll(searchIcon, searchInput);

    // Intialize our FilteredList and set the cell factory
    cityListView.setItems(filteredList);
    cityListView.setCellFactory(new CityCellFactory());
    cityListView.setEditable(false);

    // Fill our border pane
    content.setCenter(cityListView);
    content.setTop(searchBox);

    registerEventHandlers();
    styleComponents();

    return content;
  }

  private void styleComponents() {

    // style
    cityListView.getStyleClass().add("search-results");

    // Until the user enters text into the search box, the results should be empty
    cityListView.setVisible(false);

    // add style classes for our search box
    searchBox.getStyleClass().add("search-box");
    searchInput.getStyleClass().add("search-input");

    content.getStyleClass().add("search");
  }

  private void registerEventHandlers() {
    // Listen to any changes on the input field and adjust the filter
    // on our listview. This is the core of our "search".
    //
    // As an additional side effect, always select the top result
    searchInput.textProperty().addListener(obs -> {
      String filter = searchInput.getText();
      if (filter == null || filter.length() == 0) {
        cityListView.setVisible(false);
        filteredList.setPredicate(s -> false);
      } else {
        cityListView.setVisible(true);
        filteredList
            .setPredicate(city -> city.display.toLowerCase().contains(filter.toLowerCase()));
      }
      if (!filteredList.isEmpty()) {
        cityListView.getSelectionModel().select(0);

        // make sure that the first result is visible to user
        cityListView.scrollTo(0);
      }
    });

    searchInput.setOnAction(e -> {
      City c = cityListView.getSelectionModel().getSelectedItem();

      if (c == null) {
        searchInput.clear();
        return;
      }

      content.fireEvent(new LocationChangeEvent(c.lat, c.lon, c.display));
      searchInput.clear();
    });

    searchInput.setOnKeyPressed(key -> {
      // if we dont consume these events then the TextField moves the cursor around
      switch (key.getCode()) {
        case DOWN:
          selectNext();
          key.consume();
          break;
        case J:
          if (key.isControlDown()) {
            selectNext();
            key.consume();
          }
          break;
        case K:
          if (key.isControlDown()) {
            selectPrev();
            key.consume();
          }
          break;
        case UP:
          selectPrev();
          key.consume();
          break;
        default:
          return;
      }
    });
  }

  /**
   * A factory that can control the elements displayed by a {@code ListView<City>}
   * by providing a new {@code updateItem} implementation for
   * {@code ListCell<City}
   */
  public class CityCellFactory implements Callback<ListView<City>, ListCell<City>> {

    @Override
    public ListCell<City> call(ListView<City> param) {

      return new ListCell<>() {

        /**
         * custom {@code updateItem} function for {@code ListCell<City>}
         */
        @Override
        public void updateItem(City city, boolean empty) {
          super.updateItem(city, empty);

          if (empty) {
            setGraphic(null);

          } else if (city != null) {

            // determine whether this city is selected
            boolean isSelected = getListView().getSelectionModel().getSelectedItem() == city;
            String label = city.display;
            if (city.county.length() > 0) {
              label = label + " (" + city.county + ")";
            }
            Text cityLabel = new Text(label);
            cityLabel.setWrappingWidth(200);

            if (isSelected) {

              // construct our HBox and label
              HBox item = new HBox(cityLabel);

              // Add the appropriate style classes
              cityLabel.getStyleClass().add("search-result-text-selected");
              item.getStyleClass().add("search-result-box-selected");

              // set the graphic for this cell
              setGraphic(item);

            } else {

              // construct our HBox and label
              HBox item = new HBox(cityLabel);

              // Add the appropriate style classes
              cityLabel.getStyleClass().add("search-result-text");
              item.getStyleClass().add("search-result-box");

              item.setOnMouseEntered(e -> {
                cityLabel.getStyleClass().remove("search-result-text");
                item.getStyleClass().remove("search-result-box");
                cityLabel.getStyleClass().add("search-result-text-selected");
                item.getStyleClass().add("search-result-box-selected");
              });
              item.setOnMouseExited(e -> {
                cityLabel.getStyleClass().add("search-result-text");
                item.getStyleClass().add("search-result-box");
                cityLabel.getStyleClass().remove("search-result-text-selected");
                item.getStyleClass().remove("search-result-box-selected");
              });

              setOnMouseClicked(e -> {
                City c = getListView().getItems().get(this.getIndex());
                if (c == null) {
                  searchInput.clear();
                  return;
                }
                System.out.printf("%s is located at %f, %f\n Updating location\n", c.cityName,
                    c.lat,
                    c.lon);
                content.fireEvent(new LocationChangeEvent(c.lat, c.lon, c.display));
                searchInput.clear();
              });

              // set the graphic for this cell
              setGraphic(item);
            }
          } else {

            /**
             * This case should never occur
             * a {@code City} should never be null
             */
            setGraphic(new Text("null"));
          }
        }
      };

    }
  }

  private void selectNext() {
    int index = cityListView.getSelectionModel().getSelectedIndex();
    int next_index = (index + 1) % cityListView.getItems().size();

    // Okay this is gonna get ugly:
    // https://forums.oracle.com/ords/apexds/post/listview-visible-items-8075
    // https://stackoverflow.com/questions/30457708/visible-items-of-listview
    ListViewSkin<?> ts = (ListViewSkin<?>) cityListView.getSkin();
    VirtualFlow<?> vf = (VirtualFlow<?>) ts.getChildren().get(0);
    int firstVisible = vf.getFirstVisibleCell().getIndex();
    int lastVisible = vf.getLastVisibleCell().getIndex();

    // handle scroll down
    if (next_index >= lastVisible) {
      cityListView.scrollTo(firstVisible + 1);
    }
    // handle loop around
    if (next_index <= firstVisible) {
      cityListView.scrollTo(next_index);
    }

    cityListView.getSelectionModel().select(next_index);
  }

  private void selectPrev() {
    int index = cityListView.getSelectionModel().getSelectedIndex();
    int prev_index = index - 1;

    // loop around
    if (prev_index < 0) {
      prev_index = cityListView.getItems().size() - 1;
    }

    // see selectNext for links to this
    ListViewSkin<?> ts = (ListViewSkin<?>) cityListView.getSkin();
    VirtualFlow<?> vf = (VirtualFlow<?>) ts.getChildren().get(0);
    int firstVisible = vf.getFirstVisibleCell().getIndex();
    int lastVisible = vf.getLastVisibleCell().getIndex();

    // handle normal scroll up
    if (prev_index <= firstVisible) {
      cityListView.scrollTo(prev_index);
    }

    // handle loop around
    if (prev_index > lastVisible) {
      cityListView.scrollTo(prev_index - (lastVisible - firstVisible));
    }
    cityListView.getSelectionModel().select(prev_index);
  }
}
