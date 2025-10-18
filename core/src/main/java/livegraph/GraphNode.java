package livegraph;

import java.util.List;

public class GraphNode<R,N> {
    private final N nodeId;
    private int x;
    private int y;
    private NodeType tileType;
    private List<ConnectedNode<R, N>> neighbours;
    private List<RobotMovement<R, N>> occupiers;

    public GraphNode(N nodeId, int x, int y, NodeType tileType, List<ConnectedNode<R,N>> neighbours, List<RobotMovement<R,N>> occupiers) {
        this.nodeId = nodeId;
        this.x = x;
        this.y = y;
        this.tileType = tileType;
        this.neighbours = neighbours;
        this.occupiers = occupiers;
    }

    /**
     * Progress all robot movements occupying this node by one tick.
     */
    public void tick() {
        for (RobotMovement<R, N> movement : occupiers) {
            if (movement.readyToMoveNodes()) {
                // Find the shortest path from here to the robot's destination
                // TODO
            }
            movement.tick();
        }
    }


}
