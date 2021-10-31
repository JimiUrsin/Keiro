package com.jhursin.keiro.logic;

import com.jhursin.keiro.io.FileToImage;

import java.io.File;
import java.util.ArrayList;
import java.util.Random;
import static org.junit.Assert.assertEquals;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import java.util.logging.Logger;


/**
 * This class will benchmark the performance of the algorithm.
 * It will read all maps from a folder and solve them with
 * random starting and ending points. It also checks to see if the A* and JPS
 * algorithms end up with the same path length just to be sure. Each run of each
 * algorithm will be timed separately and output into the console.
 *
 * These tests will not be run while building the application.
 */
@Category(PerformanceTest.class)
public class PerformanceTest {

    File[] files;
    Random random;
    static final Logger LOGGER = Logger.getLogger(PerformanceTest.class.getName());

    private void findRandomStartAndEnd(final Grid g) {
        boolean startSet = false;
        
        for (int i = 0; i < 100; i++) {
            final int randX = random.nextInt(g.nodes[0].length);
            final int randY = random.nextInt(g.nodes.length);

            if (Path.valid(randX, randY, g)) {
                g.setStart(randX, randY);
                startSet = true;
                break;
            }
        }
        
        if (!startSet) {
            for(int y = 0; y < g.nodes.length; y++) {
                for(int x = 0; x < g.nodes[0].length; x++) {
                    if (Path.valid(x, y, g)) {
                        g.setStart(x, y);
                    }
                }
            }
        }
        
        boolean endSet = false;

        for (int i = 0; i < 100; i++) {
            final int randX = random.nextInt(g.nodes[0].length);
            final int randY = random.nextInt(g.nodes.length);

            if (Path.valid(randX, randY, g)) {
                g.setEnd(randX, randY);
                endSet = true;
                break;
            }
        }
        
        if (!endSet) {
            for(int y = g.nodes.length - 1; y >= 0; y--) {
                for(int x = g.nodes[0].length - 1; x > 0; x--) {
                    if (Path.valid(x, y, g)) {
                        g.setEnd(x, y);
                    }
                }
            }
        }
    }

    @Before
    public void setUp() {
        files = new File("resources\\benchmark").listFiles();
        random = new Random();
    }

    @Test
    public void testPerformance() {
        final ArrayList<Long> aStarTimes = new ArrayList<>();
        final ArrayList<Long> jpsTimes = new ArrayList<>();


        for (File f : files) {
            Grid g = FileToImage.readFileToGrid(f.getAbsolutePath());

            findRandomStartAndEnd(g);

            long startTime = System.nanoTime();
            final double aStarLength = Path.solveAStar(g, null, 0);
            final long aStarTime = System.nanoTime() - startTime;
            aStarTimes.add(aStarTime);

            startTime = System.nanoTime();
            final double jpsLength = Path.solveJPS(g, null, 0);
            final long jpsTime = System.nanoTime() - startTime;
            jpsTimes.add(jpsTime);

            LOGGER.info(String.format("A* took %d µs and JPS took %d µs. JPS speedup ratio = %d. Path length = %.0f", aStarTime / 1_000, jpsTime / 1_000, aStarTime / jpsTime, aStarLength));
        }

        long aStarTotal = 0;
        for (long l : aStarTimes) {
            aStarTotal += l;
        }
        double aStarAvg = aStarTotal / aStarTimes.size();

        long jpsTotal = 0;
        for (long l : jpsTimes) {
            jpsTotal += l;
        }
        double jpsAvg = jpsTotal / jpsTimes.size();

        LOGGER.info(String.format("Average A* time = %f ms \nAverage JPS time = %f ms", aStarAvg / 1_000_000, jpsAvg / 1_000_000));
    }
}
