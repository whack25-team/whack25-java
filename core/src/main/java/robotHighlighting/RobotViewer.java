package robotHighlighting;

//import java.awt.Point;
import java.util.List;

import livegraph.ConnectedNode;
import livegraph.Graph;
import livegraph.GraphNode;
import livegraph.RobotMovement;


public class RobotViewer {
    
    public GraphNode<Integer, Integer> currentNode;
    public RobotMovement<Integer, Integer> robot;

    public RobotViewer() {}

    // gets current node and robot from the coordinates of the player click
    public void setNewRobot(double x, double y, Graph<Integer> graph) {
        System.out.println("set new robot");
        this.currentNode = graph.getNodeByCoordinates((int) x, (int) y);

        if (currentNode != null && currentNode.getOccupiers().size() > 0) {
            RobotMovement<Integer, Integer> closest = currentNode.getOccupiers().get(0);
            // find closest robot to click
            for (int i = 1; i < currentNode.getOccupiers().size(); i++) {
                RobotMovement<Integer, Integer> currOccupier = currentNode.getOccupiers().get(i);
                Point coords = calcRobotCoordinates(currentNode, currOccupier);
                if (isLeftCloser(new Point(x,y), coords, calcRobotCoordinates(currentNode,closest))) {
                    closest = currOccupier;
                }
            }
            robot = closest;
        }
    }

        /**
         * updates robots location if it can be found
         * @return true if found and updated, false if not found
         */
    public boolean updateRobotLocation() {
        System.out.println("update new robot: " + currentNode);
        // check if robot still occupier
        if (currentNode.getOccupiers().contains(robot)) {
            return true;
        } else { // check adjacent nodes for robot
            List<ConnectedNode<Integer, Integer>> neighboursOut = currentNode.getNeighbours();
            System.out.println(neighboursOut);
            for (int j = 0; j < neighboursOut.size(); j++) {
                List<ConnectedNode<Integer, Integer>> neighbours = neighboursOut.get(j).node.getNeighbours();
                System.out.println(neighbours);
                for (int i = 0; i < neighbours.size(); i++) {
                    if (neighbours.get(i).node.getOccupiers().contains(robot)) {
                        currentNode = neighbours.get(i).node;
                        System.out.println("found");
                        return true;
                    } 
                }
            }
    
        }
        System.out.println("delete new robot");
        return false;
    }

    private Point calcRobotCoordinates(GraphNode<Integer, Integer> currentNode, RobotMovement<Integer, Integer> robotMovement) {
        // Calculate the robot's coordinates based on its movement progress
        double x = currentNode.getX();
        double y = currentNode.getY();

        // Adjust the coordinates based on the robot's movement
        double progress = robotMovement.getRemainingProgression() / (double) robotMovement.getTotalEdgeWeight();
            // If the robot is moving, calculate its new position
            x = x + progress * (robotMovement.getOriginX() - x);
            y = y + progress * (robotMovement.getOriginY() - y);

        return new Point(x, y);
    }

    private boolean isLeftCloser(Point base, Point left, Point right) {
        return ( Math.pow(left.x - base.x, 2) + Math.pow(left.y - base.y, 2) ) < ( Math.pow(right.x - base.x, 2) + Math.pow(right.y - base.y, 2) );
    }
}