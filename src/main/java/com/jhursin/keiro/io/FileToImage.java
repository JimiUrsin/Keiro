
package com.jhursin.keiro.io;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import com.jhursin.keiro.logic.Node;

/**
 * Contains methods related to reading images from files
 */
public class FileToImage {
    
    /**
     * Read a file into a BufferedImage
     * @param filename Name of file to be read
     * @return Returns a BufferedImage that was read from file if successful, or a 1x1 empty image if not
     */
    public static BufferedImage readFile(String filename) {
        BufferedImage bimg = null;
        try {
            bimg = ImageIO.read(new File(filename));
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
        
        if (bimg == null) {
            return new BufferedImage(1, 1, BufferedImage.TYPE_INT_RGB);
        }
        return bimg;
    }
    
    public static Node[][] readFileToNodeArray(String filename) {
        BufferedImage bimg = readFile(filename);
        Node[][] pixels = new Node[bimg.getHeight()][bimg.getWidth()];
        for(int y = 0; y < bimg.getHeight(); y++) {
            for(int x = 0; x < bimg.getWidth(); x++) {
                System.out.println(String.format("Pixel RGB value is 0x%06X", bimg.getRGB(x, y)));
                pixels[y][x] = Node.match(bimg.getRGB(x, y));
            }
        }
        
        return pixels;
    }
}
