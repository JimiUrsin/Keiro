package com.jhursin.keiro.logic;

import com.jhursin.keiro.gui.MapWindow;
import com.jhursin.keiro.io.FileToImage;
import java.util.HashMap;
import java.util.Objects;
import java.util.PriorityQueue;
import java.util.Stack;

/**
 * Contains pathfinding algorithms.
 */
public class Path {

    // Is diagonal movement allowed or not
    private static boolean diagonal;
    static Point[] deltas;

    // Points whose x and y components can be summed with a point to get
    // Points for all directions we can move to
    private static void createDeltas() {
        deltas = new Point[diagonal ? 8 : 4];
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

    public static void setDiagonal(final boolean diag) {
        diagonal = diag;
        createDeltas();
    }

    public static void printNodeArray() {
        Grid grid = FileToImage.readFileToGrid("test.png");

        for (int y = 0; y < grid.nodes.length; y++) {
            for (int x = 0; x < grid.nodes.length; x++) {
                System.out.print(String.format("%06X\t", grid.nodes[y][x].getRGB()));
            }
            System.out.println();
        }
    }

    /**
     * Solves a grid with the A* algorithm, then draws its path on the grid
     * and onto the given MapWindow
     * @param grid Grid to be solved
     * @param mw MapWindow for drawing
     * @return Length of the best path
     */
    public static int solveAStar(final Grid grid, MapWindow mw) {
        boolean draw = true;
        
        if (mw == null) {
            draw = false;
        }
        
        if (deltas == null) {
            createDeltas();
        }

        // Open nodes
        PriorityQueue<Point> open = new PriorityQueue<>();

        // Add the starting point to the open queue with priority 0
        Point start = new Point(grid.getStartX(), grid.getStartY());
        start.setPrio(distance(start, grid.getEndX(), grid.getEndY()));
        open.add(start);
        if (draw) {
            mw.setRGB(start.x, start.y, Node.QUEUE.getRGB());
        }

        // Key = A point. 
        // Value = "Parent" point where we will come from on the best route
        HashMap<Point, Point> cameFrom = new HashMap<>();

        // G Score = cost of best currently known path to this node
        HashMap<Point, Integer> gScore = new HashMap<>();
        gScore.put(start, 0);

        // F score = G score + estimated cost to goal
        HashMap<Point, Integer> fScore = new HashMap<>();
        fScore.put(start, distance(start, grid.getEndX(), grid.getEndY()));

        // Stores how many points we have been to
        int operations = 0;

        long time = System.nanoTime();

        // How long to wait between each operation in nanoseconds
        long delay = 1000000;
        while (!open.isEmpty()) {
            while (System.nanoTime() - time < delay);
            time = System.nanoTime();

            // Get the Point with the highest priority
            Point curr = open.poll();
            operations++;

            // If polled Point was the goal, we are done
            if (curr.x == grid.getEndX() && curr.y == grid.getEndY()) {
                break;
            }

            open.remove(curr);
            if (draw) {
                mw.setRGB(curr.x, curr.y, Node.DROPPED.getRGB());
            }

            // Go through all neighbors
            for (Point d : deltas) {
                Point next = new Point(curr.x + d.x, curr.y + d.y);
                // If the neighbor is a blocked point or off the grid, just check the next one
                if (!valid(next, grid)) {
                    continue;
                }

                // Cost to this neighbor will be the best known cost of previous + 1
                int tempG = gScore.get(curr) + 1;

                // If G score through current route is lower, we have found a better route
                if (tempG < gScore.getOrDefault(next, Integer.MAX_VALUE)) {
                    // Change the route to the better one
                    cameFrom.put(next, curr);

                    // Set G score and f score
                    gScore.put(next, tempG);
                    fScore.put(next, tempG + distance(next, grid.getEndX(), grid.getEndY()));

                    if (!open.contains(next)) {
                        next.setPrio(fScore.get(next));
                        open.add(next);
                        if (draw) {
                            mw.setRGB(next.x, next.y, Node.QUEUE.getRGB());
                        }
                    }
                }
            }
        }

        int length = 1;
        if (cameFrom.get(new Point(grid.getEndX(), grid.getEndY())) == null) {
            System.out.println("Goal was not found");
        } else {
            System.out.println("Goal was found");

            // Start from the end            
            Point curr = new Point(grid.getEndX(), grid.getEndY());
            
            Stack<Point> path = new Stack<>();
            // Make our way back to the start
            while (curr != start) {
                // System.out.println(String.format("Adding (%d, %d) to path", curr.x, curr.y));
                grid.nodes[curr.y][curr.x] = Node.PATH;
                path.add(curr);
                /*
                // Fill neighbours too so the line is a bit thicker
                // TODO Make this a variable size
                for(Point d: deltas) {
                    Point fill = new Point(curr.x + d.x, curr.y + d.y);
                    if (!valid(fill, grid)) continue;                
                    grid.nodes[fill.y][fill.x] = Node.PATH;
                }
                */

                curr = cameFrom.get(curr);
                length++;
            }
            
            while(!path.empty()) {
                curr = path.pop();
                mw.setRGB(curr.x, curr.y, Node.PATH.getRGB());
                /*
                for(Point d : deltas) {
                    mw.setRGB(curr.x + d.x, curr.y + d.y, Node.PATH.getRGB());
                }
                */
                try {
                    Thread.sleep(10);
                } catch (Exception e) {
                    
                }
            }
        }
        System.out.println("Operations = " + operations);

        return length;
    }

    /**
     * Returns the distance between a point and an (x, y) coordinate.
     * If diagonal movement is allowed, returns the Chebyshev distance,
     * and Manhattan distance otherwise
     * @param a Point that will be compared with coordinates
     * @param endX X coordinate of comparand
     * @param endY Y coordinate of comparand
     * @return Distance between the two points, either Manhattan or Chebyshev distance
     */
    static int distance(final Point a, final int endX, final int endY) {
        int dx = Math.abs(endX - a.x);
        int dy = Math.abs(endY - a.y);

        if (diagonal) {
            return Math.max(dx, dy);
        } else {
            return dx + dy;
        }
    }

    /**
     * Checks if a given Point is valid on a given Grid.
     * i.e. not below minimum or above maximum dimensions
     * @param a Point to be checked
     * @param g The Grid the point will be checked against
     * @return Whether the Point is a valid location on the grid
     */
    static boolean valid(final Point a, final Grid g) {
        return (a.x >= 0 && a.x < g.nodes[0].length)
               && (a.y >= 0 && a.y < g.nodes.length)
               && (g.nodes[a.y][a.x] != Node.BLOCKED);
    }
}
