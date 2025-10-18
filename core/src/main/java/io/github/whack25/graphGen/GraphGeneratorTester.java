package io.github.whack25.graphGen;

public class GraphGeneratorTester {
    public void Test() {
        GraphGenerator generator = new GraphGenerator();
        int[][] graph = generator.generateGraph(20, 25, 0.5);

        for (int i = 0; i < 20; i++) {
            for (int j = 0; j < 25; j++) {
                System.out.print((graph[i][j] == 0 ? " " : graph[i][j]) + " ");
            }
            System.out.println();
        }
    }

    public static void main(String[] args) {
        GraphGeneratorTester tester = new GraphGeneratorTester();
        tester.Test();
    }
}
