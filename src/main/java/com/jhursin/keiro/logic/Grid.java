package com.jhursin.keiro.logic;

/**
 * Contains a two-dimensional array of Nodes.
 * Keeps note of where the start and end of the Grid is
 */
public class Grid {

    // CHECKSTYLE:OFF
    // These are all self-explanatory
    private int startX = -1;
    private int startY = -1;

    private int endX = -1;
    private int endY = -1;

    public Node[][] nodes;

    public int obstacles;
    // CHECKSTYLE:ON

    /**
     * Create a new Grid with a two-dimensional array of Nodes.
     * @param width Width of Node array
     * @param height Height of Node array
     */
    public Grid(final int width, final int height) {
        this.nodes = new Node[height][width];
    }

    /**
     * Set the starting point for this Grid.
     * @param x X coordinate of starting point
     * @param y Y coordinate of starting point
     */
    public final void setStart(final int x, final int y) {
        nodes[y][x] = Node.START;
        this.startX = x;
        this.startY = y;
    }

     /**
     * Set the ending point for this Grid.
     * @param x X coordinate of ending point
     * @param y Y coordinate of ending point
     */
    public final void setEnd(final int x, final int y) {
        nodes[y][x] = Node.END;
        this.endX = x;
        this.endY = y;
    }

    // CHECKSTYLE:OFF
    public int getStartX() {
        return startX;
    }

    public int getStartY() {
        return startY;
    }

    public int getEndX() {
        return endX;
    }

    public int getEndY() {
        return endY;
    }
    // CHECKSTYLE:ON
}
