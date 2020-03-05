package slogo.View;

import java.awt.*;
import java.net.URI;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.ResourceBundle;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import javafx.util.Callback;
import slogo.Main;
import slogo.model.DisplayOption;
import slogo.model.TurtleList;

public class Toolbar {

  private ColorPicker backgroundColor;
  private ColorPicker penColor;
  private Button helpButton;
  private ResourceBundle myResources = Main.myResources;
  private ComboBox setTurtleImage;
  private ComboBox<String> changeLanguageBox;
  private ComboBox<String> changePenColor;
  private ComboBox<String> changeBackgroundColor;
  private static final List<String> LANGUAGES = Arrays
      .asList("English", "Chinese", "French", "German", "Italian", "Portuguese", "Russian",
          "Spanish", "Urdu");
  private static final List<String> TURTLES = Arrays
      .asList("TurtleImage", "BlackTurtle", "BlueTurtle", "CuteTurtle", "Duvall");
  private StringProperty currentLanguage = new SimpleStringProperty();
  private HBox toolBar;
  private static final Paint BUTTON_FONT_COLOR = Color.BLACK;
  private static final int BUTTON_FONT_SIZE = 13;
  private static final String HELP_URI = "https://www2.cs.duke.edu/courses/compsci308/spring20/assign/03_parser/commands.php";
  private static final String CHANGE_BACKGROUND = "ChangeBackgroundColor";
  private static final String CHANGE_PEN = "ChangePenColor";
  private static final String BUTTON_HELP = "Help";
  private static final String CHANGE_TURTLE = "ChangeTurtleImage";
  private static final String STYLE_COLOR = "lightgray";
  private static final int PADDING = 10;
  private static final int BUTTON_WIDTH = 200;
  private static final int BUTTON_HEIGHT = 25;
  private static final int COLOR_PICKER_WIDTH = 50;
  private HBox backgroundColorChooser, penColorChoosers;
  private Button backgroundColorPicker, penColorPicker;
  private TurtleGrid turtleGrid;
  private Desktop forHelp;
  private Language language;
  private Button makeNew;

  private ObservableList<String> colorOptions;
  private static final String DEFAULT_RESOURCE_PACKAGE = "resources.";
  private static ResourceBundle myColors2 = ResourceBundle
      .getBundle(DEFAULT_RESOURCE_PACKAGE + "Colors2");
  private static ResourceBundle buttonNames = ResourceBundle.getBundle(DEFAULT_RESOURCE_PACKAGE + "buttonNames");
  private static ResourceBundle buttonMethods = ResourceBundle.getBundle(DEFAULT_RESOURCE_PACKAGE + "buttonMethodNames");

  private static ResourceBundle possibleLanguages = ResourceBundle.getBundle(DEFAULT_RESOURCE_PACKAGE + "Languages");
  private IntegerProperty penColorIndex = new SimpleIntegerProperty();
  private IntegerProperty bgColorIndex = new SimpleIntegerProperty();
  private DoubleProperty penWidth = new SimpleDoubleProperty();
  private IntegerProperty imageIndex = new SimpleIntegerProperty();
  private IntegerProperty changedIndex = new SimpleIntegerProperty(-1);
  private StringProperty newColor = new SimpleStringProperty();

  public Toolbar(TurtleGrid grid, Language language) {
    initializeColors();
    makeComboBoxes();
    makeNew = new ViewButton("New Workspace", BUTTON_HEIGHT, BUTTON_WIDTH, BUTTON_FONT_SIZE);
    uploadSim();
    setTurtleImage = new ComboBox<>();
    forHelp = Desktop.getDesktop();
    turtleGrid = grid;
    setUpColorChoosers();
    helpButton = new ViewButton(Main.myResources.getString(BUTTON_HELP), BUTTON_HEIGHT,
        BUTTON_WIDTH, BUTTON_FONT_SIZE);
//    setTurtleImage = new ViewButton(Main.myResources.getString(CHANGE_TURTLE), SLOGO_IMAGE_HEIGHT, SLOGO_IMAGE_WIDTH, BUTTON_FONT_SIZE);
    setUpChangeLanguageChooser();
    setUpTurtleChooser();
    setUpHelpButton();
    setUpPenColorDropdown(grid);
    setUpBackgroundColorDropdown(grid);
    addBgIndexListener();
    addPenIndexListener();
    addPenWidthListener();
    addImageListener();
    addChangedIndexListener();
    this.language = language;
    currentLanguage.set("English");
  }

  private void setupLanguages() {
    //TODO: do this method later
  }

  public void bindWithDisplayOption(DisplayOption displayOption) {
    penWidth.bindBidirectional(displayOption.getPenWidthProperty());
    penColorIndex.bindBidirectional(displayOption.getPenIndex());
    bgColorIndex.bindBidirectional(displayOption.getBgIndex());
    imageIndex.bindBidirectional(displayOption.getImageIndex());
    newColor.bindBidirectional(displayOption.getNewColor());
    changedIndex.bindBidirectional(displayOption.getChangedIndex());
    List<String> colorCopy = new ArrayList<>(colorOptions);
    displayOption.createList(colorCopy);
  }

  private void setUpColorChoosers() {
    backgroundColorChooser = new HBox();
    setUpBackgroundColorChooser(turtleGrid);
    backgroundColorChooser.getChildren().addAll(backgroundColorPicker, backgroundColor);
    penColorChoosers = new HBox();
    setUpPenColorChooser(turtleGrid);
    penColorChoosers.getChildren().addAll(penColorPicker, penColor);
  }

  private void makeComboBoxes() {
    changeLanguageBox = new ComboBox<>();
    changePenColor = new ComboBox<>();
    changeBackgroundColor = new ComboBox<>();
  }

  private void makeButton() {

  }

  private void initializeColors() {
    colorOptions = FXCollections.observableArrayList();
    int index = 0;
    for (String color : myColors2.keySet()) {
      String rgb = myColors2.getString(color);
      System.out.println(rgb);
      colorOptions.add(rgb + ", " + index);
      index++;
    }
  }

  private Color getColorRGB(String[] rgb) {
    int r = Integer.parseInt(rgb[0]);
    int g = Integer.parseInt(rgb[1]);
    int b = Integer.parseInt(rgb[2]);
    return Color.rgb(r, g, b);
  }

  private void addPenIndexListener() {
    penColorIndex.addListener(new ChangeListener() {
      @Override
      public void changed(ObservableValue o, Object oldVal, Object newVal) {
        turtleGrid.setPenColor(getNewColor(penColorIndex));
      }
    });
  }

  private Color getNewColor(IntegerProperty property) {
    int index = property.get();
    String[] color = colorOptions.get(index).split(", ");
    String[] rgb = color[0].split(" ");
    return getColorRGB(rgb);
  }

  private void addBgIndexListener() {
    bgColorIndex.addListener(new ChangeListener() {
      @Override
      public void changed(ObservableValue o, Object oldVal, Object newVal) {
        turtleGrid.setBackground(getNewColor(bgColorIndex));
      }
    });
  }

  private void addChangedIndexListener() {
    changedIndex.addListener(new ChangeListener() {
      @Override
      public void changed(ObservableValue o, Object oldVal, Object newVal) {
        int index = changedIndex.get();
        if (index >= colorOptions.size()) {
          colorOptions.add(newColor.get());
        } else {
          colorOptions.set(index, newColor.get());
        }
      }
    });
  }

  private void addPenWidthListener() {
    penWidth.addListener(new ChangeListener() {
      @Override
      public void changed(ObservableValue o, Object oldVal, Object newVal) {
        turtleGrid.setPenWidth(penWidth.get());
      }
    });
  }

  private void addImageListener() {
    imageIndex.addListener(new ChangeListener() {
      @Override
      public void changed(ObservableValue o, Object oldVal, Object newVal) {
        int index = imageIndex.get();
        String image = (String)setTurtleImage.getItems().get(index);
        String imageName = image.split(", ")[0];
        turtleGrid.updateTurtlesImage(imageName, TurtleList.getActiveTurtleList());
      }
    });
  }

  private void setUpPenColorDropdown(TurtleGrid grid) {
    changePenColor.itemsProperty().bind(new SimpleObjectProperty<>(colorOptions));
    changePenColor.setPrefWidth(BUTTON_WIDTH);
    setupCellBackgrounds(changePenColor);
    changePenColor.getSelectionModel().selectFirst();
    changePenColor.setOnAction(e -> {
     changePenColor();
    });
  }

  private void changePenColor() {
    String[] color = changePenColor.getValue().split(", ");
    Color c = getColorRGB(color[0].split(" "));
    turtleGrid.setPenColor(c);
    changePenColor
        .setBackground(new Background(new BackgroundFill(c, CornerRadii.EMPTY, Insets.EMPTY)));
  }

  private void setupCellBackgrounds(ComboBox<String> box) {
    box.setCellFactory(new Callback<ListView<String>, ListCell<String>>() {
      @Override
      public ListCell<String> call(ListView<String> p) {
        final ListCell<String> cell = new ListCell<String>() {
          @Override
          protected void updateItem(String item, boolean empty) {
            super.updateItem(item, empty);
            setText(item);
            System.out.println(item);
            if (item == null || empty) {
              // do nothing
            } else {
              String[] color = item.split(", ");
              String[] rgb = color[0].split(" ");
              Color c = getColorRGB(rgb);
              Background background = new Background(
                  new BackgroundFill(c, CornerRadii.EMPTY, Insets.EMPTY));
              setBackground(background);
            }
          }
        };
        return cell;
      }
    });
  }


  private void setUpBackgroundColorDropdown(TurtleGrid grid) {
    changeBackgroundColor.itemsProperty().bind(new SimpleObjectProperty<>(colorOptions));
    changeBackgroundColor.setPrefWidth(BUTTON_WIDTH);
    setupCellBackgrounds(changeBackgroundColor);
    changeBackgroundColor.getSelectionModel().selectFirst();
    changeBackgroundColor.setOnAction(e -> {
      changeBackground();
    });
  }

  private void changeBackground() {
    String[] color = changeBackgroundColor.getValue().split(", ");
    Color c = getColorRGB(color[0].split(" "));
    //TODO: check to make sure this is an actual color
    turtleGrid.setBackground(c);
    changeBackgroundColor
        .setBackground(new Background(new BackgroundFill(c, CornerRadii.EMPTY, Insets.EMPTY)));
  }

  private void uploadSim() {
    makeNew.setOnAction(e -> {
      makeNewWorkspace();
    });
  }

  private void makeNewWorkspace(){
    Stage newScreen = new Stage();
    Main newSimulation = new Main();
    try {
      newSimulation.start(newScreen);
    } catch (Exception ex) {
      showMessage(Alert.AlertType.ERROR, ex.getMessage());
    }
  }

  private void setUpTurtleChooser() {
    setTurtleImage.setPrefWidth(BUTTON_WIDTH);

    int index = 0;
    for (String turtle : TURTLES) {
      setTurtleImage.getItems().add(turtle + ", " + index);
      index++;
    }
    setTurtleImage.setCellFactory(new Callback<ListView<String>, ListCell<String>>() {
      @Override
      public ListCell<String> call(ListView<String> p) {
        final ListCell<String> cell = new ListCell<String>() {
          @Override
          protected void updateItem(String item, boolean empty) {
            super.updateItem(item, empty);
            setText(item);
            if (item == null || empty) {
              setGraphic(null);
            } else {
              String turtleName = item.split(", ")[0];
              Image icon = new Image(myResources.getString(turtleName));
              ImageView iconImageView = new ImageView(icon);
              iconImageView.setFitHeight(30);
              iconImageView.setPreserveRatio(true);
              setGraphic(iconImageView);
            }
          }
        };
        return cell;
      }
    });
    setTurtleImage.getSelectionModel().selectFirst();
    // setTurtleImage.setOnAction(e -> language.setLanguage(changeLanguageBox.getValue()));
    //when clicked set turtle image to what ever was clicked
    // setTurtleImage.setOnAction(e -> System.out.println(setTurtleImage.getValue()));

    setTurtleImage.setOnAction(e -> {
      setImage();
    });
  }

  private void setImage() {
    turtleGrid
        .updateTurtlesImage((String) setTurtleImage.getValue(), TurtleList.getActiveTurtleList());
  }

  private void setUpHelpButton() {
    helpButton.setOnAction(e -> {
      help();
    });
  }

  private void help(){
    try {
      URL url = new URL(HELP_URI);
      URLConnection connection = url.openConnection();
      connection.connect();
      try {
        forHelp.browse(new URI(HELP_URI));
      } catch (Exception v) {
        showMessage(Alert.AlertType.ERROR, v.getMessage());
      }
    } catch (Exception ex) {
      showMessage(Alert.AlertType.ERROR, ex.getMessage());
    }
  }

  public HBox getToolBar() {
    toolBar = new HBox(PADDING);
    toolBar.setPrefHeight(40);
    toolBar.setBackground(
        new Background(new BackgroundFill(Color.rgb(88, 77, 20), CornerRadii.EMPTY, Insets.EMPTY)));
    toolBar.getChildren()
        .addAll(makeNew, backgroundColorChooser, penColorChoosers, setTurtleImage, changeLanguageBox,
            changePenColor, changeBackgroundColor,
            helpButton);
    return toolBar;
  }

  private void setUpChangeLanguageChooser() {
    changeLanguageBox.setPrefWidth(BUTTON_WIDTH);
    for (String lang : LANGUAGES) {
      changeLanguageBox.getItems().add(lang);
    }
    changeLanguageBox.getSelectionModel().selectFirst();
    changeLanguageBox.setOnAction(e -> setLanguage());
  }

  private void setLanguage() {
    language.setLanguage(changeLanguageBox.getValue());
  }

  private void setUpPenColorChooser(TurtleGrid grid) {
    penColorPicker = fakeButton(Main.myResources.getString(CHANGE_PEN), STYLE_COLOR,
        BUTTON_FONT_COLOR, BUTTON_FONT_SIZE);
    penColor = makeColorPicker();
    penColor.setOnAction(e -> setPenColor());
  }

  private void setPenColor() {
    turtleGrid.setPenColor(penColor.getValue());
  }

  private void setUpBackgroundColorChooser(TurtleGrid grid) {
    backgroundColorPicker = fakeButton(Main.myResources.getString(CHANGE_BACKGROUND), STYLE_COLOR,
        BUTTON_FONT_COLOR, BUTTON_FONT_SIZE);
    backgroundColor = makeColorPicker();
    backgroundColor.setOnAction(e -> grid.setBackground(backgroundColor.getValue()));
  }

  private ColorPicker makeColorPicker() {
    ColorPicker cp = new ColorPicker();
    cp.setPrefWidth(COLOR_PICKER_WIDTH);
    return cp;
  }

  private Button fakeButton(String text, String styleColor, Paint fontColor, int fontSize) {
    Button button = new Button(text);
    button.setTextFill(fontColor);
    button.setFont(Font.font("Calibri"));
    button.setStyle("-fx-background-color:" + styleColor + ";-fx-font-size:" + fontSize + " px;");
    button.setPrefWidth(200);
    return button;
  }

  // display given message to user using the given type of Alert dialog box
  private void showMessage(Alert.AlertType type, String message) {
    Alert alert = new Alert(type);
    javafx.scene.image.Image img = new javafx.scene.image.Image(myResources.getString("Dinosaur"));
    ImageView imageView = new ImageView(img);
    imageView.setFitWidth(500);
    imageView.setFitHeight(400);
    alert.setGraphic(imageView);
    alert.showAndWait();
  }

}
