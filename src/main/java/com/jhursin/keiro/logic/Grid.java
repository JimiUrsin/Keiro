package com.jhursin.keiro.logic;

public class Grid {
    private int startX = -1;
    private int startY = -1;
    
    private int endX = -1;
    private int endY = -1;
    
    public Node[][] nodes;
    
    public Grid(int width, int height) {
        this.nodes = new Node[height][width];
    }
    
    public void setStart(int x, int y) {
        nodes[y][x] = Node.START;
        this.startX = x;
        this.startY = y;
    }
    
    public void setEnd(int x, int y) {
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
