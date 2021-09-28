package com.jhursin.keiro;

import com.jhursin.keiro.gui.MapWindow;
import com.jhursin.keiro.io.FileToImage;
import com.jhursin.keiro.logic.Grid;
import com.jhursin.keiro.logic.Path;
import java.awt.image.BufferedImage;

public class Main {
    public static void main(String[] args) {
        BufferedImage bimg = FileToImage.readFile("test5.png");
        Grid grid = FileToImage.readFileToGrid("test5.png");
        
        MapWindow mw = new MapWindow(bimg);
        mw.show();
        try {
        Thread.sleep(300);
        } catch (Exception e) {
            
        }
        Path.setDiagonal(true);
        Path.solveAStar(grid, mw, 100_000);        
    }
}
