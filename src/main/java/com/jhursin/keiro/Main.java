package com.jhursin.keiro;

import com.jhursin.keiro.gui.MainWindow;
import com.jhursin.keiro.gui.MapWindow;
import com.jhursin.keiro.io.FileToImage;
import com.jhursin.keiro.logic.Grid;
import com.jhursin.keiro.logic.Path;
import java.awt.image.BufferedImage;

public class Main {
    public static void main(String[] args) {
        
        // Get a file from the user.
        // The filename will be the absolute (i.e. not relative) path
        // This is less error prone
        String filename = MainWindow.getFileFromUser();
        if (filename.equals("")) System.exit(0);
        System.out.println("filename = " + filename);
        
        // TODO Change these such that the file doesn't need to be read twice
        // and we can just read from BufferedImage to Grid or vice versa
        BufferedImage bimg = FileToImage.readFile(filename);
        Grid grid = FileToImage.readFileToGrid(filename);
        
        // The user will see the actual colors of the map since we make the window
        // based on the read image and not the grid
        MapWindow mw = new MapWindow(bimg, grid);
        
        // Run these on their own thread so picture processing can happen while
        // the algorithm is solving
        Runnable JPS = new Runnable() {
            @Override
            public void run() {                
                Path.solveJPS(grid, mw, 3000L);
            }
        };
        
        Runnable aStar = new Runnable() {
            @Override
            public void run() {                
                Path.solveAStar(grid, mw, 10000);
            }
        };
        
        // This is not a very good way to do this, I know, sorry.
        mw.setJPS(JPS);
        mw.setAStar(aStar);
        
        mw.show();
        Path.setDiagonal(true);
    }
}
