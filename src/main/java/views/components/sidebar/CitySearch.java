package views.components.sidebar;

import javafx.animation.PauseTransition;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.geometry.Insets;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
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

            if (isSelected) {

              // construct our HBox and label
              Text cityLabel = new Text(city.display);
              HBox item = new HBox(cityLabel);

              // Add the appropriate style classes
              cityLabel.getStyleClass().add("search-result-text-selected");
              item.getStyleClass().add("search-result-box-selected");

              // set the graphic for this cell
              setGraphic(item);

            } else {

              // construct our HBox and label
              Text cityLabel = new Text(city.display);
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
                  inputField.clear();
                  return;
                }
                System.out.printf("%s is located at %f, %f\n Updating location\n", c.cityName, c.lat,
                    c.lon);
                content.fireEvent(new LocationChangeEvent(c.lat, c.lon));
                inputField.clear();
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

  // Frame for the entire section (search bar + results)
  BorderPane content;

  // results
  ListView<City> items;

  // Underlying {@code City} collection
  ObservableList<City> cityList;

  // Underlying SQL access class
  CityData cityData;

  // Search box elements
  BorderPane searchIcon;
  TextField inputField;
  HBox inputBox;

  /*
   * Create a new
   */
  public CitySearch() {
    cityData = new CityData();
    initComponents();
  }

  public void initComponents() {
    // Frame for the entire section (search bar + results)
    content = new BorderPane();

    // results
    items = new ListView<>();

    // Search box elements
    searchIcon = new BorderPane();
    inputField = new TextField();
    inputBox = new HBox();
  }

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
          .fireAfter(Duration.seconds(2));
      ;
    }

    FilteredList<City> filtered = new FilteredList<>(this.cityList);

    // Listen to any changes on the input field and adjust the filter
    // on our listview. This is the core of our "search".
    //
    // As an additional side effect, always select the top result
    inputField.textProperty().addListener(obs -> {
      String filter = inputField.getText();
      if (filter == null || filter.length() == 0) {
        items.setVisible(false);
        filtered.setPredicate(s -> false);
      } else {
        items.setVisible(true);
        filtered.setPredicate(city -> city.display.contains(filter));
      }
      if (!filtered.isEmpty()) {
        items.getSelectionModel().select(0);
      }
    });

    Image img = new Image("/ui/search.png");
    ImageView imgView = new ImageView(img);

    imgView.setFitWidth(30);
    imgView.setFitHeight(30);

    inputField.getStyleClass().add("search-input");

    searchIcon.setCenter(imgView);

    inputBox.getChildren().setAll(searchIcon, inputField);
    inputBox.getStyleClass().add("search-box");

    items.setItems(filtered);
    items.setCellFactory(new CityCellFactory());
    items.setEditable(false);

    items.setVisible(false);

    items.getStyleClass().add("search-results");

    content.setCenter(items);
    content.setPadding(new Insets(10));
    content.setTop(inputBox);

    content.setOnKeyPressed(key -> {
      if (key.getCode() == KeyCode.ENTER) {
        City c = items.getSelectionModel().getSelectedItem();

        if (c == null) {
          inputField.clear();
          return;
        }

        System.out.printf("%s is located at %f, %f\n Updating location\n", c.cityName, c.lat,
            c.lon);
        content.fireEvent(new LocationChangeEvent(c.lat, c.lon));
        inputField.clear();
      }
    });
    return content;
  }
}
