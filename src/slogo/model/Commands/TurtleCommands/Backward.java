package slogo.model.Commands.TurtleCommands;

import slogo.model.TreeNode;
import slogo.model.Turtle;

/**
 * @author Sanna
 *
 * Back command: BACK pixels
 * Moves the turtle backward by "pixels" spaces in the direction it currently faces,
 * and returns "pixels"
 *
 */
public class Backward extends TurtleCommand {

  private double distance;

  public Backward(String name) {
    super(name);
  }

  /**
   * Moves the turtle backward by the input distance
   * @param commandNode
   */
  @Override
  public void doCommand(TreeNode commandNode) {
    distance = Double.parseDouble(getParamList().get(0));
    commandNode.setResult(distance + "");
    moveTurtles(backward, distance);
  }
}
