package livegraph;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.PriorityQueue;

public class GraphNode<R,N> {
    private final N nodeId;
    private int x;
    private int y;
    private NodeType tileType;
    private List<ConnectedNode<R, N>> neighbours; // adjacent OUTGOING nodes. this class does not store incoming edges.
    private List<RobotMovement<R, N>> occupiers;
    private int maxOccupiers = 1;
    private int disabledForGoes = 0; // number of ticks this node is disabled for, no robots can enter, but robots can leave
    private double CELL_BLOCK_PROBABILITY_QUEUE = 0.20; // Probability of blocking a cell during a queue
    private int waitToMove = 0; // Goes to wait before moving due to congestion / no available path

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
        if (this.disabledForGoes > 0) {
            this.disabledForGoes--;
            System.out.println("Node "+this.nodeId+" is disabled for "+this.disabledForGoes+" more ticks, no robots can enter.");
        }

        List<RobotMovement<R,N>> newOccupiers = new ArrayList<>(); // the occupiers at this node after this tick, during which some robots may leave
        for (RobotMovement<R, N> movement : occupiers) {
            if (movement.readyToMoveNodes() && movement.getRobot().destinationNodeId.equals(this.nodeId)) {
                // Robot has reached its destination, so it leaves the graph
                System.out.println("Robot "+movement.getRobot().robotID+" has reached its destination at node "+this.nodeId);
                continue; // Do not add to newOccupiers
            }
            else if (movement.readyToMoveNodes()) {
                // Robot has fully moved into this node, so we can decide its next move
                GraphNode<R,N> nextNode = this.getNextNodeOnPath(movement.getRobot().destinationNodeId);
                if (nextNode != null) {
                    // Move robot to next node
                    for (ConnectedNode<R,N> neighbour : this.getNeighbours()) {
                        if (neighbour.node.nodeId.equals(nextNode.nodeId)) {
                            // Found the edge to the next node
                            if (nextNode.occupiers.size() >= nextNode.getMaxOccupiers() || nextNode.isBlocked()) {
                                // Next node is full, robot stays at this node
                                System.out.println("Robot " + movement.getRobot().robotID + " at node " + this.nodeId + " cannot move to node " + nextNode.nodeId + " as it is full, staying put.");
                                newOccupiers.add(movement);
                                if (Math.random() < CELL_BLOCK_PROBABILITY_QUEUE) { // DISABLE <--- disable this if you want to demo traffic jams
                                    this.disabledForGoes = (int) (Math.random() * 10); // Block this node for 1-10 ticks due to congestion
                                    System.out.println("Node " + this.nodeId + " is now blocked for " + this.disabledForGoes + " ticks due to congestion.");
                                }
                                break;
                            } else { // Move to next node
                                RobotMovement<R,N> newMovement = new RobotMovement<>(movement.getRobot(), neighbour.edgeWeight, this.x, this.y);
                                nextNode.occupiers.add(newMovement);
                                break;
                            }
                        }
                    }
                } else {
                    // No path found, robot stays at this node
                    System.out.println("Robot "+movement.getRobot().robotID+" at node "+this.nodeId+" has no path to destination "+movement.getRobot().destinationNodeId+", staying put.");
                    if (tileType != NodeType.HOUSE) { // Delete if spawn-trapped (may be an error in graph generation)
                        newOccupiers.add(movement);
                        waitToMove = (int) (Math.random()*20);
                    }
                }
            } else {
                // Robot is still moving into this node, stays here
                newOccupiers.add(movement);
            }

            movement.tick();

        }

        this.occupiers = newOccupiers;
    }

    // Gets the next nodeid on the shortest path to the destination
    public GraphNode<R,N> getNextNodeOnPath(N destinationNodeId) { // gets the next node on the shortest path to the destination
        PriorityQueue<PathNode<R, N>> pathsToExplore = new PriorityQueue<>((path1, path2) -> Integer.compare(path1.cumulativeWeight, path2.cumulativeWeight) );

        pathsToExplore.add(new PathNode<>(new ArrayList<>(List.of(this)), 0));

        HashSet<N> visitedNodeIds = new HashSet<>();

        while (!pathsToExplore.isEmpty()) {
            PathNode<R,N> currentPath = pathsToExplore.poll();

            GraphNode<R,N> currentNode = currentPath.path.get(currentPath.path.size() - 1);
            visitedNodeIds.add(currentNode.nodeId);

            // If we reached the destination, return the next node in the path
            if (currentNode.nodeId.equals(destinationNodeId)) {
                // System.out.println("Length of path found: " + currentPath.path.size() + " with total weight: " + currentPath.cumulativeWeight);
//                for (GraphNode<R,N> node : currentPath.path) {
//                    System.out.print(node.nodeId + " -> ");
//                }
//                System.out.println("(END)");
                if (currentPath.path.size() < 2) return null; // Already at destination

                return currentPath.path.get(1); // Return the next node after the start node
            }

            // Explore neighbours
            for (ConnectedNode<R,N> neighbour : currentNode.getNeighbours()) {

                if (!visitedNodeIds.contains(neighbour.getNode().getNodeId()) && !neighbour.getNode().isBlocked()) { // Avoid cycles && currentPath.path.stream().noneMatch(node -> node.nodeId.equals(neighbour.node.nodeId))
                    // System.out.println("Exploring neighbour "+neighbour.node.nodeId+" from current node "+currentNode.nodeId);
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

    public List<RobotMovement<R,N>> getOccupiers() {
        return this.occupiers;
    }

    public N getNodeId() {
        return nodeId;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public NodeType getTileType() {
        return tileType;
    }

    public int getMaxOccupiers() {
        return maxOccupiers;
    }

    public boolean isBlocked() {
        return this.disabledForGoes > 0;
    }

    // Setters
    public void addNeighbour(ConnectedNode<R,N> neighbour) {
        this.neighbours.add(neighbour);
    }

    public void setNeighbours(List<ConnectedNode<R,N>> neighbours) {
        this.neighbours = neighbours;
    }

    public void addOccupier(RobotMovement<R,N> occupier) {
        this.occupiers.add(occupier);
    }

    // Overrides
    @Override
    public String toString() {
        return "GraphNode{" +
                "nodeId=" + nodeId +
                ", x=" + x +
                ", y=" + y +
                ", tileType=" + tileType +
                ", neighbours=" + neighbours +
                ", occupiers=" + occupiers +
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
