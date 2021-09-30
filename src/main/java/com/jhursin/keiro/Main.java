package com.jhursin.keiro;

import com.jhursin.keiro.gui.MapWindow;
import com.jhursin.keiro.io.FileToImage;
import com.jhursin.keiro.logic.Grid;
import com.jhursin.keiro.logic.Path;
import java.awt.image.BufferedImage;

public class Main {
    public static void main(String[] args) {
        BufferedImage bimg = FileToImage.readFile("test2.png");
        Grid grid = FileToImage.readFileToGrid("test2.png");
        
        MapWindow mw = new MapWindow(bimg);
        mw.show();
        try {
        Thread.sleep(300);
        } catch (Exception e) {
            
        }
        Path.setDiagonal(true);
        int length = Path.solveJPS(grid, mw, 0);
        System.out.println("length = " + length);
    }
}
