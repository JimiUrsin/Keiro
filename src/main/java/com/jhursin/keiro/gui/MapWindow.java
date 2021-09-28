package com.jhursin.keiro.gui;

import java.awt.image.BufferedImage;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;

public class MapWindow {
    JFrame frame;
    BufferedImage bimg;

    private void constructMapWindow(BufferedImage bi) {
        this.frame = new JFrame("Keiro");
        this.bimg = bi;
        
        this.frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.frame.setResizable(false);
        this.frame.setVisible(false);
        
        
        this.frame.getContentPane().add(new JLabel(new ImageIcon(bimg)));
        
        this.frame.pack();
    }

    public MapWindow(BufferedImage bimg) {
        constructMapWindow(bimg);
    }

    public void show() {
        this.frame.setVisible(true);
    }

    public void hide() {
        this.frame.setVisible(false);
    }

    public void setRGB(int x, int y, int color) {
        bimg.setRGB(x, y, color);
        frame.repaint();
    }

    /*
    public void testRepaint() {
        long time = System.currentTimeMillis();
        int framerate = 240;
        int wait = 1000/framerate;
        for (int y = 0; y < 100; y++) {
            for (int x = 0; x < 100; x++) {
                while(System.currentTimeMillis() - time < wait);
                this.bimg.setRGB(x, y, 0xFFFF0000);
                time = System.currentTimeMillis();
                this.frame.repaint();
            }
        }
    }
    /*
    frame.addWindowListener(new WindowAdapter() {

            @Override
            public void windowClosing(WindowEvent e) {
                MainWindow.show();
            }
        });
    */
}
