package com.jhursin.keiro.logic;

import com.jhursin.keiro.gui.MapWindow;
import com.jhursin.keiro.io.FileToImage;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
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
        System.out.println("A* length = " + length);

        return length;
    }
    
    public static int solveJPS(Grid grid, MapWindow mw, long delay) {
        delay *= 1000;
        
        // JumpPoints in this set will not be processed again
        HashMap<JumpPoint, JumpPoint> closed = new HashMap<>();
        
        // Contains all JumpPoints that need to be opened
        PriorityQueue<JumpPoint> nodes = new PriorityQueue<>();
        
        // Diagonal movement must be allowed for JPS
        diagonal = true;
        createDeltas();
        
        // Add JumpPoints into all 8 directions from the start
        for (Point d : deltas) {
            JumpPoint jp = new JumpPoint(grid.getStartX(), grid.getStartY(), d.x, d.y, 0, 0);
            nodes.add(jp);
            closed.put(jp, jp);
        }
        
        long time = System.nanoTime();
        while(!nodes.isEmpty()) {
            // If time elapsed since last operation is less than the delay, wait
            while(System.nanoTime() - time < delay);
            
            // Get the best priority JumpPoint from our PriorityQueue
            // The heuristic is the same as in A*, which is the
            // Chebyshev distance to the goal
            JumpPoint node = nodes.poll();
            
            if (node.x == grid.getEndX() && node.y == grid.getEndY()) {
                break;
            }
            
            // TODO Make a better color for JumpPoints
            if (mw != null) mw.setRGB(node.x, node.y, Node.DROPPED.getRGB());
            
            
            // System.out.println("Checking node (" + node.x + ", " + node.y + "), direction (" + node.dx + ", " + node.dy + ")");
            
            
            // Check which direction the latest node is going to based on
            // the delta values dx and dy, and call appropriate method
            ArrayList<JumpPoint> found_nodes;
            
            if (node.dx == 0) {
                found_nodes = search_v(nodes, closed, new Point(node.x, node.y), node.dy, 0, grid, mw, delay);
            } else if (node.dy == 0) {
                found_nodes = search_h(nodes, closed, new Point(node.x, node.y), node.dx, 0, grid, mw, delay);
            } else {
                found_nodes = search_d(nodes, closed, new Point(node.x, node.y), node.dx, node.dy, 0, grid, mw, delay);
            }
            
            // For all JumpPoints found during this JumpPoint's handling,
            // set this JumpPoint as their parent
            for (JumpPoint jp : found_nodes) {
                jp.setParent(node);
            }
            time = System.nanoTime();
        }
        
        JumpPoint goal = closed.get(new JumpPoint(grid.getEndX(), grid.getEndY(), 0, 0, 0, Integer.MAX_VALUE));
        if (goal == null) {
            // Goal wasn't found
            if (mw != null) mw.jpsHasRun = true;
            return 0;
        }
        
        System.out.println("xd");
        int length = 0;
        JumpPoint curr = goal;
        while(true) {
            JumpPoint next = curr.getParent();
            if (next == null) break;
            length += distance(curr.x, curr.y, next.x, next.y);
            if (mw != null) mw.drawPath(curr.x, curr.y, next.x, next.y);
            curr = next;
        }
        
        System.out.println("JPS length = " + (goal.dst + 1));
        System.out.println("JPS length 2 = " + (length + 1));
        
        // We found the goal
        if (mw != null) mw.jpsHasRun = true;
        return length + 1;
    }
    
    /**
     * A horizontal search for JumpPoints.
     * @param nodes Queue containing all open JumpPoints
     * @param closed HashSet containing closed JumpPoints
     * @param p The Point this search will start from
     * @param dx Horizontal delta (the direction we'll step), either 1 or -1
     * @param dst Currently unused
     * @param g Grid we are currently solving
     * @param mw MapWindow onto which our progress will be drawn
     * @param delay The minimum time between each operation in nanoseconds
     * @return All nodes that were found during this search
     */
    private static ArrayList<JumpPoint> search_h(PriorityQueue<JumpPoint> nodes, HashMap<JumpPoint, JumpPoint> closed, Point p, final int dx, int dst, Grid g, MapWindow mw, long delay) {
        int x0 = p.x;
        int y = p.y;
        
        // All JumpPoints we found during this search
        ArrayList<JumpPoint> found_nodes = new ArrayList<>();
        int newdst = dst;
        
        //System.out.println(String.format("Starting search_h from (%d, %d) with direction %d", x0, y, dx));
        while (true) {
            // Move one step to the given direction
            x0 += dx;
            newdst++;
            
            // If we encounter an obstacle or the end of the map, end this search
            if (!valid(x0, y, g)) {
                //System.out.println("Search_h found an obstacle, ending search\n");
                return found_nodes;
            }
            
            if (mw != null) mw.setRGB(x0, y, Node.QUEUE.getRGB());
            
            // If we find the goal, end this search
            if (x0 == g.getEndX() && y == g.getEndY()) {
                JumpPoint jp = new JumpPoint(x0, y, Integer.MIN_VALUE / 2, newdst);
                found_nodes.add(jp);
                nodes.add(jp);
                closed.put(jp, jp);
                // System.out.println("Search_h found the goal, ending search\n");
                return found_nodes;
            }
            
            // Take one more step to the given direction
            int x1 = x0 + dx;
            
            // Did we find a forced neighbor or not
            boolean found = false;
            
            int h = distance(x0, y, g.getEndX(), g.getEndY());
            
            // If there is an obstacle above us but no obstacle up and to the
            // left or right depending on our direction,
            // create a jump point from here to there
            if (!valid(x0, y + 1, g) && valid(x1, y + 1, g)) {
                JumpPoint jp = new JumpPoint(x0, y, dx, 1, h, newdst);
                JumpPoint find = closed.get(jp);
                if (find == null ? true : jp.dst < find.dst) {
                    closed.put(jp, jp);
                    nodes.add(jp);
                    found_nodes.add(jp);                    
                }
                
                //System.out.println("Search_h found forced neighbour above " + String.format("(%d, %d), direction %d", x0, y, dx));
                //System.out.println(String.format("(%d, %d), direction (%d, %d)", x0, y, dx, 1));
                found = true;
            }
            
            // Same as above, but this time we're checking below us
            if (!valid(x0, y - 1, g) && valid(x1, y - 1, g)) {
                JumpPoint jp = new JumpPoint(x0, y, dx, -1, h, newdst);
                JumpPoint find = closed.get(jp);
                if (find == null ? true : jp.dst < find.dst) {
                    closed.put(jp, jp);
                    nodes.add(jp);
                    found_nodes.add(jp);
                    
                    // System.out.println("Search_h found forced neighbour below " + String.format("(%d, %d), direction %d \n", x0, y, dx));
                }
                
            }
            
            // If we found a forced neighbor, add our 
            // current route as a jump point and return
            
            
            if (found) {
                
                JumpPoint jp = new JumpPoint(x0, y, dx, 0, 1, newdst);
                JumpPoint find = closed.get(jp);
                if (find == null ? true : jp.dst < find.dst) {
                    closed.put(jp, jp);
                    nodes.add(jp);
                    found_nodes.add(jp);
                    //System.out.println(String.format("Search_h adds self back to list and returns on (%d, %d) with direction %d\n", x0, y, dx));
                }
                return found_nodes;
            }
            
        }
    }
    
    /**
     * @see #search_h(java.util.PriorityQueue, java.util.HashSet, com.jhursin.keiro.logic.Point, int, int, com.jhursin.keiro.logic.Grid, com.jhursin.keiro.gui.MapWindow, long) 
     * @param nodes
     * @param closed
     * @param p
     * @param dy
     * @param dst
     * @param g
     * @param mw
     * @param delay
     * @return 
     */
    private static ArrayList<JumpPoint> search_v(PriorityQueue<JumpPoint> nodes, HashMap<JumpPoint, JumpPoint> closed, Point p, final int dy, int dst, Grid g, MapWindow mw, long delay) {
        // This is literally the exact same method as search_h, except vertically
        // please refer to its documentation
        int x = p.x;
        int y0 = p.y;
        
        // System.out.println(String.format("Starting search_v from (%d, %d) with direction %d", x, y0, dy));
        
        ArrayList<JumpPoint> found_nodes = new ArrayList<>();
        
        int newdst = dst;
        while (true) {
            // System.out.println("search_v y0 = " + y0);
            y0 += dy;
            newdst++;
            
            if (!valid(x, y0, g)) {
                //System.out.println("Search_v found obstacle, returning\n");
                return found_nodes;
            }
            
            if (mw != null) mw.setRGB(x, y0, Node.QUEUE.getRGB());
            
            if (x == g.getEndX() && y0 == g.getEndY()) {
                //System.out.println("Search_v found the goal, returning\n");
                JumpPoint jp = new JumpPoint(x, y0, Integer.MIN_VALUE / 2, newdst);
                found_nodes.add(jp);
                nodes.add(jp);
                closed.put(jp, jp);
                return found_nodes;
            }
            
            int y1 = y0 + dy;
            
            boolean found = false;
            
            int h = distance(x, y0, g.getEndX(), g.getEndY());
            
            if (!valid(x + 1, y0, g) && valid(x + 1, y1, g)) {
                JumpPoint jp = new JumpPoint(x, y0, 1, dy, h, newdst);
                JumpPoint find = closed.get(jp);
                if (find == null ? true : jp.dst < find.dst) {
                    closed.put(jp, jp);
                    nodes.add(jp);
                    found_nodes.add(jp);
                    //System.out.println(String.format("Search_v found forced neighbour to the right at (%d, %d) direction %d", x, y0, dy));
                    found = true;
                    
                }
            }
            if (!valid(x - 1, y0, g) && valid(x - 1, y1, g)) {
                JumpPoint jp = new JumpPoint(x, y0, -1, dy, h, newdst);
                JumpPoint find = closed.get(jp);
                if (find == null ? true : jp.dst < find.dst) {
                    closed.put(jp, jp);
                    nodes.add(jp);
                    found_nodes.add(jp);
                    // System.out.println(String.format("Search_v found forced neighbour to the left at (%d, %d) direction %d", x, y0, dy));
                    found = true;
                    
                }
            }
            
            
            if (found) {
                
                //System.out.println(String.format("Search_v ends search because forced neighbour was found, adding (%d, %d) with direction %d back to queue\n", x, y0, dy));
                JumpPoint jp = new JumpPoint(x, y0, 0, dy, 1, newdst);
                JumpPoint find = closed.get(jp);
                if (find == null ? true : jp.dst < find.dst) {
                    closed.put(jp, jp);
                    nodes.add(jp);
                    found_nodes.add(jp);
                }
                return found_nodes;
                
            }
            
            
        }
    }
    
    /**
     * A diagonal search for JumpPoints.
     * Spawns a horizontal and vertical search on every 
     * @param nodes Queue containing all open JumpPoints
     * @param closed HashSet containing closed JumpPoints
     * @param p The Point this search will start from
     * @param dx Horizontal delta (the direction we'll step), either 1 or -1
     * @param dst Currently unused
     * @param g Grid we are currently solving
     * @param mw MapWindow onto which our progress will be drawn
     * @param delay The minimum time between each operation in nanoseconds
     * @return All nodes that were found during this search
     */
    private static ArrayList<JumpPoint> search_d(PriorityQueue<JumpPoint> nodes, HashMap<JumpPoint, JumpPoint> closed, Point p, int dx, int dy, int dst, Grid g, MapWindow mw, long delay) {
        int x0 = p.x;
        int y0 = p.y;
        
        //System.out.println(String.format("Starting search_d from (%d, %d) with direction (%d, %d)", x0, y0, dx, dy));
        
        // All JumpPoints we found during this search
        ArrayList<JumpPoint> found_nodes = new ArrayList<>();
        
        // This point is one step into our given direction
        int x1 = p.x;
        int y1 = p.y;
        
        int newdst = dst;
        
        long time = System.nanoTime();
        
        while (true) {
            // Take one step to the given direction
            x1 += dx;
            y1 += dy;
            
            newdst++;
            
            
            // If we're on an obstacle or out of the map, this search is done.
            if (!valid(x1, y1, g)) {
                //System.out.println("Search_d was blocked, ending search\n");
                return found_nodes;
            }
            
            // If we found the end, this search is done.
            if (x1 == g.getEndX() && y1 == g.getEndY()) {
                JumpPoint jp = new JumpPoint(x1, y1, Integer.MIN_VALUE / 2, newdst);
                found_nodes.add(jp);
                nodes.add(jp);
                closed.put(jp, jp);
                return found_nodes;
            }
            
            if (mw != null) mw.setRGB(x1, y1, Node.QUEUE.getRGB());
            
            // This point is two steps into our given direction
            int x2 = x1 + dx;
            int y2 = y1 + dy;
            
            // Was a forced neighbor found or not
            boolean found = false;
            
            // Minimum distance to goal, used as our PriorityQueue's priority
            int h = distance(x0, y1, g.getEndX(), g.getEndY());
            
            // Check for a diagonal forced neighbor
            // For example if our direction is up and to the right, this checks
            // if there is a forced neighbor up and to the left of us or
            // down and to the right
            if (!valid(x0, y1, g) && valid(x0, y2, g)) {
                // Forced neighbor found, its direction will be the same vertically
                // but reversed horizontally
                JumpPoint jp = new JumpPoint(x1, y1, -dx, dy, h, newdst);
                JumpPoint find = closed.get(jp);
                if (find == null ? true : jp.dst < find.dst) {
                    closed.put(jp, jp);
                    nodes.add(jp);
                    found_nodes.add(jp);
                    
                    found = true;
                    // System.out.println("Search_d found forced neighbor from (" + x1 + ", " + y1 + "), direction (" + dx + ", " + (-dy) + ")");
                }
            }
            
            // See above but vice versa
            if (!valid(x1, y0, g) && valid(x2, y0, g)) {
                JumpPoint jp = new JumpPoint(x1, y1, dx, -dy, h, newdst);
                JumpPoint find = closed.get(jp);
                if (find == null ? true : jp.dst < find.dst) {
                    closed.put(jp, jp);
                    nodes.add(jp);
                    found_nodes.add(jp);
                    
                    found = true;
                    //System.out.println("Search_d found forced neighbor from (" + x1 + ", " + y1 + "), direction (" + dx + ", " + (-dy) + ")");
                }
            }
            
            if (found) {
                return found_nodes;
            }
            
            // Did we find any forced neighbors on our horizontal and vertical
            // search that we're about to do.
            boolean hv_done = false;
            
            
            // We'll also wait between these operations since otherwise it would
            // just blast through all diagonal scans
            while(System.nanoTime() - time < delay);
            time = System.nanoTime();
            
            // Check to see if we've done this already, if yes, skip
            JumpPoint h_jp = new JumpPoint(x1, y1, dx, 0, h, newdst);
            
            if (!closed.containsKey(h_jp)) {
                
                //System.out.println("Search_d calls search_h\n");
                ArrayList<JumpPoint> h_found = search_h(nodes, closed, new Point(x1, y1), dx, newdst, g, mw, delay);
                closed.put(h_jp, h_jp);
                
                // Make this the parent of all JumpPoints found during our search
                if (!h_found.isEmpty()) {
                    found_nodes.add(h_jp);
                    for (JumpPoint j : h_found) {
                        j.setParent(h_jp);
                    }
                    hv_done = true;
                }
            }
            
            // Same as above but vertically
            while(System.nanoTime() - time < delay);
            JumpPoint v_jp = new JumpPoint(x1, y1, 0, dy, h, newdst);
            if (!closed.containsKey(v_jp)) {

                //System.out.println("Search_d calls search_v\n");
                ArrayList<JumpPoint> v_found = search_v(nodes, closed, new Point(x1, y1), dy, newdst, g, mw, delay);
                closed.put(v_jp, v_jp);
                if (!v_found.isEmpty()) {
                    found_nodes.add(v_jp);
                    for (JumpPoint j : v_found) {
                        j.setParent(v_jp);
                    }
                    hv_done = true;
                }
            }
            
            // If our horizontal or vertical search found nodes, stop this search
            // for now and add this back to the queue to be opened later
            if (hv_done) {
                //System.out.println("Search_d ends");
                
                JumpPoint jp = new JumpPoint(x1, y1, dx, dy, 1, newdst);
                
                JumpPoint find = closed.get(jp);
                if (find == null ? true : jp.dst < find.dst) {
                    closed.put(jp, jp);
                    nodes.add(jp);
                    found_nodes.add(jp);
                    return found_nodes;
                }
            }
            // Move forward one step and repeat
            x0 = x1;
            y0 = y1;
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
     * Return the Chebyshev distance between two points
     * @param x X coordinate of point to be checked
     * @param y Y coordinate of point to be checked
     * @param endX X coordinate of point to be checked against
     * @param endY Y coordinate of point to be checked against
     * @return 
     */
    static int distance(final int x, final int y, final int endX, final int endY) {        
        int dx = Math.abs(endX - x);
        int dy = Math.abs(endY - y);
        
        return Math.max(dx, dy);
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

    
class JumpPoint implements Comparable<JumpPoint>{
    final int x;
    final int y;
    final int dx;
    final int dy;
    final int dst;
    private final int h;
    private JumpPoint parent;
    
    JumpPoint(int x, int y, int h, int dst) {
        this.x = x;
        this.y = y;
        this.h = h;
        this.dst = dst;
        this.dx = 0;
        this.dy = 0;
    }
    
    JumpPoint(int x, int y, int dx, int dy, int h, int dst) {
        this.x = x;
        this.y = y;
        this.dx = dx;
        this.dy = dy;
        this.h = h;
        this.dst = dst;
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
    
    @Override
    public int hashCode() {
        return Objects.hash(x, y, dx, dy);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final JumpPoint other = (JumpPoint) obj;
        if (this.x != other.x) {
            return false;
        }
        if (this.y != other.y) {
            return false;
        }
        if (this.dx != other.dx) {
            return false;
        }
        if (this.dy != other.dy) {
            return false;
        }
        return true;
    }
    
    @Override
    public int compareTo(JumpPoint other) {
        return this.h - other.h;
    }
}