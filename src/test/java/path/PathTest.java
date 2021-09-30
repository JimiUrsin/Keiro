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
    
    Grid g;
    
    @Before
    public void setUp() {
        g = makeEmptyGrid(10, 10);
    }
    
    @Test
    public void testAStar() {
        Path.setDiagonal(false);
        int length = Path.solveAStar(g, null, 0);
        assertEquals("Path was not shortest possible", length, 19);
    }
    
    @Test
    public void testAStarDiagonal() {
        Path.setDiagonal(true);
        int length = Path.solveAStar(g, null, 0);
        assertEquals("Path was not shortest possible", length, 10);
    }
    
    @Test
    public void testAStarWithObstacles() {
        Path.setDiagonal(true);
        for(int y = 1; y < 9; y++) {
            g.nodes[y][1] = Node.BLOCKED;
        }
        for(int x = 1; x < 9; x++) {
            g.nodes[1][x] = Node.BLOCKED;
        }
        int length = Path.solveAStar(g, null, 0);
        
        assertEquals("Path was not shortest possible", length, 18);        
    }
    
    @Test
    public void testJPS() {
        Path.setDiagonal(true);
        int length = Path.solveJPS(g, null, 0);
        assertEquals("Path was not shortest possible", 10, length);
    }
    
    @Test
    public void testJPSWithObstacles() {
        Path.setDiagonal(true);
        for(int y = 1; y < 9; y++) {
            g.nodes[y][1] = Node.BLOCKED;
        }
        for(int x = 1; x < 9; x++) {
            g.nodes[1][x] = Node.BLOCKED;
        }
        int length = Path.solveJPS(g, null, 0);
        
        assertEquals("Path was not shortest possible", length, 18);        
    }
}
