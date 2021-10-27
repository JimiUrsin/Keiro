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
 * It will read all maps from a folder and solve them 10 times each with
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
        for (int i = 0; i < 100; i++) {
            final int randX = random.nextInt(g.nodes[0].length);
            final int randY = random.nextInt(g.nodes.length);

            if (Path.valid(randX, randY, g)) {
                g.setStart(randX, randY);
            }
        }
        for (int i = 0; i < 100; i++) {
            final int randX = random.nextInt(g.nodes[0].length);
            final int randY = random.nextInt(g.nodes.length);

            if (Path.valid(randX, randY, g)) {
                g.setEnd(randX, randY);
            }
        }
    }

    @Before
    public void setUp() {
        files = new File("resources\\benchmark").listFiles();
        random = new Random(1234);
    }

    @Test
    public void testPerformance() {
        final ArrayList<Long> aStarTimes = new ArrayList<>();
        final ArrayList<Long> jpsTimes = new ArrayList<>();


        for (File f : files) {
            Grid g = FileToImage.readFileToGrid(f.getAbsolutePath());

            for (int i = 0; i < 1; i++) {
                findRandomStartAndEnd(g);

                long startTime = System.nanoTime();
                final double aStarLength = Path.solveAStar(g, null, 0);
                final long aStarTime = System.nanoTime() - startTime;
                aStarTimes.add(aStarTime);

                startTime = System.nanoTime();
                final double jpsLength = Path.solveJPS(g, null, 0);
                final long jpsTime = System.nanoTime() - startTime;
                jpsTimes.add(jpsTime);

                LOGGER.info(String.format("A* took %d ms and JPS took %d µs. JPS speedup ratio = %d", aStarTime / 1_000, jpsTime / 1_000, aStarTime / jpsTime));
                assertEquals(String.format("Path lengths weren't equal \nFilename: %s \nStart: (%d, %d)\tEnd:(%d, %d)", f.getAbsolutePath(), g.getStartX(), g.getStartY(), g.getEndX(), g.getEndY()), aStarLength, jpsLength, 0.1);
            }
        }

        double aStarAvg = 0;
        for (long l : aStarTimes) {
            aStarAvg += l;
        }
        aStarAvg /= aStarTimes.size();

        double jpsAvg = 0;
        for (long l : jpsTimes) {
            jpsAvg += l;
        }
        jpsAvg /= jpsTimes.size();

        LOGGER.info(String.format("Average A* time = %f ms \nAverage JPS time = %f ms", aStarAvg / 1_000_000, jpsAvg / 1_000_000));
    }
}
