package livegraph;

import java.util.ArrayList;
import java.util.HashMap;

public class Graph<N, R> {
    private HashMap<N, GraphNode<R, N>> nodes;
    private final int gridWidth;
    private final int gridHeight;

    /**
     * Adds a graph with the nodes given. Note, gridWidth and gridHeight are only for reference and not enforced in any way.
     * Nodes may have coordinates outside of this range, and duplicates coordinates are not supported but are not checked for.
     * @param nodes the nodes in the graph
     * @param gridWidth
     * @param gridHeight
     */
    public Graph(HashMap<N, GraphNode<R, N>> nodes, int gridWidth, int gridHeight) {
        this.nodes = nodes;
        this.gridWidth = gridWidth;
        this.gridHeight = gridHeight;
    }

    public Graph(int gridWidth, int gridHeight) {
        this.nodes = new HashMap<>();
        this.gridWidth = gridWidth;
        this.gridHeight = gridHeight;
    }

    /**
     * Adds a node to the graph.
     * @param node the node to add
     */
    public void addNode(GraphNode<R,N> node) {
        if (node.getX() < 0 || node.getX() >= gridWidth || node.getY() < 0 || node.getY() >= gridHeight) {
            throw new IllegalArgumentException("Node coordinates out of bounds");
        }
        nodes.put(node.getNodeId(), node);
    }

    /**
     * Adds a unidirectional edge from fromNodeId to toNodeId with the given edge weight.
     * @param fromNodeId the ID of the node the edge is from
     * @param toNodeId the ID of the node the edge is to
     * @param edgeWeight the weight of the edge
     */
    public void addUnidirectionalEdge(N fromNodeId, N toNodeId, int edgeWeight) {
        GraphNode<R,N> fromNode = nodes.get(fromNodeId);
        GraphNode<R,N> toNode = nodes.get(toNodeId);
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
        for (GraphNode<R,N> node : nodes.values()) {
            node.tick();
        }
    }

    // Getters

    /**
     * Gets the nodes in the graph.
     * @return the nodes in the graph
     */
    public HashMap<N, GraphNode<R, N>> getNodes() {
        return nodes;
    }

    public int getGridWidth() {
        return gridWidth;
    }

    public int getGridHeight() {
        return gridHeight;
    }

    public static void main(String[] args) {
        GraphNode<String, String> nodeA = new GraphNode<>("A", 0, 0, NodeType.ROAD, new ArrayList<>(), new ArrayList<>());
        GraphNode<String, String> nodeB = new GraphNode<>("B", 1, 0, NodeType.ROAD, new ArrayList<>(), new ArrayList<>());
        GraphNode<String, String> nodeC = new GraphNode<>("C", 1, 0, NodeType.ROAD, new ArrayList<>(), new ArrayList<>());
        GraphNode<String, String> nodeD = new GraphNode<>("D", 1, 0, NodeType.ROAD, new ArrayList<>(), new ArrayList<>());
        GraphNode<String, String> nodeE = new GraphNode<>("E", 1, 0, NodeType.ROAD, new ArrayList<>(), new ArrayList<>());
        nodeA.addNeighbour(new ConnectedNode<>(nodeB, 4));
        nodeB.addNeighbour(new ConnectedNode<>(nodeA, 4));
        nodeA.addNeighbour(new ConnectedNode<>(nodeC, 4));
        nodeC.addNeighbour(new ConnectedNode<>(nodeA, 4));
        nodeB.addNeighbour(new ConnectedNode<>(nodeD, 1));
        nodeD.addNeighbour(new ConnectedNode<>(nodeB, 1));
        nodeC.addNeighbour(new ConnectedNode<>(nodeD, 1));
        nodeD.addNeighbour(new ConnectedNode<>(nodeC, 1));
        nodeC.addNeighbour(new ConnectedNode<>(nodeE, 1));
        nodeE.addNeighbour(new ConnectedNode<>(nodeC, 1));
        nodeB.addNeighbour(new ConnectedNode<>(nodeE, 5));
        nodeE.addNeighbour(new ConnectedNode<>(nodeB, 5));
        nodeD.addNeighbour(new ConnectedNode<>(nodeE, 1));
        nodeE.addNeighbour(new ConnectedNode<>(nodeD, 1));
        HashMap<String, GraphNode<String, String>> nodes = new HashMap<>();
        nodes.put("A", nodeA);
        nodes.put("B", nodeB);
        nodes.put("C", nodeC);
        nodes.put("D", nodeD);
        nodes.put("E", nodeE);
        Graph<String, String> graph = new Graph<>(nodes, 10, 10);

        GraphNode<String, String> nextNode = nodeE.getNextNodeOnPath("A");

        System.out.println(nextNode);

        // Testing with robot occupiers

        // Add a robot moving into node E from an arbitrary node, with an edge weight of 2
        nodeE.addOccupier(new RobotMovement<>(new Robot<>("Robo 1", "A"), 2));

        System.out.println(nodeE);
        nodeE.tick();
        System.out.println(nodeE);
        nodeE.tick();
        System.out.println(nodeE);
        System.out.println("----");
        nodeE.tick();
        System.out.println(nodeE);
        System.out.println(nodeC);
        nodeC.tick();
        System.out.println(nodeC);
        System.out.println("----");
        nodeC.tick();
        System.out.println(nodeC);
        System.out.println(nodeA);
        nodeA.tick();
        System.out.println(nodeA);

    }

    public static Graph<String, String> exampleGraph() {
        GraphNode<String, String> nodeA = new GraphNode<>("A", 0, 0, NodeType.ROAD, new ArrayList<>(), new ArrayList<>());
        GraphNode<String, String> nodeB = new GraphNode<>("B", 9, 0, NodeType.HOUSE, new ArrayList<>(), new ArrayList<>());
        GraphNode<String, String> nodeC = new GraphNode<>("C", 0, 9, NodeType.BLANK, new ArrayList<>(), new ArrayList<>());
        GraphNode<String, String> nodeD = new GraphNode<>("D", 9, 9, NodeType.ROAD, new ArrayList<>(), new ArrayList<>());
        GraphNode<String, String> nodeE = new GraphNode<>("E", 4, 4, NodeType.ROAD, new ArrayList<>(), new ArrayList<>());
        nodeA.addNeighbour(new ConnectedNode<>(nodeB, 4));
        nodeB.addNeighbour(new ConnectedNode<>(nodeA, 4));
        nodeA.addNeighbour(new ConnectedNode<>(nodeC, 4));
        nodeC.addNeighbour(new ConnectedNode<>(nodeA, 4));
        nodeB.addNeighbour(new ConnectedNode<>(nodeD, 1));
        nodeD.addNeighbour(new ConnectedNode<>(nodeB, 1));
        nodeC.addNeighbour(new ConnectedNode<>(nodeD, 1));
        nodeD.addNeighbour(new ConnectedNode<>(nodeC, 1));
        nodeC.addNeighbour(new ConnectedNode<>(nodeE, 1));
        nodeE.addNeighbour(new ConnectedNode<>(nodeC, 1));
        nodeB.addNeighbour(new ConnectedNode<>(nodeE, 5));
        nodeE.addNeighbour(new ConnectedNode<>(nodeB, 5));
        nodeD.addNeighbour(new ConnectedNode<>(nodeE, 1));
        nodeE.addNeighbour(new ConnectedNode<>(nodeD, 1));
        HashMap<String, GraphNode<String, String>> nodes = new HashMap<>();
        nodes.put("A", nodeA);
        nodes.put("B", nodeB);
        nodes.put("C", nodeC);
        nodes.put("D", nodeD);
        nodes.put("E", nodeE);
        Graph<String, String> graph = new Graph<>(nodes, 10, 10);

        nodeE.addOccupier(new RobotMovement<>(new Robot<>("Robo 1", "A"), 2));

        return graph;
    }
}
