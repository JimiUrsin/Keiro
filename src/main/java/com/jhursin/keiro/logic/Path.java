package com.jhursin.keiro.logic;

import com.jhursin.keiro.gui.MapWindow;
import com.jhursin.keiro.io.FileToImage;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;
import java.util.PriorityQueue;
import java.util.Set;
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
     * @param delay How long to wait between each operation in nanoseconds
     * @return Length of the best path
     */
    public static int solveAStar(final Grid grid, MapWindow mw, long delay) {
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
            
            if (draw) {
                // Set delay based on path length such that path drawing always takes 3 seconds
                long pathDelay = 3000 / path.size();

                while(!path.empty()) {
                    curr = path.pop();
                    mw.setRGB(curr.x, curr.y, Node.PATH.getRGB());

                    // If dimensions exceed 400, we probably need to fill all
                    // surrounding pixels as well for visibility
                    if (grid.nodes.length > 400 && grid.nodes[0].length > 400) {
                        for(Point d : deltas) {                        
                            mw.setRGB(curr.x + d.x, curr.y + d.y, Node.PATH.getRGB());
                        }
                    }
                    try {
                        Thread.sleep(pathDelay);
                    } catch (Exception e) {

                    }
                }
            }
        }
        System.out.println("Operations = " + operations);

        return length;
    }
    
    public static int solveJPS(Grid grid, MapWindow mw, long delay) {
        ArrayList<JumpPoint> nodes = new ArrayList<>();
        int startX = grid.getStartX();
        int startY = grid.getStartY();
        
        diagonal = true;
        createDeltas();
        
        JumpPoint start = new JumpPoint(startX, startY, 0);
        
        for (Point d : deltas) {
            nodes.add(new JumpPoint(startX, startY, d.x, d.y, 0));
        }
        
        while(!nodes.isEmpty()) {
            System.out.println(nodes.size());
            JumpPoint node = nodes.remove(nodes.size() - 1);
            System.out.println("Checking node (" + node.x + ", " + node.y + "), direction (" + node.dx + ", " + node.dy + ")");
            if (node.x == grid.getEndX() && node.y == grid.getEndY()) {
                System.out.println("Goal was found");
                int length = 0;
                int currx = node.x;
                int curry = node.y;
                while(node.getParent() != null) {
                    System.out.println("(" + node.x + ", " + node.y + ")");
                    node = node.getParent();
                    length += distance(new Point(currx, curry), node.x, node.y);
                    currx = node.x;
                    curry = node.y;
                }
                System.out.println("(" + node.x + ", " + node.y + ")");
                
                return length + 1;
            }
            ArrayList<JumpPoint> found_nodes;
            if (node.dx == 0) {
                found_nodes = search_v(nodes, new Point(node.x, node.y), node.dy, 0, grid);
            } else if (node.dy == 0) {
                found_nodes = search_h(nodes, new Point(node.x, node.y), node.dx, 0, grid);
            } else {
                found_nodes = search_d(nodes, new Point(node.x, node.y), node.dx, node.dy, 0, grid);
            }
            for (JumpPoint jp : found_nodes) {
                jp.setParent(node);
            }
        }
        
        return 0;
    }
    
    private static ArrayList<JumpPoint> search_h(ArrayList<JumpPoint> nodes, Point p, int dx, int dst, Grid g) {
        int x0 = p.x;
        int y = p.y;
        
        ArrayList<JumpPoint> found_nodes = new ArrayList<>();
        
        while (true) {
            x0 += dx;
            System.out.println("x0 = " + x0);
            
            if (!valid(x0, y, g)) {
                return found_nodes;
            }
            
            if (x0 == g.getEndX() && y == g.getEndY()) {
                JumpPoint jp = new JumpPoint(x0, y, dst + 1);
                nodes.add(jp);
                found_nodes.add(jp);
                System.out.println("Adding to list 8");
                return found_nodes;
            }
            
            int x1 = x0 + dx;
            
            boolean found = false;
            
            if (!valid(x0, y + 1, g) && valid(x1, y + 1, g)) {
                JumpPoint jp = new JumpPoint(x0, y, dx, 1, dst + 1);
                nodes.add(jp);
                found_nodes.add(jp);
                System.out.println("Adding to list 6");
                System.out.println(String.format("(%d, %d), direction (%d, %d)", x0, y, dx, 1));
                found = true;
            }
            if (!valid(x0, y - 1, g) && valid(x0, y - 1, g)) {
                JumpPoint jp = new JumpPoint(x0, y, dx, -1, dst + 1);
                nodes.add(jp);
                found_nodes.add(jp);
                System.out.println("Adding to list 5");
                System.out.println(String.format("(%d, %d), direction (%d, %d)", x0, y, dx, -1));
                found = true;
            }
            
            if (found) {
                JumpPoint jp = new JumpPoint(x0, y, dx, 0, dst + 1);
                nodes.add(jp);
                found_nodes.add(jp);
                System.out.println("Adding to list 7");
                return found_nodes;
            }
        }
    }
    
    private static ArrayList<JumpPoint> search_v(ArrayList<JumpPoint> nodes, Point p, int dy, int dst, Grid g) {
        int x = p.x;
        int y0 = p.y;
        
        ArrayList<JumpPoint> found_nodes = new ArrayList<>();
        
        while (true) {
            System.out.println("search_v y0 = " + y0);
            y0 += dy;
            
            if (!valid(x, y0, g)) {
                return found_nodes;
            }
            
            if (x == g.getEndX() && y0 == g.getEndY()) {
                JumpPoint jp = new JumpPoint(x, y0, dst + 1);
                nodes.add(jp);
                found_nodes.add(jp);
                return found_nodes;
            }
            
            int y1 = y0 + dy;
            
            boolean found = false;
            
            if (!valid(x + 1, y0, g) && valid(x + 1, y1, g)) {
                JumpPoint jp = new JumpPoint(x, y0, +1, dy, dst + 1);
                nodes.add(jp);
                found_nodes.add(jp);
                System.out.println("Adding to list 3");
                found = true;
            }
            if (!valid(x - 1, y0, g) && valid(x - 1, y1, g)) {
                JumpPoint jp = new JumpPoint(x, y0, -1, dy, dst + 1);
                nodes.add(jp);
                found_nodes.add(jp);
                System.out.println("Adding to list 4");
                found = true;
            }
            
            if (found) {
                JumpPoint jp = new JumpPoint(x, y0, 0, dy, dst + 1);
                nodes.add(jp);
                found_nodes.add(jp);
                return found_nodes;
            }
        }
    }
    
    private static ArrayList<JumpPoint> search_d(ArrayList<JumpPoint> nodes, Point p, int dx, int dy, int dst, Grid g) {
        int x0 = p.x;
        int y0 = p.y;
        
        ArrayList<JumpPoint> found_nodes = new ArrayList<>();
        
        int x1 = p.x;
        int y1 = p.y;
        while (true) {
            x1 += dx;
            y1 += dy;
            
            System.out.println("Search_d x1 = " + x1);
            System.out.println("Search_d y1 = " + y1);
            System.out.println();
            
            if (!valid(x1, y1, g)) {
                System.out.println("Invalid location, ending search\n");
                return found_nodes;
            }
            
            if (x1 == g.getEndX() && y1 == g.getEndY()) {
                JumpPoint jp = new JumpPoint(x1, y1, dst + 1);
                found_nodes.add(jp);
                nodes.add(jp);
            }
            
            int x2 = x1 + dx;
            int y2 = y1 + dy;
            
            boolean found = false;
            
            if (!valid(x0, y1, g) && valid(x0, y2, g)) {
                JumpPoint parent = new JumpPoint(x1, y1, dst);
                JumpPoint jp = new JumpPoint(x1, y1, -dx, dy, dst + 1);
                System.out.println("Adding to list 1");
                found_nodes.add(parent);
                jp.setParent(parent);
                nodes.add(jp);
                
                found = true;
            }
            if (!valid(x1, y0, g) && valid(x2, y0, g)) {
                JumpPoint jp = new JumpPoint(x1, y1, dx, -dy, dst + 1);
                System.out.println("Adding to list 2, direction (" + dx + ", " + (-dy) + ")");
                found_nodes.add(jp);
                nodes.add(jp);
                found = true;
            }
            
            boolean h = false;
            boolean v = false;
            
            System.out.println("Search_d calls search_h");
            ArrayList<JumpPoint> h_found = search_h(nodes, new Point(x1, y1), dx, dst + 1, g);
            JumpPoint h_jp = new JumpPoint(x1, y1, dx, 0, dst + 1);
            if (!h_found.isEmpty()) {
                h = true;
                found_nodes.add(h_jp);    
            }
            for (JumpPoint j : h_found) {
                j.setParent(h_jp);
            }

            System.out.println("Search_d calls search_v");
            ArrayList<JumpPoint> v_found = search_v(nodes, new Point(x1, y1), dy, dst + 1, g);
            JumpPoint v_jp = new JumpPoint(x1, y1, 0, dy, dst + 1);
            if (!v_found.isEmpty()) {
                v = true;
                found_nodes.add(v_jp);
            }
            for (JumpPoint j : v_found) {
                j.setParent(v_jp);
            }
            if (h || v) {
                System.out.println("Found jump points in h or v, ending search");
                JumpPoint jp = new JumpPoint(x1, y1, dx, dy, dst + 1);
                found_nodes.add(jp);
                nodes.add(jp);
                return found_nodes;
            }
        }
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
    
    /**
     * @see #valid(com.jhursin.keiro.logic.Point, com.jhursin.keiro.logic.Grid)
     * @param x X coordinate of point to be checked
     * @param y Y coordinate of point to be checked
     * @param g The Grid the point will be checked against
     * @return Whether the Point is a valid location on the grid
     */
    static boolean valid(final int x, final int y, final Grid g) {
        return (x >= 0 && x < g.nodes[0].length)
               && (y >= 0 && y < g.nodes.length)
               && (g.nodes[y][x] != Node.BLOCKED);
    }
}
    
class JumpPoint {
    final int x;
    final int y;
    final int dx;
    final int dy;
    private final int cost;
    private JumpPoint parent;
    
    JumpPoint(int x, int y, int newcost) {
        this.x = x;
        this.y = y;
        this.cost = newcost;
        this.dx = 0;
        this.dy = 0;
    }
    
    JumpPoint(int x, int y, int dx, int dy, int cost) {        
        this.x = x;
        this.y = y;
        this.dx = dx;
        this.dy = dy;
        this.cost = cost;
    }
    
    JumpPoint(int x, int y, int dx, int dy, int cost, JumpPoint parent) {
        this.x = x;
        this.y = y;
        this.dx = dx;
        this.dy = dy;
        this.cost = cost;
        this.parent = parent;
    }
    
    JumpPoint(int x, int y, int cost, JumpPoint parent) {
        this.x = x;
        this.y = y;
        this.dx = 0;
        this.dy = 0;
        this.cost = cost;
        this.parent = parent;
    }
    
    int getX() {
        return this.x;
    }
    
    int getY() {
        return this.y;
    }
    
    void setParent(JumpPoint parent) {
        this.parent = parent;
    }
    
    JumpPoint getParent() {
        return this.parent;
    }
}