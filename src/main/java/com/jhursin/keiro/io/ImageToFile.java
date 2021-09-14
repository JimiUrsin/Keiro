package com.jhursin.keiro.io;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;

/**
 * Contains methods related to drawing pixel images to file
 * 
 */
public class ImageToFile {
    
    /**
     * Takes a BufferedImage as input and draws it to a PNG file
     * @param bimg Image to be drawn to file
     * @param filename File to be written to
     */
    public void drawToFile(BufferedImage bimg, String filename){
        try {
            ImageIO.write(bimg, "PNG", new File(filename));
        } catch (IOException ioe) {
            if (ioe.getCause() == null) {
                System.err.println("Unknown error occurred during file rendering");
            } else {
                System.err.println(ioe.getCause());
            }
        }
    }
    
    /**
     * Draws a dummy image to file
     * @param filename Name of the dummy image file
     */
    public void drawDummyToFile(String filename) {
        BufferedImage bimg = makeDummyImage();
        drawToFile(bimg, filename);
    }
    
    /**
     * Makes a 100x100 dummy BufferedImage with an alternating black-and-white pattern
     * used mainly for testing purposes.
     * @return Dummy BufferedImage with aforementioned pattern
     */
    private BufferedImage makeDummyImage() {
        BufferedImage dummy = new BufferedImage(100, 100, BufferedImage.TYPE_INT_RGB);
        boolean white = true;
        for(int y = 0; y < 100; y++) {
            white = !white;
            for(int x = 0; x < 100; x++) {
                if (white) {
                    dummy.setRGB(x, y, 0xFFFFFF);
                } else {
                    dummy.setRGB(x, y, 0x000000);
                }
                white = !white;
            }
        }
        return dummy;
    }
}
