package slogo.View;

import java.util.List;
import java.util.Map;

import java.util.ResourceBundle;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.ListChangeListener;
import javafx.collections.MapChangeListener;
import javafx.collections.ObservableList;
import javafx.collections.ObservableMap;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.stage.Stage;

import slogo.Main;
import slogo.model.*;
import slogo.model.Commands.Command;

public class Visualizer {
  private static int WINDOW_WIDTH = 1500;
  private static int WINDOW_HEIGHT = 1000;
  private BorderPane bp;
  private ObservableList<Turtle> viewTurtles;
  private ObservableMap<String,String> variables;
  private javafx.scene.image.Image img;
  private static final String style = "Style";
  private ObservableMap myMap;
  private DisplayOption displayOption;
  private Toolbar tool;
  private TurtleGrid grid;
  private Configuration config;
  private HistoryPanel myHistoryPanel;
  public static final int SLOGO_IMAGE_HEIGHT = 80;
  public static final double SLOGO_IMAGE_WIDTH = 200.0;
  private ResourceBundle myResources = Main.myResources;
  private ImageView slogoImage;
  private ObservableList<Triplet<String, String, String>> customCommandList;

  /**
   * Constructor for the visualizer class
   *
   */
  public Visualizer(Stage window, ObservableList viewTurtles, ObservableList activatedTurtles, ObservableMap<String,String> variables,
      StringProperty commandLineText,
      BooleanProperty textUpdate, Language language, CommandParser parser,
      ObservableMap myMap, DisplayOption d, ObservableList<Triplet<String, String, String>> customCommandList) {
    this.viewTurtles = viewTurtles;
    this.customCommandList = customCommandList;
    this.variables = variables;
    config= new Configuration(viewTurtles, d);
    myHistoryPanel = new HistoryPanel(window, parser, config,variables, customCommandList);
    grid = new TurtleGrid(viewTurtles, config,activatedTurtles);
    tool = new Toolbar(grid, language, activatedTurtles);
    CommandLine cmdline = new CommandLine(commandLineText, textUpdate, activatedTurtles,parser);
    this.myMap = myMap;
    img = new Image(myResources.getString("SlogoLogo"));
    slogoImage = new ImageView(img);
    slogoImage.setFitHeight(SLOGO_IMAGE_HEIGHT);
    slogoImage.setFitWidth(SLOGO_IMAGE_WIDTH);
    slogoImage.isFocused();
    setUpBorderPane(grid, cmdline, tool);
    Scene scene = new Scene(bp, WINDOW_WIDTH, WINDOW_HEIGHT);
    window.setScene(scene);
    window.show();
    addSizeListener();
  }

  public void setDisplayOption(DisplayOption d) {
    tool.bindWithDisplayOption(d);
  }

  private void addSizeListener() {
    viewTurtles.addListener(new ListChangeListener<Turtle>() {
      @Override
      public void onChanged(Change<? extends Turtle> c) {
        c.next();
        List<Turtle> newTurtles = (List<Turtle>) c.getAddedSubList();
      }
    });
  }

  private void setUpBorderPane(TurtleGrid grid, CommandLine commandLine, Toolbar tool) {
    bp = new BorderPane();
    bp.setBackground(Background.EMPTY);
    bp.setStyle(myResources.getString(style));
    bp.setBottom(commandLine.getCommandLineGroup());
    bp.setLeft(grid.getTurtleGrid());
    bp.setTop(tool.getToolBar());
    VBox rightSide = new VBox(10);
    rightSide.setAlignment(Pos.CENTER);
    rightSide.getChildren().addAll(slogoImage, myHistoryPanel.returnScene());
    bp.setRight(rightSide);
  }
  public void makeNewBox(String value) {
    myHistoryPanel.makeNewBox(value);
  }
  public void makeNewTerminalBox(String parseText) {
    myHistoryPanel.makeNewTerminalBox(parseText);
  }
}
