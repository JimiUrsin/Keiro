package com.jhursin.keiro.logic;

import com.jhursin.keiro.gui.MapWindow;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.PriorityQueue;
import java.util.Stack;

/**
 * Contains pathfinding algorithms.
 */
public final class Path {

    /**
     * Prevent instantiation.
     */
    private Path() { }

    /**
     * Contains all directions.
     * First non-diagonals starting from up moving clockwise,
     * then diagonals starting from up and right moving clockwise
     */
    private static final Point[] DELTAS = {
        // CHECKSTYLE:OFF
        new Point( 0, -1),
        new Point(+1,  0),
        new Point( 0, +1),
        new Point(-1,  0),
        new Point(+1, -1),
        new Point(+1, +1),
        new Point(-1, +1),
        new Point(-1, -1)
        // CHECKSTYLE:ON
    };

    /**
     * Cost of diagonal movement.
     */
    private static final double DIAG_COST = Math.sqrt(2.0);

    /**
     * Solves a grid with the A* algorithm, then draws its path on the grid
     * and onto the given MapWindow.
     * @param grid Grid to be solved
     * @param mw MapWindow for drawing
     * @param delay How long to wait between each operation in nanoseconds
     * @return Length of the best path
     */
    public static double solveAStar(final Grid grid, final MapWindow mw, final long delay) {
        boolean draw = true;

        if (mw == null) {
            draw = false;
        }

        // Open nodes
        PriorityQueue<Point> open = new PriorityQueue<>();

        // Add the starting point to the open queue
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
        HashMap<Point, Double> gScore = new HashMap<>();
        gScore.put(start, 0D);

        // F score = G score + estimated cost to goal
        HashMap<Point, Double> fScore = new HashMap<>();
        fScore.put(start, distance(start, grid.getEndX(), grid.getEndY()));

        long time = System.nanoTime();

        while (!open.isEmpty()) {
            // CHECKSTYLE:OFF
            while (System.nanoTime() - time < delay) { };
            // CHECKSTYLE:ON
            time = System.nanoTime();

            // Get the Point with the highest priority
            Point curr = open.poll();

            // If polled Point was the goal, we are done
            if (curr.x == grid.getEndX() && curr.y == grid.getEndY()) {
                break;
            }

            open.remove(curr);

            if (draw) {
                mw.setRGB(curr.x, curr.y, Node.DROPPED.getRGB());
            }

            // Go through all neighbors
            for (int i = 0; i < DELTAS.length; i++) {
                Point d = DELTAS[i];
                Point next = new Point(curr.x + d.x, curr.y + d.y);
                // If the neighbor is a blocked point or off the grid, just check the next one
                if (!valid(next, grid)) {
                    continue;
                }

                // Cost to this neighbor will be the best known cost of previous + 1
                // or the diagonal cost if we're moving diagonally
                double tempG = gScore.get(curr);
                if (i >= 4) {
                    tempG += DIAG_COST;
                } else {
                    tempG++;
                }

                // If G score through current route is lower, we have found a better route
                if (tempG < gScore.getOrDefault(next, Double.MAX_VALUE)) {
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

        double length;
        Point goal = new Point(grid.getEndX(), grid.getEndY());

        if (cameFrom.get(goal) == null) {
            System.out.println("A* did not find the goal");
            return 0;
        } else {
            length = gScore.get(goal);

            if (draw) {
                // Start from the end
                Point curr = goal;

                Stack<Point> path = new Stack<>();

                // Make our way back to the start
                while (curr != start) {
                    grid.nodes[curr.y][curr.x] = Node.PATH;
                    path.add(curr);

                    curr = cameFrom.get(curr);
                }

                // Set delay based on path length such that path drawing always takes 3 seconds
                long pathDelay = 3_000_000_000L / path.size();

                long pathTime = System.nanoTime();
                while (!path.empty()) {
                    // CHECKSTYLE:OFF
                    while (System.nanoTime() - pathTime < pathDelay) { };
                    // CHECKSTYLE:ON
                    pathTime = System.nanoTime();
                    curr = path.pop();
                    mw.setRGB(curr.x, curr.y, Node.PATH.getRGB());

                    // If dimensions exceed 500, we probably need to fill all
                    // surrounding pixels as well for visibility
                    if (Math.min(grid.nodes.length, grid.nodes[0].length) > 500) {
                        for (Point d : DELTAS) {
                            mw.setRGB(curr.x + d.x, curr.y + d.y, Node.PATH.getRGB());
                        }
                    }
                }
            }
        }

        System.out.println("A* length = " + length);

        return length;
    }

    /**
     * Solves a Grid using the Jump Point Search algorithm.
     * @param grid Grid to be solved
     * @param mw MapWindow for drawing
     * @param delay Delay between each operation in nanoseconds
     * @return Length of the best path
     */
    public static double solveJPS(final Grid grid, final MapWindow mw, final long delay) {
        // Nanoseconds -> microseconds
        long delayMs = delay * 1000;

        // JumpPoints in this set will not be processed again
        HashMap<JumpPoint, JumpPoint> closed = new HashMap<>();

        // Contains all JumpPoints that need to be opened
        PriorityQueue<JumpPoint> nodes = new PriorityQueue<>();

        // Add JumpPoints into all 8 directions from the start
        for (Point d : DELTAS) {
            JumpPoint jp = new JumpPoint(grid.getStartX(), grid.getStartY(), d.x, d.y, 0, 0);
            nodes.add(jp);
            closed.put(jp, jp);
        }

        long time = System.nanoTime();

        while (!nodes.isEmpty()) {
            // If time elapsed since last operation is less than the delay, wait
            // CHECKSTYLE:OFF
            while (System.nanoTime() - time < delayMs) { };
            // CHECKSTYLE:ON

            // Get the best priority JumpPoint from our PriorityQueue
            // The heuristic is the same as in A* with diagonal movement allowed,
            // which is the Chebyshev distance to the goal
            JumpPoint node = nodes.poll();

            if (node.x == grid.getEndX() && node.y == grid.getEndY()) {
                break;
            }

            ArrayList<JumpPoint> foundNodes;


            Point p = new Point(node.x, node.y);

            // Check which direction the latest node is going to based on
            // the movement delta values dx and dy, and call appropriate method
            if (node.dx == 0) {
                foundNodes = searchV(nodes, closed, p, node.dy, node.dst, grid, mw);
            } else if (node.dy == 0) {
                foundNodes = searchH(nodes, closed, p, node.dx, node.dst, grid, mw);
            } else {
                foundNodes = searchD(nodes, closed, p, node.dx, node.dy, node.dst, grid, mw, delayMs);
            }

            // For all JumpPoints found during this JumpPoint's handling,
            // set this JumpPoint as their parent
            for (JumpPoint jp : foundNodes) {
                jp.setParent(node);
            }

            time = System.nanoTime();
        }

        JumpPoint goal = closed.get(new JumpPoint(grid.getEndX(), grid.getEndY(), 0, 0, 0, Integer.MAX_VALUE));

        if (goal == null) {
            // Goal wasn't found
            System.out.println("JPS didn't find the goal");
            if (mw != null) {
                mw.jpsHasRun = true;
            }
            return 0;
        }

        // We found the goal
        double length = goal.dst;

        if (mw != null) {
            // We're moving from the goal to the start, so we need a LIFO data
            // structure to reverse with no fuss
            Stack<Point> s = new Stack<>();

            // Create our path by jumping from parent to parent
            while (goal != null) {
                s.add(new Point(goal.x, goal.y));
                goal = goal.getParent();
            }

            Point curr = s.pop();

            long pathDelay = 3_000_000_000L / s.size();

            long pathTime = System.nanoTime();

            while (!s.empty()) {
                // CHECKSTYLE:OFF
                while (System.nanoTime() - pathTime < pathDelay) { };
                // CHECKSTYLE:ON

                pathTime = System.nanoTime();

                Point next = s.pop();

                mw.drawPath(curr.x, curr.y, next.x, next.y);

                curr = next;
            }
        }

        System.out.println("JPS length = " + length);
        if (mw != null) {
            mw.jpsHasRun = true;
        }
        return length;
    }

    /**
     * A horizontal search for JumpPoints.
     * @param nodes Queue containing all open JumpPoints
     * @param closed HashSet containing closed JumpPoints
     * @param p The Point this search will start from
     * @param dx Horizontal delta (the direction we'll step), either 1 or -1
     * @param dst Distance we've travelled so far
     * @param g Grid we are currently solving
     * @param mw MapWindow onto which our progress will be drawn
     * @return All nodes that were found during this search
     */
    static ArrayList<JumpPoint> searchH(final PriorityQueue<JumpPoint> nodes, final HashMap<JumpPoint, JumpPoint> closed, final Point p, final int dx, final double dst, final Grid g, final MapWindow mw) {
        int x0 = p.x;
        int y = p.y;

        double newDst = dst;

        // All JumpPoints we found during this search
        ArrayList<JumpPoint> foundNodes = new ArrayList<>();

        while (true) {
            // Move one step to the given direction
            x0 += dx;
            newDst++;

            // If we encounter an obstacle or the end of the map, end this search
            if (!valid(x0, y, g)) {
                return foundNodes;
            }

            // Show the user we're currently processing this area
            if (mw != null) {
                mw.setTemp(x0, y);
            }

            // If we find the goal, end this search
            if (x0 == g.getEndX() && y == g.getEndY()) {
                JumpPoint jp = new JumpPoint(x0, y, Double.MIN_VALUE / 2, newDst);
                foundNodes.add(jp);
                nodes.add(jp);
                closed.put(jp, jp);
                return foundNodes;
            }

            // Take one more step to the given direction
            int x1 = x0 + dx;

            // Did we find a forced neighbor or not
            boolean found = false;

            double h = distance(x0, y, g.getEndX(), g.getEndY());

            // If there is an obstacle below us but no obstacle up and to the
            // left or right depending on our direction,
            // create a jump point from here to there
            if (!valid(x0, y + 1, g) && valid(x1, y + 1, g)) {
                JumpPoint jp = new JumpPoint(x0, y, dx, 1, h, newDst);
                JumpPoint find = closed.get(jp);

                // You will see the following couple lines quite a lot, so read this
                // If we find a potential JumpPoint, check to see if the same
                // JumpPoint can be found in the closed list. If yes, we will
                // replace it if our current distance travelled is less than
                // the JumpPoint that was found earlier and add it back
                // to the open queue. I am aware that this could be accomplished
                // with just replacing all of the old nodes' children's parent
                // with the better one but this is the best I've got right now
                if (find == null ? true : jp.dst < find.dst) {
                    closed.put(jp, jp);
                    nodes.add(jp);
                    foundNodes.add(jp);
                    found = true;
                }
            }

            // Same as the last one, but this time check above
            if (!valid(x0, y - 1, g) && valid(x1, y - 1, g)) {
                JumpPoint jp = new JumpPoint(x0, y, dx, -1, h, newDst);
                JumpPoint find = closed.get(jp);
                if (find == null ? true : jp.dst < find.dst) {
                    closed.put(jp, jp);
                    nodes.add(jp);
                    foundNodes.add(jp);
                    found = true;
                }
            }

            // If we found a forced neighbor, add our current route as a jump point and return
            if (found) {
                JumpPoint jp = new JumpPoint(x0, y, dx, 0, h, newDst);
                JumpPoint find = closed.get(jp);
                if (find == null ? true : jp.dst < find.dst) {
                    closed.put(jp, jp);
                    nodes.add(jp);
                    foundNodes.add(jp);
                }
                return foundNodes;
            }
        }
    }

    /**
     * A vertical search for JumpPoints.
     * @param nodes Queue containing all open JumpPoints
     * @param closed HashSet containing closed JumpPoints
     * @param p The Point this search will start from
     * @param dy Vertical delta (the direction we'll step), either 1 or -1
     * @param dst Distance we've travelled so far
     * @param g Grid we are currently solving
     * @param mw MapWindow onto which our progress will be drawn
     * @return All nodes that were found during this search
     */
    static ArrayList<JumpPoint> searchV(final PriorityQueue<JumpPoint> nodes, final HashMap<JumpPoint, JumpPoint> closed, final Point p, final int dy, final double dst, final Grid g, final MapWindow mw) {
        // This is literally the exact same method as searchH, except vertically
        // please refer to its documentation
        // This is done for speed and readability

        int x = p.x;
        int y0 = p.y;

        double newDst = dst;

        ArrayList<JumpPoint> foundNodes = new ArrayList<>();

        while (true) {
            y0 += dy;
            newDst++;

            if (!valid(x, y0, g)) {
                return foundNodes;
            }

            if (mw != null) {
                mw.setTemp(x, y0);
            }

            if (x == g.getEndX() && y0 == g.getEndY()) {
                JumpPoint jp = new JumpPoint(x, y0, Double.MIN_VALUE / 2, newDst);
                foundNodes.add(jp);
                nodes.add(jp);
                closed.put(jp, jp);
                return foundNodes;
            }

            int y1 = y0 + dy;

            boolean found = false;

            double h = distance(x, y0, g.getEndX(), g.getEndY());

            if (!valid(x + 1, y0, g) && valid(x + 1, y1, g)) {
                JumpPoint jp = new JumpPoint(x, y0, 1, dy, h, newDst);
                JumpPoint find = closed.get(jp);
                if (find == null ? true : jp.dst < find.dst) {
                    closed.put(jp, jp);
                    nodes.add(jp);
                    foundNodes.add(jp);
                    found = true;

                }
            }
            if (!valid(x - 1, y0, g) && valid(x - 1, y1, g)) {
                JumpPoint jp = new JumpPoint(x, y0, -1, dy, h, newDst);
                JumpPoint find = closed.get(jp);
                if (find == null ? true : jp.dst < find.dst) {
                    closed.put(jp, jp);
                    nodes.add(jp);
                    foundNodes.add(jp);
                    found = true;

                }
            }

            if (found) {
                JumpPoint jp = new JumpPoint(x, y0, 0, dy, h, newDst);
                JumpPoint find = closed.get(jp);
                if (find == null ? true : jp.dst < find.dst) {
                    closed.put(jp, jp);
                    nodes.add(jp);
                    foundNodes.add(jp);
                }
                return foundNodes;
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
     * @param dy Vertical delta (the direction we'll step), either 1 or -1
     * @param dst Distance we've travelled so far
     * @param g Grid we are currently solving
     * @param mw MapWindow onto which our progress will be drawn
     * @param delay The minimum time between each operation in nanoseconds
     * @return All nodes that were found during this search
     */
    static ArrayList<JumpPoint> searchD(final PriorityQueue<JumpPoint> nodes, final HashMap<JumpPoint, JumpPoint> closed, final Point p, final int dx, final int dy, final double dst, final Grid g, final MapWindow mw, final long delay) {
        int x0 = p.x;
        int y0 = p.y;

        // All JumpPoints we found during this search
        ArrayList<JumpPoint> foundNodes = new ArrayList<>();

        // This point is one step into our given direction
        int x1 = p.x;
        int y1 = p.y;

        double newDst = dst;

        long time = System.nanoTime();

        while (true) {
            // Take one step to the given direction
            x1 += dx;
            y1 += dy;

            newDst += DIAG_COST;

            // If we're on an obstacle or out of the map, this search is done.
            if (!valid(x1, y1, g)) {
                if (mw != null) {
                    mw.flushTemp();
                }
                return foundNodes;
            }

            // If we found the end, this search is done.
            if (x1 == g.getEndX() && y1 == g.getEndY()) {
                JumpPoint jp = new JumpPoint(x1, y1, Double.MIN_VALUE / 2, newDst);
                foundNodes.add(jp);
                nodes.add(jp);
                closed.put(jp, jp);
                return foundNodes;
            }

            if (mw != null) {
                mw.setTemp(x1, y1);
            }

            // This point is two steps into our given direction
            int x2 = x1 + dx;
            int y2 = y1 + dy;

            // Was a forced neighbor found or not
            boolean found = false;

            // Minimum distance to goal, used as our PriorityQueue's priority
            double h = distance(x0, y1, g.getEndX(), g.getEndY());

            // Check for a diagonal forced neighbor
            // For example if our direction is up and to the right, this checks
            // if there is a forced neighbor up and to the left of us or
            // down and to the right
            if (!valid(x0, y1, g) && valid(x0, y2, g)) {
                // Forced neighbor found, its direction will be the same vertically
                // but reversed horizontally
                JumpPoint jp = new JumpPoint(x1, y1, -dx, dy, h, newDst);
                JumpPoint find = closed.get(jp);
                if (find == null ? true : jp.dst < find.dst) {
                    closed.put(jp, jp);
                    nodes.add(jp);
                    foundNodes.add(jp);

                    found = true;
                }
            }

            // See above but vice versa
            if (!valid(x1, y0, g) && valid(x2, y0, g)) {
                JumpPoint jp = new JumpPoint(x1, y1, dx, -dy, h, newDst);
                JumpPoint find = closed.get(jp);
                if (find == null ? true : jp.dst < find.dst) {
                    closed.put(jp, jp);
                    nodes.add(jp);
                    foundNodes.add(jp);

                    found = true;
                }
            }

            // Did we find any forced neighbors on our horizontal and vertical
            // search that we're about to do.
            boolean hDone = false;
            boolean vDone = false;


            // We'll also wait between these operations since otherwise it would
            // just blast through all diagonal scans
            // CHECKSTYLE:OFF
            while (System.nanoTime() - time < delay) { };
            // CHECKSTYLE:ON
            time = System.nanoTime();

            // Check to see if we've done this already, if yes, skip
            JumpPoint jpH = new JumpPoint(x1, y1, dx, 0, h, newDst);
            JumpPoint findH = closed.get(jpH);

            if (findH == null ? true : jpH.dst < findH.dst) {
                ArrayList<JumpPoint> foundH = searchH(nodes, closed, new Point(x1, y1), dx, newDst, g, mw);
                closed.put(jpH, jpH);

                // Make this the parent of all JumpPoints found during our search
                if (!foundH.isEmpty()) {
                    foundNodes.add(jpH);
                    for (JumpPoint j : foundH) {
                        j.setParent(jpH);
                    }
                    hDone = true;
                }
            }

            // Same as above but vertically
            // CHECKSTYLE:OFF
            while (System.nanoTime() - time < delay) { };
            // CHECKSTYLE:ON
            JumpPoint jpV = new JumpPoint(x1, y1, 0, dy, h, newDst);
            JumpPoint findV = closed.get(jpV);
            if (findV == null ? true : jpV.dst < findV.dst) {
                ArrayList<JumpPoint> foundV = searchV(nodes, closed, new Point(x1, y1), dy, newDst, g, mw);
                closed.put(jpV, jpV);
                if (!foundV.isEmpty()) {
                    foundNodes.add(jpV);
                    for (JumpPoint j : foundV) {
                        j.setParent(jpV);
                    }
                    vDone = true;
                }
            }

            // If our horizontal or vertical search found nodes, stop this search
            // for now and add this back to the queue to be opened later
            if (hDone || vDone) {
                JumpPoint jp = new JumpPoint(x1, y1, dx, dy, 1, newDst);

                JumpPoint find = closed.get(jp);
                if (find == null ? true : jp.dst < find.dst) {
                    closed.put(jp, jp);
                    nodes.add(jp);
                    foundNodes.add(jp);
                    if (mw != null) {
                        mw.flushTemp();
                    }
                    return foundNodes;
                }
            }

            // Move forward one step and repeat
            x0 = x1;
            y0 = y1;
        }
    }

    /**
     * Returns the Euclidean distance between a point and an (x, y) coordinate.
     * @param a Point whose coordinates will be compared
     * @param endX X coordinate of comparand
     * @param endY Y coordinate of comparand
     * @return Distance between the two points
     */
    static double distance(final Point a, final int endX, final int endY) {
        double dx = Math.abs(endX - a.x);
        double dy = Math.abs(endY - a.y);

        return Math.sqrt(dx * dx + dy * dy);
    }

    /**
     * Return the Euclidean distance between two points.
     * @param x X of comparand 1
     * @param y Y of comparand 1
     * @param endX X coordinate of point to be checked against
     * @param endY Y coordinate of point to be checked against
     * @return Distance between the two points
     */
    static double distance(final int x, final int y, final int endX, final int endY) {
        double dx = Math.abs(endX - x);
        double dy = Math.abs(endY - y);

        return Math.sqrt(dx * dx + dy * dy);
    }

    /**
     * Checks if a given Point is valid on a given Grid.
     * i.e. not below minimum or above maximum dimensions or blocked by an obstacle
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
    public static boolean valid(final int x, final int y, final Grid g) {
        return (y >= 0 && y < g.nodes.length)
                && (x >= 0 && x < g.nodes[0].length)
                && (g.nodes[y][x] != Node.BLOCKED);
    }
}
