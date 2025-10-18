package livegraph;

public class Robot<T, N> {
    final public T robotID;
    final public N destinationNodeId;

    public Robot(T robotID, N destinationNodeId) {
        this.robotID = robotID;
        this.destinationNodeId = destinationNodeId;
    }
}
