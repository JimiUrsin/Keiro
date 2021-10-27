package com.jhursin.keiro.io;

import java.awt.image.BufferedImage;
import java.io.File;

import static org.junit.Assert.fail;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;


public class IOTest {
    /**
     * Makes a 100x100 dummy BufferedImage with an alternating black-and-white pattern
     * @return Dummy BufferedImage with aforementioned pattern
     */
    private static BufferedImage makeDummyImage() {
        BufferedImage dummy = new BufferedImage(100, 100, BufferedImage.TYPE_INT_RGB);
        boolean white = true;
        for(int y = 0; y < 100; y++) {
            white = !white;
            for(int x = 0; x < 100; x++) {
                if (white) {
                    dummy.setRGB(x, y, 0xFFFFFFFF);
                } else {
                    dummy.setRGB(x, y, 0xFF000000);
                }
                white = !white;
            }
        }
        return dummy;
    }

    BufferedImage bimg;

    @Before
    public void setUp() {
        bimg = makeDummyImage();
    }

    @Test
    public void testReadingFromFile() {
        BufferedImage checkers = FileToImage.readFile("resources\\test\\checkers.png");
        assertFalse("File reading was not successful", checkers.getWidth() == 1 && checkers.getHeight() == 1);
    }

    @Test
    public void testWritingAndReadingMatchesOriginal() {
        String filename = "resources\\test\\temp_checkers.png";
        ImageToFile.drawToFile(bimg, filename);
        BufferedImage bimg2 = FileToImage.readFile(filename);

        assertEquals("Image differs in width from original", bimg.getWidth(), bimg2.getWidth());
        assertEquals("Image differs in height from original", bimg.getHeight(), bimg2.getHeight());

        for(int y = 0; y < bimg.getHeight(); y++) {
            for(int x = 0; x < bimg.getWidth(); x++) {
                if (bimg.getRGB(x, y) != bimg2.getRGB(x, y)) {
                    fail(String.format("Pixels at (%d, %d) were different, expected %08X, was %08X", x, y, bimg.getRGB(x, y), bimg2.getRGB(x, y)));
                }
            }
        }
        File file = new File(filename);
        file.delete();
    }
}
