package livegraph;

public class ConnectedNode<R, N> {
    public final GraphNode<R, N> node;
    public final int edgeWeight;

    public ConnectedNode(GraphNode<R, N> node, int edgeWeight) {
        this.node = node;
        this.edgeWeight = edgeWeight;
    }
}
