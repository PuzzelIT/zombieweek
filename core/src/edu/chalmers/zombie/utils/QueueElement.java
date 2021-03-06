
package edu.chalmers.zombie.utils;

import java.awt.*;
import java.util.ArrayList;

/** A helper class for PathAlgorithm. It's used in the priority queue.
 * Created by Erik on 2015-04-01.
 */
public class QueueElement implements Comparable<QueueElement>{
    private Point node;
    private int g, h;
    ArrayList<Point> path;

    /**
     *  Constructor
     * @param node  Current node
     * @param g  The g cost to that node
    * @param h  The h cost to that node
     * @param path  The path to that node
     */

    public QueueElement(Point node, int g, int h, ArrayList<Point> path){
        if(node == null)
            throw new NullPointerException("QueueElement: node cannot be null");
        if(node.getX()<0 || node.getY() < 0)
            throw new IndexOutOfBoundsException("QueueElement: x and y values must be positive");
        this.node = node;
        if(path == null)
            this.path = new ArrayList<Point>();
        else
            this.path = path;
        this.g = g;
        this.h= h;
    }

    public int getGCost(){
        return g;
    }

    public int getHCost(){
        return h;
    }

    /**
     *
     * @return the cost of the path to the node
     */

    public int getCost() {
        return g + h;
    }

    /**
     *
     * @return the path to the node
     */
    public ArrayList<Point> getPath() {
        return path;
    }

    /**
     * @return the specified node
     */

    public Point getNode() {
        return node;
    }


    /**
     * Compares the cost with another queue element
     *
     * @param otherElement
     *            the other element
     * @returns 0 if the cost is the same, a number less than 0 if this element's cost is the smallest or a number more than 0 if the other element's cost is the least.
     * @throws NullPointerException if the other element is null
     */
    @Override
    public int compareTo(QueueElement otherElement) {if (otherElement == null)
        throw new NullPointerException(
                "QueueElement: Cannot compare with something that is null");
        if(equals(otherElement))
            return 0;
        else
            return Double.compare(this.getCost(), otherElement.getCost());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof QueueElement)) return false;

        QueueElement that = (QueueElement) o;

        if (g != that.g) return false;
        if (h != that.h) return false;
        if (node != null ? !node.equals(that.node) : that.node != null) return false;
        return !(path != null ? !path.equals(that.path) : that.path != null);

    }

    @Override
    public int hashCode() {
        int result = node != null ? node.hashCode() : 0;
        result = 31 * result + g;
        result = 31 * result + h;
        result = 31 * result + (path != null ? path.hashCode() : 0);
        return result;
    }
}


