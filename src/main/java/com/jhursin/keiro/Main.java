package com.jhursin.keiro;

import com.jhursin.keiro.io.FileToImage;
import com.jhursin.keiro.io.ImageToFile;
import com.jhursin.keiro.logic.Grid;
import com.jhursin.keiro.logic.Path;

public class Main {
    public static void main(String[] args) {
        String filename = "test5.png";
        Grid grid = FileToImage.readFileToGrid(filename);
        Path.solveAStar(grid);
        ImageToFile.drawGridToFile(grid, "solved_" + filename);
        
    }
}
