
package edu.chalmers.zombie.utils;

import java.awt.*;
import java.util.*;


/**
 * Created by Erik on 2015-04-01.
 * This is the class which handles the path finding for the zombies. It uses A*, which is a modified version of Dijkstra's algorithm.
 */
public class PathAlgorithm {

    /** The start function for calculating the shortest path between two points on the map, with a default maxStep value of 50.
     * 
     * @param startPos The starting position (normally the zombie's position)
     * @param endPos   The end position (normally the player position)
     * @return  the shortest path as an iterator over points
     * @throws IndexOutOfBoundsException if the start or end position is negative
     * @throws  NullPointerException    if either of the points is null
     */

    public static ArrayList<Point> getPath(Point startPos, Point endPos, boolean[][] navigationalMesh) throws NullPointerException, IndexOutOfBoundsException {
        int maxSteps = 50;
        return getPath(startPos, endPos, navigationalMesh, maxSteps);
    }

    /** The start function for calculating the shortest path between two points on the map, a maximum number of steps included
     *
     * @param startPos The starting position (normally the zombie's position)
     * @param endPos   The end position (normally the player position)
     * @param maxSteps  The maximum number of steps
     * @return  the shortest path as an iterator over points
     * @throws IndexOutOfBoundsException if the start or end position is negative
     * @throws  NullPointerException    if either of the points is null
     */
    public static ArrayList<Point> getPath(Point startPos, Point endPos, boolean[][] navigationalMesh, int maxSteps) throws NullPointerException, IndexOutOfBoundsException {
        if (navigationalMesh == null)
            throw new NullPointerException("PathAlgorithm: the navigational mesh cannot be null");
        if(startPos == null || endPos == null)
            throw new NullPointerException("PathAlgorithm: the points given cannot be null");
        if(startPos.y<0 || startPos.x <0)
            throw new IndexOutOfBoundsException("PathAlgorithm: the start position must be positive");
        if(endPos.x<0 || endPos.y<0)
            throw new IndexOutOfBoundsException("PathAlgorithm: the end position must be positive");
        if(maxSteps<1)
            throw new IndexOutOfBoundsException("The number of steps must be >0");
        if(startPos.equals(endPos)){
            ArrayList<Point> singlePointArray = new ArrayList<Point>();
            singlePointArray.add(endPos);
            return singlePointArray;
        }
        return calculatePath(startPos,endPos,navigationalMesh,maxSteps);
    }

    /**
     * Calculates the shortest path given the data from the constructor
     *
     * @return the shortest path or, if none found, null
     */

    private static ArrayList<Point> calculatePath(Point startPos, Point endPos, boolean[][] navigationalMesh, int maxSteps) {
        PriorityQueue<QueueElement> queue = new PriorityQueue<QueueElement>();
        QueueElement currentElement;
        int width = navigationalMesh.length;
        int height = navigationalMesh[0].length;
        int steps = 0;
        boolean[][] closedNodes = new boolean[width][height];
        int[][] gCost = new int[width][height];         //holds the negative g value, since that will make the comparison easier.
        queue.add(new QueueElement(startPos, 0, 0, new ArrayList<Point>()));

        while (!queue.isEmpty()) {
            currentElement = queue.poll();
            Point currentNode = currentElement.getNode();
            if(currentElement.getPath()!=null)
              steps = currentElement.getPath().size();
            if(steps < maxSteps) {
                if (currentNode.equals(endPos)) {
                    currentElement.getPath().add(currentNode);
                    return currentElement.getPath();
                } else {
                    int x = currentNode.x;
                    int y = currentNode.y;
                    closedNodes[x][y] = true;
                    for (int i = Math.max(0, x - 1); i <= Math.min(width - 1, x + 1); i++) {
                        for (int j = Math.max(0, y - 1); j <= Math.min(height - 1, y + 1); j++) {
                            if (walkableTile(closedNodes, i, j, navigationalMesh)
                                    && noCornersCut(closedNodes, currentNode, i, j, navigationalMesh)) {
                                int g;
                                int h = 10 * (Math.abs(endPos.x - i) + Math.abs(endPos.y - j));     //Manhattan distance
                                if (isDiagonal(currentNode, i, j))
                                    g = currentElement.getGCost() + 14;  // 10 * sqrt(2) is approx. 14
                                else
                                    g = currentElement.getGCost() + 10;
                                if (gCost[i][j] < g) {        //Since the g value is below 0 this works. Otherwise it would have to be == 0 || < 0
                                    ArrayList<Point> currentPath = new ArrayList<Point>(currentElement.getPath());
                                    currentPath.add(currentNode);
                                    queue.add(new QueueElement(new Point(i, j), g, h, currentPath));
                                    gCost[i][j] = -g;
                                }
                            }
                        }
                    }
                }
                steps++;
            }
        }
        return null;    //No path found
    }//calculatePath

    /**Checks if the node in questions is walkable
     * @param closedNodes the matrix of closed nodes
     * @param x     The node's x variable
     * @param y     The node's y variable
     * @return  true if it's walkable, false if it isn't
     */
    private static boolean walkableTile(boolean[][] closedNodes, int x, int y, boolean[][] navigationalMesh){
            return !closedNodes[x][y] == true && navigationalMesh[x][y]==true;
    }


    /**Checks if the path is walkable, i.e. no corners are cut
     * @param closedNodes the matrix of closed nodes
     * @param parent    The parent element
     * @param x     The node's x variable
     * @param y     The node's y variable
     * @return true if it's walkable, false if it cuts corners
     */
    private static boolean noCornersCut(boolean[][] closedNodes, Point parent, int x, int y, boolean[][] navigationalMesh){
        int parentX = parent.x;
        int parentY = parent.y;
        if(isDiagonal(parent, x, y))
            return walkableTile(closedNodes, parentX, y, navigationalMesh) && walkableTile(closedNodes, x, parentY, navigationalMesh);
        else
            return true;
    }


    /** Checks if a point is diagonal to the parent
     *
     * @param parentNode The parent node
     * @param x     The node's x variable
     * @param y     The node's y variable
     * @return true if it is diagonal, false if it isn't
     */
    private static boolean isDiagonal(Point parentNode, int x, int y){
        int parentX = parentNode.x;
        int parentY = parentNode.y;
        return(x == parentX - 1 && y == parentY - 1) ||
                (x == parentX - 1 && y == parentY + 1) ||
                (x == parentX + 1 && y == parentY - 1) ||
                (x == parentX + 1 && y == parentY + 1);
    }

}