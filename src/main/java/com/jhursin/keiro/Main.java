package com.jhursin.keiro;

import com.jhursin.keiro.gui.MainWindow;
import com.jhursin.keiro.gui.MapWindow;
import com.jhursin.keiro.io.FileToImage;
import com.jhursin.keiro.logic.Grid;
import com.jhursin.keiro.logic.Path;
import java.awt.image.BufferedImage;

/**
 * Main class.
 * Requests a file from the user, reads it and sets up the program
 */
public final class Main {

    /**
     * Prevent instantiation.
     */
    private Main() { }

    /**
     * Set up the GUI and displays it to the user.
     * @param args Command line arguments, these are ignored
     */
    public static void main(final String[] args) {

        // Get a file from the user.
        // The filename will be the absolute (i.e. not relative) path
        // This is less error prone
        String filename = MainWindow.getFileFromUser();

        // If no file was supplied, there is no point in continuing
        if (filename == null) {
            System.exit(0);
        }

        // TODO Change these such that the file doesn't need to be read twice
        // and we can just read from BufferedImage to Grid or vice versa
        BufferedImage bimg = FileToImage.readFile(filename);
        Grid grid = FileToImage.readFileToGrid(filename);

        // The user will see the actual colors of the map since we make the window
        // based on the read image and not the grid
        MapWindow mw = new MapWindow(bimg, grid);

        // Run these on their own thread so picture processing can happen while
        // the algorithm is solving
        Runnable jps = () -> {
            Path.solveJPS(grid, mw, 1000L);
        };

        Runnable aStar = () -> {
            Path.solveAStar(grid, mw, 5000L);
        };

        mw.setJPS(jps);
        mw.setAStar(aStar);

        mw.show();
    }
}
