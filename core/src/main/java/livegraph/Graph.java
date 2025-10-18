package livegraph;

import java.util.HashMap;

public class Graph<N, R> {
    HashMap<N, GraphNode<R, N>> nodes;

    public Graph(HashMap<N, GraphNode<R, N>> nodes) {
        this.nodes = nodes;
    }
}
