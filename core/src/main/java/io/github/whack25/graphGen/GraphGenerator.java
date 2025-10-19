package io.github.whack25.graphGen;
import java.util.ArrayList;
import java.util.HashMap;

import livegraph.Graph;
import livegraph.GraphNode;
import livegraph.NodeType;

public class GraphGenerator {

    final int EDGE_STRAIGHT = 15;
    final int EDGE_UTURN = 25;

    final int MAX_COVERAGE_ITERATIONS = 1024;
    /**
     * Generates a graph with the specified dimensions and coverage goal, creates houses on it, and displays it.
     * @param width Width of the graph
     * @param height Height of the graph
     * @param coverageGoal Desired coverage as a fraction (0.0 to 1.0)
     * @return Generated graph
     */
    public Graph<Integer> generate(int width, int height, double coverageGoal, double probability) {
        GraphData graphData = convertGraphToCellGraph(generateGraph(generatePublicTransport(width, height), coverageGoal));
        createHousesOnGraph(graphData, probability);
        displayGraph(graphData, 0, 0);
        return graphData.graph;
    }

    public int[][] generatePublicTransport(int width, int height) {
        int[][] graph = new int[width][height];

        // generate random start point
        int x1 =(int)(Math.random() * width);
        int y1 = (int)(Math.random() * height);

        int x2 = (int)(Math.random() * width);
        int y2 = (int)(Math.random() * height);

        while ((x1 * x1 + y1 * y1)  - (x2 * x2 + y2 * y2) < ((width + height) / 3) * ((width + height) / 3)) { // ensure points are far enough apart
            x1 =(int)(Math.random() * width);
            y1 = (int)(Math.random() * height);
            x2 = (int)(Math.random() * width);
            y2 = (int)(Math.random() * height);
        }

        // draw line between the points
        int dx = x2 - x1;
        int dy = y2 - y1;
        int nx = Math.abs(dx);
        int ny = Math.abs(dy);
        int signX = dx > 0 ? 1 : -1;
        int signY = dy > 0 ? 1 : -1;

        int px = x1, py = y1;

        for (int ix = 0, iy = 0; ix < nx || iy < ny; ) {
            if ((0.5 + ix) / nx < (0.5 + iy) / ny) {
                // next step is horizontal
                px += signX;
                ix++;
            } else {
                // next step is vertical
                py += signY;
                iy++;
            }
            if (px >= 0 && px < width && py >= 0 && py < height) {
                graph[px][py] = 3; // mark as public transport route
            }
        }
        return graph;
    }

    /**
     * Generates a graph with the specified dimensions and coverage goal.
     * Nodes generated are 2 thick so the graph cell-width is double the size of the input dimensions.
     * @param width Width of the graph
     * @param height Height of the graph
     * @param coverageGoal Desired coverage as a fraction (0.0 to 1.0)
     */
    public int[][] generateGraph(int[][] graph, double coverageGoal) {

        int width = graph.length;
        int height = graph[0].length;

        int coverage = 0; // number of covered cells



        // generate random start point
        int x = width / 2;  //(int)(Math.random() * width);
        int y = height / 2;//(int)(Math.random() * height);

        // generate paths until coverage goal is met
        while (coverage / (double)(width * height) < coverageGoal) {

            if (graph[x][y] == 0) { // if not path
                graph[x][y] = 1; // mark as path
                coverage++;
            }

            int direction = (int)(Math.random() * 4); // holds last direction. north = 0, east = 1, south = 2, west = 3
            int pathLength = 0;

            int iterations = 0;

            while (coverage / (double)(width * height) < coverageGoal) { // while not maxed out coverage
                if (MAX_COVERAGE_ITERATIONS < iterations++) {
                    break;
                }

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
                            switch (newDirection) { // stop adding paths if there is a path to the side of the next one, avoids 2+ thick line of junctions
                                case 0:
                                    if (xInc < width - 1 && graph[xInc+1][yInc] > 0) paths++;
                                    if (xInc > 0 && graph[xInc-1][yInc] > 0) paths++;
                                    break;
                                case 1:
                                    if (yInc < height - 1 && graph[xInc][yInc+1] > 0) paths++;
                                    if (yInc > 0 && graph[xInc][yInc-1] > 0) paths++;
                                    break;
                                case 2:
                                    if (xInc < width - 1 && graph[xInc+1][yInc] > 0) paths++;
                                    if (xInc > 0 && graph[xInc-1][yInc] > 0) paths++;
                                    break;
                                case 3:
                                    if (yInc < height - 1 && graph[xInc][yInc+1] > 0) paths++;
                                    if (yInc > 0 && graph[xInc][yInc-1] > 0) paths++;
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

                // try end whole path randomly
                pathLength += i - 1;
                if ((Math.random() * pathLength) >= 15) {
                    break;
                }
                // update values
                if (i == length) {
                    x = newX;
                    y = newY;
                } else { // overlap occurred, set x and y to last valid position
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

                if (graph[x][y] > 0) { // if not empty cell
                    found = true;
                }
            }
        }

        //add junctions
        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                if (graph[i][j] > 0) {
                    int paths = 0;
                    // count adjacent paths
                    if (i < width - 1 && graph[i+1][j] > 0) paths++;
                    if (i > 0 && graph[i-1][j] > 0) paths++;
                    if (j < height - 1 && graph[i][j+1] > 0) paths++;
                    if (j > 0 && graph[i][j-1] > 0) paths++;

                    if (paths >= 3) {
                        graph[i][j] += 1; // mark as junction
                    }
                }
            }
        }

        return graph;
    }

    // generate ID for nodes
    public int GenerateId(int x, int y, int width) {
        return x + y * width + 1;
    }

    /**
     * Converts a 2D int array graph representation to a graph.
     * @param table
     * @return
     */
    public GraphData convertGraphToCellGraph(int[][] table) {
        Graph<Integer> graph = new Graph<Integer>(new HashMap<Integer, GraphNode<Integer, Integer>>(), table.length * 2, table[0].length * 2);

        int width = table.length;
        int height = table[0].length;

        int[][] nodeTable = new int[width * 2][height * 2];
        // generate nodes for all corridors and junctions
        for (int i = 0; i < table.length; i++) {
            for (int j = 0; j < table[i].length; j++) {
                NodeType type = table[i][j] > 2 ? NodeType.TRACK : NodeType.ROAD;
                if (table[i][j] > 0) {
                    nodeTable[i*2][j*2] = GenerateId(2 * i, 2 * j, width * 2);
                    graph.addNode(new GraphNode<>(nodeTable[i*2][j*2], 2 * i, 2 * j, type, new ArrayList<>(), new ArrayList<>()));
                    nodeTable[i*2 + 1][j*2] = GenerateId(2 * i + 1, 2 * j, width * 2);
                    graph.addNode(new GraphNode<>(nodeTable[i*2 + 1][j*2], 2 * i + 1, 2 * j, type, new ArrayList<>(), new ArrayList<>()));
                    nodeTable[i*2][j*2 + 1] = GenerateId(2 * i, 2 * j + 1, width * 2);
                    graph.addNode(new GraphNode<>(nodeTable[i*2][j*2 + 1], 2 * i, 2 * j + 1, type, new ArrayList<>(), new ArrayList<>()));
                    nodeTable[i*2 + 1][j*2 + 1] = GenerateId(2 * i + 1, 2 * j + 1, width * 2);
                    graph.addNode(new GraphNode<>(nodeTable[i*2 + 1][j*2 + 1], 2 * i + 1, 2 * j + 1, type, new ArrayList<>(), new ArrayList<>()));
                }
            }
        }

        // add edges for all nodes
        // done in sets of 2x2 cells as per graph node

        for (int i = 0; i < width; i++) {
            //System.out.println("row: " + i);
            for (int j = 0; j < height; j++) {
                //System.out.println("column: " + j);
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
                // count adjacent adjacent-paths to find cornders
                boolean isAdjacent = false;
                for (int d = 0; d < 4; d++) {
                    if (directions[d] == 1 && directions[(d+1) % 4] == 1) {
                        isAdjacent = true;
                    }
                }
                int x = i * 2;
                int y = j * 2;
                //displayGraph(nodeTable, x, y);
                /*System.out.println("Node at " + x + ", " + y
                + " with objs at directions: N:" + directions[0] + " E:" + directions[1] + " S:" + directions[2] + " W:" + directions[3]
                 + " with " + table[i][j] + "type.");*/

                // BEHOLD! THE EDGE ADDING STATEMENT OF DOOM

                // adds nodes within 2x2 cell and to the next 2x2 cells to the bottom and right
                if (nodeTable[x][y] > 0) {
                    if (sum > 2) { // is junction
                        graph.addUnidirectionalEdge(nodeTable[x][y], nodeTable[x+1][y], EDGE_STRAIGHT);
                        graph.addUnidirectionalEdge(nodeTable[x+1][y], nodeTable[x+1][y+1], EDGE_STRAIGHT);
                        graph.addUnidirectionalEdge(nodeTable[x+1][y+1], nodeTable[x][y+1], EDGE_STRAIGHT);
                        graph.addUnidirectionalEdge(nodeTable[x][y+1], nodeTable[x][y], EDGE_STRAIGHT);
                        if (directions[2] == 1) { // if south path solid
                            graph.addUnidirectionalEdge(nodeTable[x][y+2], nodeTable[x][y+1], EDGE_STRAIGHT);
                            graph.addUnidirectionalEdge(nodeTable[x+1][y+1], nodeTable[x+1][y+2], EDGE_STRAIGHT);
                        }
                        if (directions[1] == 1) { // if north path solid
                            graph.addUnidirectionalEdge(nodeTable[x+1][y], nodeTable[x+2][y], EDGE_STRAIGHT);
                            graph.addUnidirectionalEdge(nodeTable[x+2][y+1], nodeTable[x+1][y+1], EDGE_STRAIGHT);
                        }
                    }
                    else if (isAdjacent) { // add corner edges
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
                            graph.addUnidirectionalEdge(nodeTable[x+1][y+1], nodeTable[x][y+1], EDGE_STRAIGHT);
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
                    } else if (directions[0] == 1) { // only connect north
                        graph.addUnidirectionalEdge(nodeTable[x+1][y], nodeTable[x+1][y+1], EDGE_STRAIGHT);
                        graph.addUnidirectionalEdge(nodeTable[x+1][y+1], nodeTable[x][y+1], EDGE_STRAIGHT);
                        graph.addUnidirectionalEdge(nodeTable[x][y+1], nodeTable[x][y], EDGE_STRAIGHT);
                        graph.addUnidirectionalEdge(nodeTable[x][y], nodeTable[x+1][y], EDGE_UTURN);
                        graph.addUnidirectionalEdge(nodeTable[x+1][y], nodeTable[x][y], EDGE_UTURN);
                    } else if (directions[1] == 1) { // only connection east
                        graph.addUnidirectionalEdge(nodeTable[x+2][y+1], nodeTable[x+1][y+1], EDGE_STRAIGHT);
                        graph.addUnidirectionalEdge(nodeTable[x+1][y+1], nodeTable[x][y+1], EDGE_STRAIGHT);
                        graph.addUnidirectionalEdge(nodeTable[x][y+1], nodeTable[x][y], EDGE_STRAIGHT);
                        graph.addUnidirectionalEdge(nodeTable[x][y], nodeTable[x+1][y], EDGE_STRAIGHT);
                        graph.addUnidirectionalEdge(nodeTable[x+1][y], nodeTable[x+2][y], EDGE_STRAIGHT);
                        graph.addUnidirectionalEdge(nodeTable[x+1][y+1], nodeTable[x+1][y], EDGE_UTURN);
                        graph.addUnidirectionalEdge(nodeTable[x+1][y], nodeTable[x+1][y+1], EDGE_UTURN);
                    } else if (directions[2] == 1) { // only conneciton south
                        graph.addUnidirectionalEdge(nodeTable[x][y+2], nodeTable[x][y+1], EDGE_STRAIGHT);
                        graph.addUnidirectionalEdge(nodeTable[x][y+1], nodeTable[x][y], EDGE_STRAIGHT);
                        graph.addUnidirectionalEdge(nodeTable[x][y], nodeTable[x+1][y], EDGE_STRAIGHT);
                        graph.addUnidirectionalEdge(nodeTable[x+1][y], nodeTable[x+1][y+1], EDGE_STRAIGHT);
                        graph.addUnidirectionalEdge(nodeTable[x+1][y+1], nodeTable[x+1][y+2], EDGE_STRAIGHT);
                        graph.addUnidirectionalEdge(nodeTable[x+1][y+1], nodeTable[x][y+1], EDGE_UTURN);
                        graph.addUnidirectionalEdge(nodeTable[x][y+1], nodeTable[x+1][y+1], EDGE_UTURN);
                    } else if (directions[3] == 1) { // only connection west
                        graph.addUnidirectionalEdge(nodeTable[x][y], nodeTable[x+1][y], EDGE_STRAIGHT);
                        graph.addUnidirectionalEdge(nodeTable[x+1][y], nodeTable[x+1][y+1], EDGE_STRAIGHT);
                        graph.addUnidirectionalEdge(nodeTable[x+1][y+1], nodeTable[x][y+1], EDGE_STRAIGHT);
                        graph.addUnidirectionalEdge(nodeTable[x][y], nodeTable[x][y+1], EDGE_UTURN);
                        graph.addUnidirectionalEdge(nodeTable[x][y+1], nodeTable[x][y], EDGE_UTURN);
                    }
                }
            }
        }

        return new GraphData(graph, nodeTable);
    }

    public void displayGraph(GraphData graph, int x, int y) {

        for (int i = 0; i < graph.nodeTable.length; i++) {
            for (int j = 0; j < graph.nodeTable[i].length; j++) {
                if (i == x && j == y) {
                    System.out.print("X ");
                } else if (graph.nodeTable[i][j] == 0) {
                    System.out.print("  ");
                } else if (graph.graph.getNode(graph.nodeTable[i][j]).getTileType() == NodeType.HOUSE) {
                    System.out.print("H ");
                } else if (graph.graph.getNode(graph.nodeTable[i][j]).getTileType() == NodeType.TRACK) {
                    System.out.print("T ");
                } else {
                    System.out.print("- ");
                }
            }
            System.out.println();
        }
    }

    public void displayPreGraph(int[][] table) {
        for (int i = 0; i < table.length; i++) {
            for (int j = 0; j < table[i].length; j++) {
                System.out.print((table[i][j] == 0 ? " " : table[i][j]) + " ");
            }
            System.out.println();
        }
    }
    /**
     * Creates houses on the graph at empty nodes with a given probability.
     * @param graphData
     * @param probability
     */
    public void createHousesOnGraph(GraphData graphData, double probability) {
        for (int i = 0; i < graphData.getNodeTable().length; i++) {
            for (int j = 0; j < graphData.getNodeTable()[i].length; j++) {
                if (graphData.getNodeTable()[i][j] == 0) {
                    if (Math.random() < probability) {

                        ArrayList<Integer> directions = new ArrayList<Integer>();
                        if (i > 0 && graphData.getNodeTable()[i-1][j] > 0 && graphData.graph.getNode(graphData.getNodeTable()[i-1][j]).getTileType() == NodeType.ROAD) {
                            directions.add(3); // west
                        } else if (i < graphData.getNodeTable().length - 1 && graphData.getNodeTable()[i+1][j] > 0 && graphData.graph.getNode(graphData.getNodeTable()[i+1][j]).getTileType() == NodeType.ROAD) {
                            directions.add(1); // east
                        } else if (j > 0 && graphData.getNodeTable()[i][j-1] > 0 && graphData.graph.getNode(graphData.getNodeTable()[i][j-1]).getTileType() == NodeType.ROAD) {
                            directions.add(0); // north
                        } else if (j < graphData.getNodeTable()[i].length - 1 && graphData.getNodeTable()[i][j+1] > 0 && graphData.graph.getNode(graphData.getNodeTable()[i][j+1]).getTileType() == NodeType.ROAD) {
                            directions.add(2); // south
                        }

                        //System.out.println("Possible directions for house at " + i + ", " + j + ": " + directions.toString());
                        if (!directions.isEmpty()) {
                            int direction = directions.get((int)(Math.random() * directions.size()));
                            // create house facing direction
                            graphData.nodeTable[i][j] = GenerateId(i, j, graphData.getNodeTable().length);
                            graphData.graph.addNode( new GraphNode<>(graphData.nodeTable[i][j], i, j, NodeType.HOUSE, new ArrayList<>(), new ArrayList<>()));

                            int a = i;
                            int b = j;

                            switch (direction) {
                                case 0:
                                    b -= 1; // north
                                    break;
                                case 1:
                                    a += 1; // east
                                    break;
                                case 2:
                                    b += 1; // south
                                    break;
                                case 3:
                                    a -= 1; // west
                                    break;
                                default:
                                    b = 0;
                                    break;
                            }
                            //displayGraph(graphData, i, j);
                            //System.out.println("Creating house at " + i + ", " + j + " facing direction " + direction + " connecting to road at " + a + ", " + b);
                            graphData.graph.addUnidirectionalEdge(graphData.nodeTable[i][j], graphData.nodeTable[a][b], EDGE_STRAIGHT);
                            graphData.graph.addUnidirectionalEdge(graphData.nodeTable[a][b], graphData.nodeTable[i][j], EDGE_STRAIGHT);
                        }
                    }
                }
            }
        }
    }




    public class GraphData {
        public Graph<Integer> graph;
        public int[][] nodeTable;

        public GraphData(Graph<Integer> graph, int[][] nodeTable) {
            this.graph = graph;
            this.nodeTable = nodeTable;
        }
        public Graph<Integer> getGraph() {
            return graph;
        }
        public int[][] getNodeTable() {
            return nodeTable;
        }
    }
}
