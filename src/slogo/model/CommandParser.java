package slogo.model;

import javafx.collections.ObservableList;
import javafx.collections.ObservableMap;
import slogo.View.Language;
import slogo.model.Commands.CommandFactory;
import slogo.model.Commands.CommandFactoryInterface;

import java.util.*;
import java.util.AbstractMap.SimpleEntry;
import java.util.Map.Entry;
import java.util.regex.Pattern;

public class CommandParser {
  private List<Entry<String, Pattern>> mySymbols;
  private ObservableList<Turtle> turtles;
  private ObservableMap<String,String> variables;
  private CommandFactoryInterface commandFactory;
  private CommandTreeExecutor treeExec;
  private CommandTreeConstructor treeMaker;
  private HashMap<Pattern,String> translations = new HashMap<>();
  private static final String RESOURCES = "resources.";
  private static final String ERRORS = RESOURCES + "ErrorMessages";
  private ResourceBundle errors = ResourceBundle.getBundle(ERRORS);
  private Language language;
  private DisplayOption displayOption;
  private static final Pattern COMMAND_PATTERN = Pattern.compile("(\\+)|(\\-)|(\\*)|(\\~)|(\\/)|(\\%)|[a-zA-Z_]+(\\?)?");
  private GeneralParserBehavior parserBehavior;

  /**
   * Create an empty parser
   */
  public CommandParser(ObservableList<Turtle> turtles, ObservableMap<String, String> variables, Language language) {
    parserBehavior = new GeneralParserBehavior();
    this.language = language;
    mySymbols = new ArrayList<>();
    addPatterns(this.language.getCurrentLanguage());
    createReverseHashMap(mySymbols);
    commandFactory = new CommandFactory();
    this.turtles = turtles;
    this.variables = variables;
  }

  public void setDisplayOption(DisplayOption disp) {
    displayOption = disp;
  }

  public void setTurtles(ObservableList<Turtle> turtles)
  {
    this.turtles = turtles;
  }

  /**
   * Adds the given resource file to this language's recognized types
   */
  public void addPatterns(String syntax) {
    ResourceBundle resources = ResourceBundle.getBundle(RESOURCES + syntax);
    for (String key : Collections.list(resources.getKeys())) {
      String regex = resources.getString(key);
      mySymbols.add(new SimpleEntry<>(key,
          Pattern.compile(regex, Pattern.CASE_INSENSITIVE)));
    }
  }

  /**
   * Returns language's type associated with the given text if one exists.
   * Throws an error if there is no match
   */
  public String getSymbol(String text) {
    for (Entry<String, Pattern> e : mySymbols) {
      if (match(text, e.getValue())) {
        return e.getKey();
      }
    }
    throw new CommandException(new Exception(), errors.getString("InvalidCommand"));
  }

  public void createReverseHashMap (List<Entry<String, Pattern>> mySymbols) {
    for (Entry<String, Pattern> e : mySymbols) {
      translations.putIfAbsent(e.getValue(), e.getKey());
    }
    // FIXME: perhaps throw an exception instead
  }

  // Returns true if the given text matches the given regular expression pattern
  private boolean match(String text, Pattern regex) {
    return regex.matcher(text).matches();
  }

  public String parseText(String commandLine) {
    System.out.println("The current language is " + language.getCurrentLanguage());
    mySymbols = new ArrayList<>();
    addPatterns(language.getCurrentLanguage());
    String[] lineValues = commandLine.split("\\s+");
    boolean toCommand = false;

    for (int i = 0; i < lineValues.length; i++) {
      if (match(lineValues[i], COMMAND_PATTERN)) {
        String string = lineValues[i];

        if (toCommand) {
          toCommand = false;
        } else if (!CustomCommandMap.getKeySet().contains(string)) {
          lineValues[i] = getSymbol(lineValues[i]);
        }

        System.out.println("ELEMENT:" + lineValues[i]);
//        if (string.equals("to")) // TODO: have to generalize this to other languages
//          toCommand = true;
        if (getSymbol(string).equals("MakeUserInstruction")) // TODO: have to generalize this to other languages
          toCommand = true;
      }

      if (lineValues[i].equals("\n")) {
        lineValues[i] = "|n";
      }
      System.out.println("GENERAL ELEMENT:" + lineValues[i]);
    }
    String translatedCommands = String.join(" ", lineValues);
    System.out.println("TRANSLATED: " +translatedCommands);
    return makeCommandTree(translatedCommands);
  }

  public String miniParse(String commandLine) {
    mySymbols = new ArrayList<>();
    addPatterns(language.getCurrentLanguage());
    String[] lineValues = commandLine.split("\\s+");
    String translatedCommands = String.join(" ", lineValues);
    return makeCommandTree(translatedCommands);
  }

  private String makeCommandTree(String commands) {
    treeMaker = new CommandTreeConstructor(translations);
    ArrayList<TreeNode> head = (ArrayList) treeMaker.buildTrees(commands);
    treeExec = new CommandTreeExecutor(commandFactory, turtles, variables, translations, language);
    treeExec.setDisplayOption(displayOption);
    return treeExec.executeTrees(head);
  }
}


//        if (toCommand) {
//          toCommand = false;
//        } else {
//         if (CustomCommandMap.getKeySet().contains(string)) {
//
//         } else {
//           lineValues[i] = getSymbol(lineValues[i]);
//         }
//        }
