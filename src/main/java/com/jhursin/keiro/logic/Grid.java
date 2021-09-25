package com.jhursin.keiro.logic;

/**
 * Contains a two-dimensional array of Nodes.
 * Keeps note of where the start and end of the Grid is
 */
public class Grid {
    private int startX = -1;
    private int startY = -1;

    private int endX = -1;
    private int endY = -1;

    public Node[][] nodes;

    public Grid(final int width, final int height) {
        this.nodes = new Node[height][width];
    }

    public final void setStart(int x, int y) {
        nodes[y][x] = Node.START;
        this.startX = x;
        this.startY = y;
    }

    public final void setEnd(final int x, final int y) {
        nodes[y][x] = Node.END;
        this.endX = x;
        this.endY = y;
    }

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
}
