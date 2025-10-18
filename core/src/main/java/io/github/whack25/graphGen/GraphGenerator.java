package io.github.whack25.graphGen;
import java.util.ArrayList;
import java.util.HashMap;

import livegraph.Graph;
import livegraph.GraphNode;
import livegraph.NodeType;

public class GraphGenerator {

    final int EDGE_STRAIGHT = 3;
    final int EDGE_UTURN= 5;

    public Graph<Integer,Integer> generate(int width, int height, double coverageGoal) {
        return convertGraphToCellGraph(generateGraph(width, height, coverageGoal));
    }

    /**
     * Generates a graph with the specified dimensions and coverage goal.
     * Nodes generated are 2 thick so the graph cell-width is double the size of the input dimensions.
     * @param x Width of the graph
     * @param y Height of the graph
     * @param coverageGoal Desired coverage as a fraction (0.0 to 1.0)
     */
    public int[][] generateGraph(int width, int height, double coverageGoal) {
        
        int[][] graph = new int[width][height];

        int coverage = 0; // number of covered cells



        // generate random start point
        int x = (int)(Math.random() * width);
        int y = (int)(Math.random() * height);

        // generate paths until coverage goal is met
        while (coverage / (double)(width * height) < coverageGoal) {

            if (graph[x][y] == 0) { // if not path
                graph[x][y] = 1; // mark as path
                coverage++;
            }

            int direction = 0; // holds last direction. north = 0, east = 1, south = 2, west = 3
            int pathLength = 0;

            while (coverage / (double)(width * height) < coverageGoal) {

                int length = (int)(Math.random() * 5) + 2; // random length between 2 and 6

                int newDirection = ((direction + (int)(Math.random() * 3) - 1) + 4) % 4; // random direction -1 betwen 1

                // update x coordinate
                int newX = x;
                int newY = y;
                switch (newDirection) {
                    case 0: 
                        newY = y - length; // north
                        break;
                    case 1: 
                        newX = x + length; // east
                        break;
                    case 2: 
                        newY = y + length; // south
                        break;
                    case 3: 
                        newX = x - length; // west
                        break;
                    default: 
                        newY = 0;
                        break;
                }

                // set correct bounds of graph
                if (newX <= 0) {
                    newX = 0;
                    length = x;
                }
                else if (newX >= width) {
                    newX = width - 1;
                    length = width - x - 1;
                }
                if (newY <= 0) {
                    newY = 0;
                    length = y;
                }
                else if (newY >= height) 
                {
                    newY = height - 1;
                    length = height -  y - 1;
                }


                //System.out.println(x + ",  " +newX + ",  " + y + ",  " + newY+",  " + length + ",  " + newDirection);

                // write path and check for overlap
                int i;
                for (i = 1; i <= length; i++) {
                    //System.out.println("  step " + i);
                    int xInc = x;
                    int yInc = y;
                    switch (newDirection) {
                        case 0 : {yInc = y-i; break;} // north
                        case 1 : {xInc = x+i; break;} // east
                        case 2 : {yInc = y+i; break;} // south
                        case 3 : {xInc = x-i; break;} // west
                        default : {xInc = -2; yInc = -2; break;}
                    }

                    if (graph[xInc][yInc] == 0) { // if not path

                            int paths = 0;
                            switch (newDirection) {
                                case 0: 
                                    if (xInc < width - 1 && graph[xInc+1][yInc] == 1) paths++;
                                    if (xInc > 0 && graph[xInc-1][yInc] == 1) paths++;
                                    break;
                                case 1:
                                    if (yInc < height - 1 && graph[xInc][yInc+1] == 1) paths++;
                                    if (yInc > 0 && graph[xInc][yInc-1] == 1) paths++;
                                    break;
                                case 2: 
                                    if (xInc < width - 1 && graph[xInc+1][yInc] == 1) paths++;
                                    if (xInc > 0 && graph[xInc-1][yInc] == 1) paths++;
                                    break;
                                case 3: 
                                    if (yInc < height - 1 && graph[xInc][yInc+1] == 1) paths++;
                                    if (yInc > 0 && graph[xInc][yInc-1] == 1) paths++;
                                    break;
                                default: 
                                    break;
                            }
                            if (paths < 1) { // if even number of paths adjacent, stop path
                                graph[xInc][yInc] = 1; // mark as path
                                coverage++;
                            }
                            else {
                                break; // stop path if overlap
                            }
                    }
                }

                // try end path
                pathLength += i;
                if ((Math.random() * pathLength) >= 15) {
                    break;
                }
                // update values
                if (i == length) {
                    x = newX;
                    y = newY;
                } else {
                    x = x + (newDirection == 1 ? i - 1 : (newDirection == 3 ? -i + 1 : 0));
                    y = y + (newDirection == 2 ? i - 1   : (newDirection == 0 ? -i + 1 : 0));
                }
                direction = newDirection;

            }

            // generate new random start point on known paths ------------------- very inefficient, needs improvement
            boolean found = false;
            while (!found) {
                x = (int)(Math.random() * width);
                y = (int)(Math.random() * height);

                if (graph[x][y] > 0) { // if path
                    found = true;
                }
            }
        }

        //add junctions
        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                if (graph[i][j] > 0) {
                    int paths = 0;
                    if (i < width - 1 && graph[i+1][j] > 0) paths++;
                    if (i > 0 && graph[i-1][j] > 0) paths++;
                    if (j < height - 1 && graph[i][j+1] > 0) paths++;
                    if (j > 0 && graph[i][j-1] > 0) paths++;

                    if (paths >= 3) {
                        graph[i][j] = 2; // mark as junction
                    }
                }
            }
        }

        return graph;
    }

    // generate ID for nodes
    public int GenerateId(int x, int y, int width) {
        return x + y * width;
    }

    /**
     * Converts a 2D int array graph representation to a graph.
     * @param table
     * @return
     */
    public Graph<Integer, Integer> convertGraphToCellGraph(int[][] table) {
        Graph<Integer, Integer> graph = new Graph<Integer, Integer>(new HashMap<Integer, GraphNode<Integer, Integer>>(), table.length * 2, table[0].length * 2);

        int width = table.length;
        int height = table[0].length;

        int[][] nodeTable = new int[width * 2][height * 2];
        // generate nodes for all corridors and junctions
        for (int i = 0; i < table.length; i++) {
            for (int j = 0; j < table[i].length; j++) {
                if (table[i][j] > 0) {
                    nodeTable[i*2][j*2] = GenerateId(2 * i, 2 * j, width * 2);
                    graph.addNode(new GraphNode<>(nodeTable[i*2][j*2], 2 * i, 2 * j, NodeType.ROAD, new ArrayList<>(), new ArrayList<>()));
                    nodeTable[i*2 + 1][j*2] = GenerateId(2 * i + 1, 2 * j, width * 2);
                    graph.addNode(new GraphNode<>(nodeTable[i*2 + 1][j*2], 2 * i + 1, 2 * j, NodeType.ROAD, new ArrayList<>(), new ArrayList<>()));
                    nodeTable[i*2][j*2 + 1] = GenerateId(2 * i, 2 * j + 1, width * 2);
                    graph.addNode(new GraphNode<>(nodeTable[i*2][j*2 + 1], 2 * i, 2 * j + 1, NodeType.ROAD, new ArrayList<>(), new ArrayList<>()));
                    nodeTable[i*2 + 1][j*2 + 1] = GenerateId(2 * i + 1, 2 * j + 1, width * 2);
                    graph.addNode(new GraphNode<>(nodeTable[i*2 + 1][j*2 + 1], 2 * i + 1, 2 * j + 1, NodeType.ROAD, new ArrayList<>(), new ArrayList<>()));
                }
            }
        }

        for (int i = 0; i < width; i++) {
            System.out.println("row: " + i);
            for (int j = 0; j < height; j++) {
                System.out.println("column: " + j);
                int[] directions = new int[4];
                int sum = 0;

                if (i < width - 1 && table[i+1][j] > 0) {
                    directions[1] = 1;
                    sum++;
                }
                if (i > 0 && table[i-1][j] > 0) {
                    directions[3] = 1;
                    sum++;
                }
                if (j < height - 1 && table[i][j+1] > 0) {
                    directions[2] = 1;
                    sum++;
                }
                if (j > 0 && table[i][j-1] > 0) {
                    directions[0] = 1;
                    sum++;
                }

                boolean isAdjacent = false;
                for (int d = 0; d < 3; d++) {
                    if (directions[d] == 1 && directions[d+1] == 1) {
                        isAdjacent = true;
                    }
                }
                int x = i * 2;
                int y = j * 2;
                System.out.println("Node at " + x + ", " + y 
                + " with objs at directions: N:" + directions[0] + " E:" + directions[1] + " S:" + directions[2] + " W:" + directions[3]
                 + " with " + table[i][j] + "type.");
                if (nodeTable[x][y] > 0) {
                    if (isAdjacent) { // add corner edges
                        if (directions[2] == 1 && directions[3] == 1) { // north and east empty
                            graph.addUnidirectionalEdge(nodeTable[x][y], nodeTable[x+1][y], EDGE_STRAIGHT);
                            graph.addUnidirectionalEdge(nodeTable[x+1][y], nodeTable[x+1][y+1], EDGE_STRAIGHT);
                            graph.addUnidirectionalEdge(nodeTable[x+1][y+1], nodeTable[x+1][y+2], EDGE_STRAIGHT);
                            graph.addUnidirectionalEdge(nodeTable[x][y+2], nodeTable[x][y+1], EDGE_STRAIGHT);
                            graph.addUnidirectionalEdge(nodeTable[x][y+1], nodeTable[x][y], EDGE_UTURN);
                            graph.addUnidirectionalEdge(nodeTable[x][y+1], nodeTable[x+1][y+1], EDGE_UTURN);
                            graph.addUnidirectionalEdge(nodeTable[x][y], nodeTable[x][y+1], EDGE_UTURN);
                            graph.addUnidirectionalEdge(nodeTable[x+1][y+1], nodeTable[x][y+1], EDGE_UTURN);
                        } else if (directions[3] == 1 && directions[0] == 1) { // east and south empty
                            graph.addUnidirectionalEdge(nodeTable[x+1][y], nodeTable[x+1][y+1], EDGE_STRAIGHT);
                            graph.addUnidirectionalEdge(nodeTable[x+1][y], nodeTable[x+1][y+1], EDGE_STRAIGHT);
                            graph.addUnidirectionalEdge(nodeTable[x][y], nodeTable[x+1][y], EDGE_UTURN);
                            graph.addUnidirectionalEdge(nodeTable[x][y], nodeTable[x][y+1], EDGE_UTURN);
                            graph.addUnidirectionalEdge(nodeTable[x+1][y], nodeTable[x][y], EDGE_UTURN);
                            graph.addUnidirectionalEdge(nodeTable[x][y+1], nodeTable[x][y], EDGE_UTURN);
                        } else if (directions[0] == 1 && directions[1] == 1) { // south and west empty
                            graph.addUnidirectionalEdge(nodeTable[x+1][y+1], nodeTable[x][y+1], EDGE_STRAIGHT);
                            graph.addUnidirectionalEdge(nodeTable[x][y+1], nodeTable[x][y], EDGE_STRAIGHT);
                            graph.addUnidirectionalEdge(nodeTable[x+1][y], nodeTable[x+2][y], EDGE_STRAIGHT);
                            graph.addUnidirectionalEdge(nodeTable[x+2][y+1], nodeTable[x+1][y+1], EDGE_STRAIGHT);
                            graph.addUnidirectionalEdge(nodeTable[x+1][y], nodeTable[x+1][y+1], EDGE_UTURN);
                            graph.addUnidirectionalEdge(nodeTable[x+1][y], nodeTable[x][y], EDGE_UTURN);
                            graph.addUnidirectionalEdge(nodeTable[x+1][y+1], nodeTable[x+1][y], EDGE_UTURN);
                            graph.addUnidirectionalEdge(nodeTable[x][y], nodeTable[x+1][y], EDGE_UTURN);
                        } else if (directions[1] == 1 && directions[2] == 1) { // west and north empty
                            graph.addUnidirectionalEdge(nodeTable[x][y+1], nodeTable[x][y], EDGE_STRAIGHT);
                            graph.addUnidirectionalEdge(nodeTable[x][y], nodeTable[x+1][y], EDGE_STRAIGHT);
                            graph.addUnidirectionalEdge(nodeTable[x+1][y], nodeTable[x+2][y], EDGE_STRAIGHT);
                            graph.addUnidirectionalEdge(nodeTable[x+2][y+1], nodeTable[x+1][y+1], EDGE_STRAIGHT);
                            graph.addUnidirectionalEdge(nodeTable[x+1][y+1], nodeTable[x+1][y+2], EDGE_STRAIGHT);
                            graph.addUnidirectionalEdge(nodeTable[x][y+2], nodeTable[x][y+1], EDGE_STRAIGHT);
                            graph.addUnidirectionalEdge(nodeTable[x+1][y+1], nodeTable[x][y+1], EDGE_UTURN);
                            graph.addUnidirectionalEdge(nodeTable[x+1][y+1], nodeTable[x+1][y], EDGE_UTURN);
                            graph.addUnidirectionalEdge(nodeTable[x][y+1], nodeTable[x+1][y+1], EDGE_UTURN);
                            graph.addUnidirectionalEdge(nodeTable[x+1][y], nodeTable[x+1][y+1], EDGE_UTURN);
                        }
                    } else if (directions[0] == 1 && sum == 2) { // vertical line
                        graph.addUnidirectionalEdge(nodeTable[x][y+1], nodeTable[x][y], EDGE_STRAIGHT);
                        graph.addUnidirectionalEdge(nodeTable[x][y+2], nodeTable[x][y+1], EDGE_STRAIGHT);
                        graph.addUnidirectionalEdge(nodeTable[x+1][y], nodeTable[x+1][y+1], EDGE_STRAIGHT);
                        graph.addUnidirectionalEdge(nodeTable[x+1][y+1], nodeTable[x+1][y+2], EDGE_STRAIGHT);
                        graph.addUnidirectionalEdge(nodeTable[x][y], nodeTable[x+1][y], EDGE_UTURN);
                        graph.addUnidirectionalEdge(nodeTable[x][y+1], nodeTable[x+1][y+1], EDGE_UTURN);
                        graph.addUnidirectionalEdge(nodeTable[x+1][y], nodeTable[x][y], EDGE_UTURN);
                        graph.addUnidirectionalEdge(nodeTable[x+1][y+1], nodeTable[x][y+1], EDGE_UTURN);
                    } else if (directions[1] == 1 && sum == 2) { // horizontal line
                        graph.addUnidirectionalEdge(nodeTable[x][y], nodeTable[x+1][y], EDGE_STRAIGHT);
                        graph.addUnidirectionalEdge(nodeTable[x+1][y], nodeTable[x+2][y], EDGE_STRAIGHT);
                        graph.addUnidirectionalEdge(nodeTable[x+1][y+1], nodeTable[x][y+1], EDGE_STRAIGHT);
                        graph.addUnidirectionalEdge(nodeTable[x+2][y+1], nodeTable[x+1][y+1], EDGE_STRAIGHT);
                        graph.addUnidirectionalEdge(nodeTable[x][y], nodeTable[x][y+1], EDGE_UTURN);
                        graph.addUnidirectionalEdge(nodeTable[x][y+1], nodeTable[x][y], EDGE_UTURN);
                        graph.addUnidirectionalEdge(nodeTable[x+1][y+1], nodeTable[x+1][y], EDGE_UTURN);
                        graph.addUnidirectionalEdge(nodeTable[x+1][y], nodeTable[x+1][y+1], EDGE_UTURN);
                    } else if (directions[0] == 1) {
                        graph.addUnidirectionalEdge(nodeTable[x+1][y], nodeTable[x+1][y+1], EDGE_STRAIGHT);
                        graph.addUnidirectionalEdge(nodeTable[x+1][y+1], nodeTable[x][y+1], EDGE_STRAIGHT);
                        graph.addUnidirectionalEdge(nodeTable[x][y+1], nodeTable[x][y], EDGE_STRAIGHT);
                        graph.addUnidirectionalEdge(nodeTable[x][y], nodeTable[x+1][y], EDGE_UTURN);
                        graph.addUnidirectionalEdge(nodeTable[x+1][y], nodeTable[x][y], EDGE_UTURN);
                    } else if (directions[1] == 1) {
                        graph.addUnidirectionalEdge(nodeTable[x+2][y+1], nodeTable[x+1][y+1], EDGE_STRAIGHT);
                        graph.addUnidirectionalEdge(nodeTable[x+1][y+1], nodeTable[x][y+1], EDGE_STRAIGHT);
                        graph.addUnidirectionalEdge(nodeTable[x][y+1], nodeTable[x][y], EDGE_STRAIGHT);
                        graph.addUnidirectionalEdge(nodeTable[x][y], nodeTable[x+1][y], EDGE_STRAIGHT);
                        graph.addUnidirectionalEdge(nodeTable[x+1][y], nodeTable[x+2][y], EDGE_STRAIGHT);
                        graph.addUnidirectionalEdge(nodeTable[x+1][y+1], nodeTable[x+1][y], EDGE_UTURN);
                        graph.addUnidirectionalEdge(nodeTable[x+1][y], nodeTable[x+1][y+1], EDGE_UTURN);
                    } else if (directions[2] == 1) {
                        graph.addUnidirectionalEdge(nodeTable[x][y+2], nodeTable[x][y+1], EDGE_STRAIGHT);
                        graph.addUnidirectionalEdge(nodeTable[x][y+1], nodeTable[x][y], EDGE_STRAIGHT);
                        graph.addUnidirectionalEdge(nodeTable[x][y], nodeTable[x+1][y], EDGE_STRAIGHT);
                        graph.addUnidirectionalEdge(nodeTable[x+1][y], nodeTable[x+1][y+1], EDGE_STRAIGHT);
                        graph.addUnidirectionalEdge(nodeTable[x+1][y+1], nodeTable[x+1][y+2], EDGE_STRAIGHT);
                        graph.addUnidirectionalEdge(nodeTable[x+1][y+1], nodeTable[x][y+1], EDGE_UTURN);
                        graph.addUnidirectionalEdge(nodeTable[x][y+1], nodeTable[x+1][y+1], EDGE_UTURN);
                    } else if (directions[3] == 1) {
                        graph.addUnidirectionalEdge(nodeTable[x][y], nodeTable[x+1][y], EDGE_STRAIGHT);
                        graph.addUnidirectionalEdge(nodeTable[x+1][y], nodeTable[x+1][y+1], EDGE_STRAIGHT);
                        graph.addUnidirectionalEdge(nodeTable[x+1][y+1], nodeTable[x][y+1], EDGE_STRAIGHT);
                        graph.addUnidirectionalEdge(nodeTable[x][y], nodeTable[x][y+1], EDGE_UTURN);
                        graph.addUnidirectionalEdge(nodeTable[x][y+1], nodeTable[x][y], EDGE_UTURN);
                    }
                }
            }
        }

        return graph;
    }

    public void createHousesOnGraph(Graph graph, int[][] nodeTable) {

    }
}