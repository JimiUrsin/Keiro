package path;

import com.jhursin.keiro.logic.Grid;
import com.jhursin.keiro.logic.Node;
import com.jhursin.keiro.logic.Path;
import static org.junit.Assert.assertEquals;
import org.junit.Before;
import org.junit.Test;

public class PathTest {

    /**
     * Make and grid full of empty nodes with a start node in the upper left corner
     * and an end node in the bottom right
     * @param width
     * @param height
     * @return
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
}
