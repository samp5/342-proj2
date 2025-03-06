package views.components.sidebar;

import java.util.ArrayList;
import java.util.Collection;
import java.util.stream.Collectors;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.Text;

public class Sidebar {

  // sections
  ArrayList<Section> sections;

  Header header;

  public class Header {
    static final int titleFontSize = 40;
    VBox container;

    // text fields
    Text title;

    VBox coordInputBox;
    // text input
    HBox xGridBox, yGridBox;
    Text xGrid, yGrid;
    TextField x, y;

    public Header() {

      buildTextInput();

      title = new Text(getTitle());
      styleTitle();

      container = new VBox(title, xGridBox, yGridBox);
      container.setPadding(new Insets(10, 0, 10, 10));
    }

    public VBox component() {
      return this.container;
    }

    public void styleTitle() {
      title.setFont(new Font("Atkinson Hyperlegible Bold", titleFontSize));
      title.getStyleClass().add("side-bar-header");
    }

    public void styleInputBoxes() {
      title.setFont(new Font("Atkinson Hyperlegible Bold", titleFontSize));
      title.getStyleClass().add("side-bar-header");
    }


    public void buildTextInput() {
      x = new TextField("77");
      y = new TextField("70");

      x.setFont(new Font("Atkinson Hyperlegible Bold", 18));
      y.setFont(new Font("Atkinson Hyperlegible Bold", 18));

      xGrid = new Text("x:");
      yGrid = new Text("y:");

      xGrid.setFont(new Font("Atkinson Hyperlegible Bold", 18));
      yGrid.setFont(new Font("Atkinson Hyperlegible Bold", 18));

      x.getStyleClass().addAll("side-bar-text", "side-bar-coord-input");
      y.getStyleClass().addAll("side-bar-text", "side-bar-coord-input");
      xGrid.getStyleClass().addAll("side-bar-text", "side-bar-coord-input");
      yGrid.getStyleClass().addAll("side-bar-text", "side-bar-coord-input");


      xGridBox = new HBox(xGrid, x);
      yGridBox = new HBox(yGrid, y);

      xGridBox.setPadding(new Insets(0, 0, 0, 10));
      yGridBox.setPadding(new Insets(0, 0, 0, 10));

      xGridBox.setAlignment(Pos.CENTER_LEFT);
      yGridBox.setAlignment(Pos.CENTER_LEFT);

      coordInputBox = new VBox(xGridBox, yGridBox);
    }
  }

  // main vbox
  VBox container;
  String navTitle = "Chicago, IL";

  public <T extends Collection<Section>> Sidebar(T sections) {
    this.sections = new ArrayList<>(sections);
    this.header = new Header();
    container = new VBox(header.component());
    container.getChildren()
        .addAll(sections.stream().map(section -> section.component()).collect(Collectors.toList()));
  }

  public void addSection(Section section) {
    this.sections.add(section);
  }

  public void setTitle(String title) {
    this.navTitle = title;
  }

  public String getTitle() {
    return this.navTitle;
  }

  public VBox component() {
    return this.container;
  }

}

