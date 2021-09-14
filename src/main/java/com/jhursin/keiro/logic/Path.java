package com.jhursin.keiro.logic;

import com.jhursin.keiro.logic.Node;
import com.jhursin.keiro.io.FileToImage;

/**
 * Contains pathfinding algorithms
 */

public class Path {
    public static void printNodeArray() {
        Node[][] nodeArray = FileToImage.readFileToNodeArray("test.png");
        
        for(int y = 0; y < nodeArray.length; y++) {
            for(int x = 0; x < nodeArray[0].length; x++) {
                System.out.print(String.format("%06X\t", nodeArray[y][x].getRGB()));
            }
            System.out.println();
        }
    }
}
