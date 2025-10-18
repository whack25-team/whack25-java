package livegraph;

import java.util.ArrayList;
import java.util.HashMap;

public class Graph<N, R> {
    HashMap<N, GraphNode<R, N>> nodes;

    public Graph(HashMap<N, GraphNode<R, N>> nodes) {
        this.nodes = nodes;
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
        Graph<String, String> graph = new Graph<>(nodes);

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
}
