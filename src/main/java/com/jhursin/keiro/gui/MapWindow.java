package com.jhursin.keiro.gui;

import java.awt.Image;
import java.awt.image.BufferedImage;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;

/**
 * This window contains a map that will be solved.
 * 
 */
public class MapWindow {
    JFrame frame;
    BufferedImage bimg;
    int multiplier = 1;

    /**
     * Construct a window with a given image in it.
     * @param bi BufferedImage to be displayed
     */
    private void constructMapWindow(BufferedImage bi) {
        this.frame = new JFrame("Keiro");
        this.bimg = bi;
        
        this.frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.frame.setResizable(false);
        this.frame.setVisible(false);
        
        
        this.frame.getContentPane().add(new JLabel(new ImageIcon(bimg)));
        
        this.frame.pack();
    }
    
    private void constructMapWindow(BufferedImage bi, int newmultiplier) {
        if (newmultiplier < 0) {
            throw new IllegalArgumentException("Multiplier can't be less than 1");
        }
        this.frame = new JFrame("Keiro");
        this.multiplier = newmultiplier;
        
        Image newImage = bi.getScaledInstance(bi.getWidth() * this.multiplier, bi.getHeight() * this.multiplier, Image.SCALE_FAST);
        this.bimg = new BufferedImage(bi.getWidth() * this.multiplier, bi.getHeight() * this.multiplier, BufferedImage.TYPE_INT_RGB);
        this.bimg.getGraphics().drawImage(newImage, 0, 0, null);
        
        this.frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.frame.setResizable(false);
        this.frame.setVisible(false);
        
        
        this.frame.getContentPane().add(new JLabel(new ImageIcon(bimg)));
        
        this.frame.pack();
    }

    public MapWindow(BufferedImage bimg) {
        constructMapWindow(bimg);
    }
    
    public MapWindow(BufferedImage bimg, int multiplier) {
        constructMapWindow(bimg, multiplier);
    }

    public void show() {
        this.frame.setVisible(true);
    }

    public void hide() {
        this.frame.setVisible(false);
    }
    
    /**
     * Sets a pixel on this windows BufferedImage to a certain color.
     * If the image is scaled, sets additional pixels in a n*n area down and to
     * the right from the original pixel where n is the multiplier
     * Used by the pathfinding algorithm to show its progress
     * @param x X coordinate of pixel to be changed
     * @param y Y coordinate of pixel to be changed
     * @param color Color the pixel will be set to
     */
    public void setRGB(int x, int y, int color) {
        if (this.multiplier == 1) {
            this.bimg.setRGB(x, y, color);
        } else {
            x *= this.multiplier;
            y *= this.multiplier;
            for(int dy = 0; dy < this.multiplier; dy++) {
                for(int dx = 0; dx < this.multiplier; dx++) {
                    this.bimg.setRGB(x + dx, y + dy, color);
                }
            }
        }
        frame.repaint();
    }
}
