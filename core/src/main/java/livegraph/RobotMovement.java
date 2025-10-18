package livegraph;

/**
 * A class representing a robot moving into an area.
 * Movement cannot be cancelled while it is moving into the area
 * When remainingProgression is 0, the robot has fully moved into the area
 * Only when remainingProgression is 0 can the robot start moving into another area and make decisions.
 */
public class RobotMovement<R, N> {
    private int remainingProgression;
    private final int totalEdgeWeight;
    private Robot<R, N> robot;

    public RobotMovement(Robot<R, N> robot, int edgeWeight) {
        this.robot = robot;
        this.totalEdgeWeight = edgeWeight;
        this.remainingProgression = edgeWeight;
    }

    public int getRemainingProgression() {
        return remainingProgression;
    }

    /**
     * Progress the robot's movement by one tick.
     */
    public void tick() {
        if (remainingProgression > 0) {
            remainingProgression--;
        }
    }

    /**
     * The robot has arrived at the current node, so is ready to make decisions upon the next node to move to
     * @return true if the robot has fully moved into the area and can make decisions, false otherwise
     */
    public boolean readyToMoveNodes() {
        return remainingProgression == 0;
    }
}
