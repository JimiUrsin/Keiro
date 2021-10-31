package com.jhursin.keiro.logic;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.PriorityQueue;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import org.junit.Before;
import org.junit.Test;

public class PathTest {

    /**
     * Make and grid full of empty nodes with a start node in the upper left corner
     * and an end node in the bottom right
     * @param width Width of grid to be created
     * @param height Height of grid to be greated
     * @return The empty grid that was created
     */
    private Grid makeEmptyGrid(int width, int height) {
        Grid g = new Grid(width, height);
        for(int y = 0; y < height; y++) {
            for(int x = 0; x < width; x++) {
                g.nodes[y][x] = Node.EMPTY;
            }
        }
        g.setStart(0, 0);
        g.setEnd(width - 1, height - 1);

        return g;
    }

    private void addObstacles(Grid g) {
        for(int y = 1; y < 9; y++) {
            g.nodes[y][1] = Node.BLOCKED;
        }
        for(int x = 1; x < 9; x++) {
            g.nodes[1][x] = Node.BLOCKED;
        }
    }

    Grid g;

    @Before
    public void setUp() {
        g = makeEmptyGrid(10, 10);
    }

    @Test
    public void testAStarDiagonal() {
        double length = Path.solveAStar(g, null, 0);
        assertEquals("Path was not shortest possible", length, Math.sqrt(2) * 9d, 0.1);
    }

    @Test
    public void testAStarWithObstacles() {
        addObstacles(g);
        double length = Path.solveAStar(g, null, 0);

        assertEquals("Path was not shortest possible", 16 + Math.sqrt(2), length, 0.1);
    }

    @Test
    public void testJPS() {
        double length = Path.solveJPS(g, null, 0);
        assertEquals("Path was not shortest possible", Math.sqrt(2) * 9d, length, 0.1);
    }

    @Test
    public void testJPSWithObstacles() {
        addObstacles(g);
        double length = Path.solveJPS(g, null, 0);

        assertEquals("Path was not shortest possible", 16 + Math.sqrt(2), length, 0.1);
    }

    @Test
    public void testVerticalSearchFindsAllJumpPoints() {
        addObstacles(g);

        // A forced neighbor moving down and right should be found
        // and it should also add itself back as a JumpPoint
        JumpPoint downRight = new JumpPoint(0, 8, 1, 1, 0, 0);
        JumpPoint down = new JumpPoint(0, 8, 0, 1, 0, 0);

        final PriorityQueue<JumpPoint> nodes = new PriorityQueue<>();
        final HashMap<JumpPoint, JumpPoint> closed = new HashMap<>();
        final Point p = new Point(0, 0);

        ArrayList<JumpPoint> foundNodes = Path.searchV(nodes, closed, p, 1, 0, g, null);
        assertEquals("An incorrect amount of JumpPoints were found during vertical search", 2, foundNodes.size());
        assertTrue("A JumpPoint moving down and right was not present in foundNodes", foundNodes.contains(downRight));
        assertTrue("Vertical search did not correctly add itself to foundNodes", foundNodes.contains(down));
    }

    @Test
    public void testHorizontalSearchFindsAllJumpPoints() {
        addObstacles(g);

        // A forced neighbor moving down and right should be found
        // and it should also add itself back as a JumpPoint
        JumpPoint downRight = new JumpPoint(8, 0, 1, 1, 0, 0);
        JumpPoint down = new JumpPoint(8, 0, 1, 0, 0, 0);

        final PriorityQueue<JumpPoint> nodes = new PriorityQueue<>();
        final HashMap<JumpPoint, JumpPoint> closed = new HashMap<>();
        final Point p = new Point(0, 0);

        ArrayList<JumpPoint> foundNodes = Path.searchH(nodes, closed, p, 1, 0, g, null);
        assertEquals("An incorrect amount of JumpPoints were found during horizontal search", 2, foundNodes.size());
        assertTrue("A JumpPoint moving down and right was not present in foundNodes", foundNodes.contains(downRight));
        assertTrue("Horizontal search did not correctly add itself to foundNodes", foundNodes.contains(down));
    }
    
    @Test
    public void testDiagonalSearchFindsAllJumpPoints() {
        g.nodes[7][1] = Node.BLOCKED;
        g.nodes[8][2] = Node.BLOCKED;
        
        final PriorityQueue<JumpPoint> nodes = new PriorityQueue<>();
        final HashMap<JumpPoint, JumpPoint> closed = new HashMap<>();        
        final Point p = new Point(0, 9);
        
        JumpPoint upLeft = new JumpPoint(2, 7, -1, -1, 0, 0);        
        JumpPoint downRight = new JumpPoint(2, 7, 1, 1, 0, 0);
        
        ArrayList<JumpPoint> foundNodes = Path.searchD(nodes, closed, p, 1, -1, 0, g, null, 0L);        
        
        assertEquals("An incorrect amount of JumpPoints were found during diagonal search", 2, foundNodes.size());
        assertTrue("Diagonal search didn't find forced neighbor moving up and left", foundNodes.contains(upLeft));
        assertTrue("Diagonal search didn't find forced neighbor moving down and right", foundNodes.contains(downRight));
    }
}
