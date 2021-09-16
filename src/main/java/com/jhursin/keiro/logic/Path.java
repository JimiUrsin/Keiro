package com.jhursin.keiro.logic;

import com.jhursin.keiro.logic.Node;
import com.jhursin.keiro.io.FileToImage;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Objects;
import java.util.PriorityQueue;
import java.util.Stack;

/**
 * Contains pathfinding algorithms
 */

class Point implements Comparable{
    int x;
    int y;
    int prio;
    
    Point(int x, int y, int prio) {
        this.x = x;
        this.y = y;
        this.prio = prio;
    }
    
    Point(int x, int y) {
        this.x = x;
        this.y = y;
        this.prio = 0;
    }
    
    void setPrio(int prio) {
        this.prio = prio;
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(x, y);
    }
    
    @Override
    public boolean equals(Object other) {
        if (other == null || other.getClass() != getClass()) {
            return false;
        }
        Point p = (Point) other;
        return this.x == p.x && this.y == p.y;
    }
    
    public int compare(Point o1, Point o2) {
        return o2.prio - o1.prio;
    } 

    @Override
    public int compareTo(Object o) {
        Point other = (Point) o;
        return this.prio - other.prio;
    }
}


public class Path {
    
    // Is diagonal movement allowed or not
    private static boolean diagonal = false;    
    static Point[] deltas = new Point[diagonal ? 8 : 4];
    
    private static void createDeltas() {
        deltas[0] = new Point(-1,  0);
        deltas[1] = new Point( 0, +1);
        deltas[2] = new Point(+1,  0);
        deltas[3] = new Point( 0, -1);
        
        if (diagonal) {
            deltas[4] = new Point(+1, +1);
            deltas[5] = new Point(+1, -1);
            deltas[6] = new Point(-1, -1);
            deltas[7] = new Point(-1, +1);
        }
    }
    
    
    public static void printNodeArray() {
        Grid grid = FileToImage.readFileToGrid("test.png");
        
        for(int y = 0; y < grid.nodes.length; y++) {
            for(int x = 0; x < grid.nodes.length; x++) {
                System.out.print(String.format("%06X\t", grid.nodes[y][x].getRGB()));
            }
            System.out.println();
        }
    }
    
    public static Grid solveAStar(Grid grid) {
        if (deltas[0] == null) createDeltas();
        PriorityQueue<Point> open = new PriorityQueue<>();
        
        Point start = new Point(grid.getStartX(), grid.getStartY());
        start.setPrio(distance(start, grid.getEndX(), grid.getEndY()));
        open.add(start);
        
        HashMap<Point, Point> cameFrom = new HashMap<>();
        
        HashMap<Point, Integer> gScore = new HashMap<>();
        gScore.put(start, 0);
        
        HashMap<Point, Integer> fScore = new HashMap<>();
        fScore.put(start, distance(start, grid.getEndX(), grid.getEndY()));
        
        int operations = 0;
        while(!open.isEmpty()) {
            Point curr = open.poll();
            operations++;
            if (curr.x == grid.getEndX() && curr.y == grid.getEndY()) {
                break;
            }
            
            open.remove(curr);
            for(Point d : deltas) {
                Point next = new Point(curr.x + d.x, curr.y + d.y);
                if (!valid(next, grid)) continue;
                // System.out.println(String.format("Next is (%d, %d)", next.x, next.y));
                int tempG = gScore.getOrDefault(curr, Integer.MAX_VALUE) + 1;
                if (tempG < gScore.getOrDefault(next, Integer.MAX_VALUE)) {
                    cameFrom.put(next, curr);
                    gScore.put(next, tempG);
                    fScore.put(next, tempG + distance(next, grid.getEndX(), grid.getEndY()));
                    if (!open.contains(next)) {
                        next.setPrio(fScore.get(next));
                        open.add(next);
                    }
                }
            }
        }
        System.out.println(cameFrom.get(new Point(grid.getEndX(), grid.getEndY())) == null ? "Goal was not found" : "Goal was found");
        System.out.println("Operations = " + operations);
        
        ArrayDeque<Point> path = new ArrayDeque<>();
        Point curr = new Point(grid.getEndX(), grid.getEndY());
        while(curr != start) {
            System.out.println(String.format("Adding (%d, %d) to path", curr.x, curr.y));
            grid.nodes[curr.y][curr.x] = Node.PATH;
            for(Point d: deltas) {
                Point fill = new Point(curr.x + d.x, curr.y + d.y);
                if (!valid(fill, grid)) continue;                
                grid.nodes[fill.y][fill.x] = Node.PATH;
            }
            curr = cameFrom.get(curr);
        }
        return grid;
    }
    
    static int distance(Point a, int endX, int endY) {
        int dx = Math.abs(endX - a.x);
        int dy = Math.abs(endY - a.y);
        
        if (diagonal) {
            return Math.max(dx, dy);
        } else {
            return dx + dy;
        }
    }
    
    static boolean valid(Point a, Grid g) {
        return (a.x >= 0 && a.x < g.nodes[0].length) &&
               (a.y >= 0 && a.y < g.nodes.length) &&
               (g.nodes[a.y][a.x] != Node.BLOCKED);
    }
}
