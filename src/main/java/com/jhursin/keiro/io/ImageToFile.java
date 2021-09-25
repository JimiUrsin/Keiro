package com.jhursin.keiro.io;

import com.jhursin.keiro.logic.Grid;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;

/**
 * Contains methods related to drawing pixel images to file.
 * 
 */
public final class ImageToFile {

    private ImageToFile() { }

    /**
     * Takes a BufferedImage as input and draws it to a PNG file.
     * @param bimg Image to be drawn to file
     * @param filename File to be written to
     */
    public static void drawToFile(final BufferedImage bimg, final String filename) {
        try {
            ImageIO.write(bimg, "PNG", new File(filename));
        } catch (IOException ioe) {
            if (ioe.getCause() == null) {
                System.err.println("Unknown error occurred while writing to file");
            } else {
                System.err.println(ioe.getCause());
            }
        }
    }

    /**
     * Takes a grid and draws it into a file using drawToFile.
     * @see #drawToFile(java.awt.image.BufferedImage, java.lang.String) 
     * @param grid Grid to be drawn
     * @param filename File to be written to
     */
    public static void drawGridToFile(final Grid grid, final String filename) {
        BufferedImage bimg = new BufferedImage(
                grid.nodes[0].length,
                grid.nodes.length,
                BufferedImage.TYPE_INT_RGB
        );

        for (int y = 0; y < bimg.getHeight(); y++) {
            for (int x = 0; x < bimg.getWidth(); x++) {
                bimg.setRGB(x, y, grid.nodes[y][x].getRGB());
            }
        }
        drawToFile(bimg, filename);
    }
}
