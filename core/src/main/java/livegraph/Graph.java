package livegraph;

import java.util.ArrayList;
import java.util.HashMap;

public class Graph<N> {
    private HashMap<N, GraphNode<Integer, N>> nodes;
    private final int gridWidth;
    private final int gridHeight;
    private final double PROBABILITY_ROBOT_SPAWN_ON_TILE = 0.015;
    private int robotCounter = 0;
    private Runnable onRobotSpawn;
    private Runnable onRobotFinish;

    /**
     * Adds a graph with the nodes given. Note, gridWidth and gridHeight are only for reference and not enforced in any way.
     * Nodes may have coordinates outside of this range, and duplicates coordinates are not supported but are not checked for.
     * @param nodes the nodes in the graph
     * @param gridWidth
     * @param gridHeight
     */
    public Graph(HashMap<N, GraphNode<Integer, N>> nodes, int gridWidth, int gridHeight, Runnable onRobotSpawn, Runnable onRobotFinish) {
        // note: onRobotFinish needs to be specified in the nodes themselves
        this.nodes = nodes;
        this.gridWidth = gridWidth;
        this.gridHeight = gridHeight;
        this.onRobotSpawn = onRobotSpawn;
        this.onRobotFinish = onRobotFinish;
    }

    public Graph(int gridWidth, int gridHeight) {
        this.nodes = new HashMap<>();
        this.gridWidth = gridWidth;
        this.gridHeight = gridHeight;
        this.onRobotSpawn = () -> {};
        this.onRobotFinish = () -> {};
    }

    public void setOnRobotFinish(Runnable onRobotFinish) {
        this.onRobotFinish = onRobotFinish;
        for (GraphNode<Integer, N> node : nodes.values()) {
            node.setOnRobotFinish(onRobotFinish);
        }
    }

    public void setOnRobotSpawn(Runnable onRobotSpawn) {
        this.onRobotSpawn = onRobotSpawn;
    }

    /**
     * Gets a node from the graph.
     * @param node the ID of the node to get
     * @return the node, or null if not found
     */
    public GraphNode<Integer,N> getNode(N node) {
        return nodes.get(node);
    }

    /**
     * Adds a node to the graph.
     * @param node the node to add
     */
    public void addNode(GraphNode<Integer,N> node) {
        if (node.getX() < 0 || node.getX() >= gridWidth || node.getY() < 0 || node.getY() >= gridHeight) {
            throw new IllegalArgumentException("Node coordinates out of bounds");
        }
        nodes.put(node.getNodeId(), node);
    }

    public boolean toggleNodeEnabled(int x, int y) {
        for (GraphNode<Integer,N> node : nodes.values()) {
            if (node.getX() == x && node.getY() == y) {
                node.setBlocked(!node.isBlocked());
                return true;
            }
        }
        return false;
    }

    /**
     * Adds a unidirectional edge from fromNodeId to toNodeId with the given edge weight.
     * @param fromNodeId the ID of the node the edge is from
     * @param toNodeId the ID of the node the edge is to
     * @param edgeWeight the weight of the edge
     */
    public void addUnidirectionalEdge(N fromNodeId, N toNodeId, int edgeWeight) {
        GraphNode<Integer,N> fromNode = nodes.get(fromNodeId);
        GraphNode<Integer,N> toNode = nodes.get(toNodeId);
        if (fromNode != null && toNode != null) {
            fromNode.addNeighbour(new ConnectedNode<>(toNode, edgeWeight));
        } else {
            throw new IllegalArgumentException("One or both node IDs not found in graph");
        }
    }

    // During gameplay
    /**
     * Progress all robot movements in the graph by one tick.
     */
    public void tick() {
        GraphNode<Integer,N> endNodeR = null; // node for the robot to end at

        for (GraphNode<Integer,N> node : nodes.values()) {
            node.tick();
            // Randomly decide to spawn a robot at this node or at the end
            if (Math.random() < PROBABILITY_ROBOT_SPAWN_ON_TILE && node.getTileType() == NodeType.HOUSE) {
                if (endNodeR == null) {
                    endNodeR = node;
                } else { // end node determined from before, use the current node as the start node
                    Robot<Integer,N> robot = new Robot<>(robotCounter++, endNodeR.getNodeId());
                    node.addOccupier(new RobotMovement<>(robot, 1, node.getX(), node.getY()));
                    System.out.println("Spawned new robot "+robot.robotID+" at node "+node.getNodeId()+" with destination "+endNodeR.getNodeId());
                    endNodeR = null; // reset for next spawn
                    onRobotSpawn.run();
                }
            }
        }
    }

    // Getters

    /**
     * Gets the nodes in the graph.
     * @return the nodes in the graph
     */
    public HashMap<N, GraphNode<Integer, N>> getNodes() {
        return nodes;
    }

    public int getGridWidth() {
        return gridWidth;
    }

    public int getGridHeight() {
        return gridHeight;
    }

    public static void main(String[] args) {
//        GraphNode<String, String> nodeA = new GraphNode<>("A", 0, 0, NodeType.ROAD, new ArrayList<>(), new ArrayList<>());
//        GraphNode<String, String> nodeB = new GraphNode<>("B", 1, 0, NodeType.ROAD, new ArrayList<>(), new ArrayList<>());
//        GraphNode<String, String> nodeC = new GraphNode<>("C", 1, 0, NodeType.ROAD, new ArrayList<>(), new ArrayList<>());
//        GraphNode<String, String> nodeD = new GraphNode<>("D", 1, 0, NodeType.ROAD, new ArrayList<>(), new ArrayList<>());
//        GraphNode<String, String> nodeE = new GraphNode<>("E", 1, 0, NodeType.ROAD, new ArrayList<>(), new ArrayList<>());
//        nodeA.addNeighbour(new ConnectedNode<>(nodeB, 4));
//        nodeB.addNeighbour(new ConnectedNode<>(nodeA, 4));
//        nodeA.addNeighbour(new ConnectedNode<>(nodeC, 4));
//        nodeC.addNeighbour(new ConnectedNode<>(nodeA, 4));
//        nodeB.addNeighbour(new ConnectedNode<>(nodeD, 1));
//        nodeD.addNeighbour(new ConnectedNode<>(nodeB, 1));
//        nodeC.addNeighbour(new ConnectedNode<>(nodeD, 1));
//        nodeD.addNeighbour(new ConnectedNode<>(nodeC, 1));
//        nodeC.addNeighbour(new ConnectedNode<>(nodeE, 1));
//        nodeE.addNeighbour(new ConnectedNode<>(nodeC, 1));
//        nodeB.addNeighbour(new ConnectedNode<>(nodeE, 5));
//        nodeE.addNeighbour(new ConnectedNode<>(nodeB, 5));
//        nodeD.addNeighbour(new ConnectedNode<>(nodeE, 1));
//        nodeE.addNeighbour(new ConnectedNode<>(nodeD, 1));
//        HashMap<String, GraphNode<String, String>> nodes = new HashMap<>();
//        nodes.put("A", nodeA);
//        nodes.put("B", nodeB);
//        nodes.put("C", nodeC);
//        nodes.put("D", nodeD);
//        nodes.put("E", nodeE);
//        Graph<String, String> graph = new Graph<>(nodes, 10, 10);
//
//        GraphNode<String, String> nextNode = nodeE.getNextNodeOnPath("A");
//
//        System.out.println(nextNode);
//
//        // Testing with robot occupiers
//
//        // Add a robot moving into node E from an arbitrary node, with an edge weight of 2
//        nodeE.addOccupier(new RobotMovement<>(new Robot<>("Robo 1", "A"), 2, 4,4));
//
//        System.out.println(nodeE);
//        nodeE.tick();
//        System.out.println(nodeE);
//        nodeE.tick();
//        System.out.println(nodeE);
//        System.out.println("----");
//        nodeE.tick();
//        System.out.println(nodeE);
//        System.out.println(nodeC);
//        nodeC.tick();
//        System.out.println(nodeC);
//        System.out.println("----");
//        nodeC.tick();
//        System.out.println(nodeC);
//        System.out.println(nodeA);
//        nodeA.tick();
//        System.out.println(nodeA);

    }

    public static Graph<Integer> exampleGraph() {
        GraphNode<Integer, Integer> nodeA = new GraphNode<>(1, 0, 0, NodeType.ROAD, new ArrayList<>(), new ArrayList<>(), () -> {});
        GraphNode<Integer, Integer> nodeB = new GraphNode<>(2, 9, 0, NodeType.HOUSE, new ArrayList<>(), new ArrayList<>(), () -> {});
        GraphNode<Integer, Integer> nodeC = new GraphNode<>(3, 0, 9, NodeType.BLANK, new ArrayList<>(), new ArrayList<>(), () -> {});
        GraphNode<Integer, Integer> nodeD = new GraphNode<>(4, 9, 9, NodeType.ROAD, new ArrayList<>(), new ArrayList<>(), () -> {});
        GraphNode<Integer, Integer> nodeE = new GraphNode<>(5, 4, 4, NodeType.ROAD, new ArrayList<>(), new ArrayList<>(), () -> {});
//        nodeA.addNeighbour(new ConnectedNode<>(nodeB, 40));
//        nodeB.addNeighbour(new ConnectedNode<>(nodeA, 40));
//        nodeA.addNeighbour(new ConnectedNode<>(nodeC, 40));
//        nodeC.addNeighbour(new ConnectedNode<>(nodeA, 40));
        nodeB.addNeighbour(new ConnectedNode<>(nodeD, 10));
        nodeD.addNeighbour(new ConnectedNode<>(nodeB, 10));
        nodeC.addNeighbour(new ConnectedNode<>(nodeD, 10));
        nodeD.addNeighbour(new ConnectedNode<>(nodeC, 10));
        nodeC.addNeighbour(new ConnectedNode<>(nodeE, 10));
        nodeE.addNeighbour(new ConnectedNode<>(nodeC, 10));
        nodeB.addNeighbour(new ConnectedNode<>(nodeE, 50));
        nodeE.addNeighbour(new ConnectedNode<>(nodeB, 50));
        nodeD.addNeighbour(new ConnectedNode<>(nodeE, 10));
        nodeE.addNeighbour(new ConnectedNode<>(nodeD, 10));
        HashMap<Integer, GraphNode<Integer, Integer>> nodes = new HashMap<>();
        nodes.put(1, nodeA);
        nodes.put(2, nodeB);
        nodes.put(3, nodeC);
        nodes.put(4, nodeD);
        nodes.put(5, nodeE);
        Graph<Integer> graph = new Graph<>(nodes, 10, 10, () -> {}, () -> {});

        nodeE.addOccupier(new RobotMovement<>(new Robot<>(1, 1), 2, 4,4));

        return graph;
    }
}
