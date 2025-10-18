package livegraph;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.PriorityQueue;

public class GraphNode<R,N> {
    private final N nodeId;
    private int x;
    private int y;
    private NodeType tileType;
    private List<ConnectedNode<R, N>> neighbours; // adjacent OUTGOING nodes. this class does not store incoming edges.
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

    // Gets the next nodeid on the shortest path to the destination
    public GraphNode<R,N> getNextNodeOnPath(N destinationNodeId) { // gets the next node on the shortest path to the destination
        PriorityQueue<PathNode<R, N>> pathsToExplore = new PriorityQueue<>((path1, path2) -> Integer.compare(path1.cumulativeWeight, path2.cumulativeWeight) );

        pathsToExplore.add(new PathNode<>(new ArrayList<>(List.of(this)), 0));

        while (!pathsToExplore.isEmpty()) {
            PathNode<R,N> currentPath = pathsToExplore.poll();

            GraphNode<R,N> currentNode = currentPath.path.get(currentPath.path.size() - 1);

            // If we reached the destination, return the next node in the path
            if (currentNode.nodeId.equals(destinationNodeId)) {
                // System.out.println("Length of path found: " + currentPath.path.size() + " with total weight: " + currentPath.cumulativeWeight);
//                for (GraphNode<R,N> node : currentPath.path) {
//                    System.out.print(node.nodeId + " -> ");
//                }
//                System.out.println("(END)");
                return currentPath.path.get(1); // Return the next node after the start node
            }

            // Explore neighbours
            for (ConnectedNode<R,N> neighbour : currentNode.getNeighbours()) {
                if (currentPath.path.stream().noneMatch(node -> node.nodeId.equals(neighbour.node.nodeId))) { // Avoid cycles
                    PathNode<R,N> newPath = currentPath.addStep(neighbour.node, neighbour.edgeWeight);
                    pathsToExplore.add(newPath);
                }
            }
        }

        return null; // No path found

    }

    // Getters
    public List<ConnectedNode<R,N>> getNeighbours() {
        return this.neighbours;
    }

    // Setters
    public void addNeighbour(ConnectedNode<R,N> neighbour) {
        this.neighbours.add(neighbour);
    }

    public void setNeighbours(List<ConnectedNode<R,N>> neighbours) {
        this.neighbours = neighbours;
    }

    // Overrides
    @Override
    public String toString() {
        return "GraphNode{" +
                "nodeId=" + nodeId +
                ", x=" + x +
                ", y=" + y +
                ", tileType=" + tileType +
                '}';
    }


}

class PathNode<R, N> {
    public final List<GraphNode<R,N>> path;
    public final int cumulativeWeight;

    public PathNode(List<GraphNode<R,N>> path, int cumulativeWeight) {
        this.path = path;
        this.cumulativeWeight = cumulativeWeight;
    }

    public PathNode<R,N> addStep(GraphNode<R,N> node, int weight) {
        List<GraphNode<R,N>> newPath = new ArrayList<>(List.copyOf(path));
        newPath.add(node);
        return new PathNode<>(newPath, cumulativeWeight + weight);
    }
}
