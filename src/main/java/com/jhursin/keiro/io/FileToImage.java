
package com.jhursin.keiro.io;

import com.jhursin.keiro.logic.Grid;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
import com.jhursin.keiro.logic.Node;

/**
 * Contains methods related to reading images from files.
 *
 */
public final class FileToImage {

    /**
     * Prevent instantiation.
     */
    private FileToImage() { }

    /**
     * Read a file into a BufferedImage.
     * @param filename Name of file to be read
     * @return Returns a BufferedImage that was read from file if successful,
     * or a 1x1 empty image if not
     */
    public static BufferedImage readFile(final String filename) {
        BufferedImage bimg = null;
        try {
            bimg = ImageIO.read(new File(filename));
        } catch (IOException ioe) {
            // TODO Better error handling
            ioe.printStackTrace();
        }

        if (bimg == null) {
            return new BufferedImage(1, 1, BufferedImage.TYPE_INT_RGB);
        }

        return bimg;
    }

    /**
     * Constructs a Grid object from an image with given filename.
     * It will find the starting and ending points of the given map
     * Throws if the given map is malformed
     * (more than one starting or ending point, empty image, etc...)
     * @param filename Name of file to be read
     * @return A Grid object constructed from an image
     */
    public static Grid readFileToGrid(final String filename) {
        BufferedImage bimg = readFile(filename);

        if (bimg.getHeight() == 0 || bimg.getWidth() == 0) {
            throw new IllegalArgumentException("Neither of the map's dimensions can be zero");
        }

        Grid grid = new Grid(bimg.getWidth(), bimg.getHeight());

        for (int y = 0; y < bimg.getHeight(); y++) {
            for (int x = 0; x < bimg.getWidth(); x++) {
                Node n = Node.match(bimg.getRGB(x, y));
                if (n == Node.BLOCKED) {
                    grid.obstacles++;
                }
                grid.nodes[y][x] = n;
            }
        }

        return grid;
    }
}
