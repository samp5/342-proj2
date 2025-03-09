package views.components.sidebar;

import java.util.stream.IntStream;

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
import javafx.scene.layout.Background;
import javafx.scene.layout.Border;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.text.Text;
import views.components.events.LocationChangeEvent;
import views.util.CityData;
import views.util.CityData.City;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.util.Callback;

public class CitySearch {

  public class CityCellFactory implements Callback<ListView<City>, ListCell<City>> {

    @Override
    public ListCell<City> call(ListView<City> param) {
      return new ListCell<>() {
        @Override
        public void updateItem(City city, boolean empty) {
          if (empty) {
            setText(null);
          } else if (city != null) {
            boolean isSelected = getListView().getSelectionModel().getSelectedItem() == city;
            if (isSelected) {
              Text cityLabel = new Text(city.display);
              HBox item = new HBox(cityLabel);
              cityLabel.getStyleClass().add("search-result-text-selected");
              item.getStyleClass().add("search-result-box-selected");
              setGraphic(item);
            } else {
              Text cityLabel = new Text(city.display);
              HBox item = new HBox(cityLabel);
              cityLabel.getStyleClass().add("search-result-text");
              item.getStyleClass().add("search-result-box");
              setGraphic(item);
            }
          } else {
            setText("null");
          }
        }
      };

    }
  }

  BorderPane content;
  ListView<City> items;
  ObservableList<City> data;
  CityData cityData;

  BorderPane search_icon;
  TextField inputField;
  HBox inputBox;

  public CitySearch() {
    cityData = new CityData();

  }

  public BorderPane component() {
    try {
      this.data = cityData.getCityList();
    } catch (Exception e) {
      e.printStackTrace();
      this.data = FXCollections.observableArrayList();
    }

    FilteredList<City> filtered = new FilteredList<>(this.data);

    inputField = new TextField();
    inputField.textProperty().addListener(obs -> {
      String filter = inputField.getText();
      if (filter == null || filter.length() == 0) {
        items.setVisible(false);
        filtered.setPredicate(s -> false);
      } else {
        items.setVisible(true);
        filtered.setPredicate(city -> city.display.contains(filter));
      }
    });

    filtered.predicateProperty().addListener(e -> {
      if (!filtered.isEmpty()) {
        items.getSelectionModel().select(0);
      }
    });

    Image img = new Image("/ui/search.png");
    ImageView imgView = new ImageView(img);
    imgView.setFitWidth(30);
    imgView.setFitHeight(30);

    inputField.getStyleClass().add("search-input");

    search_icon = new BorderPane();
    search_icon.setCenter(imgView);

    inputBox = new HBox(search_icon, inputField);
    inputBox.getStyleClass().add("search-box");

    items = new ListView<>(filtered);
    items.setCellFactory(new CityCellFactory());
    items.setEditable(false);

    items.setVisible(false);

    items.getStyleClass().add("search-results");

    content = new BorderPane(items);
    content.setPadding(new Insets(10));
    content.setTop(inputBox);

    content.setOnKeyPressed(key -> {
      if (key.getCode() == KeyCode.ENTER) {
        City c = items.getSelectionModel().getSelectedItem();
        System.out.printf("%s is located at %f, %f\n Updating location\n", c.cityName, c.lat,
            c.lon);
        content.fireEvent(new LocationChangeEvent(c.lat, c.lon));
        inputField.clear();
      }
    });
    return content;
  }
}
