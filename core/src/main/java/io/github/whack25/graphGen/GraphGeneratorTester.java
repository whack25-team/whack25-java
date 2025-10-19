package io.github.whack25.graphGen;

import livegraph.Graph;

public class GraphGeneratorTester {
    public void Test() {
        GraphGenerator generator = new GraphGenerator();
        Graph<Integer> graph = generator.generate(20, 25, 0.45, 0.2);

        /*for (int i = 0; i < 20; i++) {
            for (int j = 0; j < 25; j++) {
                System.out.print((graph[i][j] == 0 ? " " : graph[i][j]) + " ");
            }
            System.out.println();
        }*/

    }

    public static void main(String[] args) {
        for (int i = 0; i < 1; i++) {
            GraphGeneratorTester tester = new GraphGeneratorTester();
            tester.Test();
        }
    }
}
