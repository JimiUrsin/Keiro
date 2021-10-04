package com.jhursin.keiro.gui;

import com.jhursin.keiro.logic.Grid;
import com.jhursin.keiro.logic.Node;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import java.awt.event.MouseListener;
import java.awt.event.MouseEvent;
import java.awt.image.DataBufferByte;
import java.awt.image.DataBufferInt;
import java.util.ArrayList;

class Point {
    int x;
    int y;
    
    Point(int x, int y) {
        this.x = x;
        this.y = y;
    }
}

/**
 * This window contains a map that will be solved.
 * 
 */
public class MapWindow {
    JFrame frame;
    BufferedImage bimg;
    BufferedImage bimg2;
    int multiplier = 1;
    public boolean startSet;
    public boolean goalSet;
    public boolean jpsHasRun;
    public boolean aStarHasRun;
    private boolean jpsRunning = false;
    Runnable jps;
    Runnable aStar;
    ArrayList<Point> tempPixels;

    /**
     * Construct a window with a given image in it.
     * @param bi BufferedImage to be displayed
     * @param g Grid for this MapWindow. Will be used to set start and end points
     */
    private void constructMapWindow(BufferedImage bi, Grid g) {
        this.frame = new JFrame("Keiro");
        this.bimg = bi;
        
        this.bimg2 = new BufferedImage(bi.getWidth(), bi.getHeight(), BufferedImage.TYPE_INT_RGB);
        for(int y = 0; y < bi.getHeight(); y++) {
            for(int x = 0; x < bi.getWidth(); x++) {
                this.bimg2.setRGB(x, y, bi.getRGB(x, y));
            }
        }
        
        // TODO Change this when we use more than one MapWindow
        this.frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.frame.setResizable(false);
        this.frame.setVisible(false);
        
        JLabel label = new JLabel(new ImageIcon(bimg));
        
        // ImageIcons can't have listeners, so put it on the JLabel
        label.addMouseListener(new MouseListener() {
            @Override
            public void mousePressed(MouseEvent e) { }

            @Override
            public void mouseReleased(MouseEvent e) { }

            @Override
            public void mouseEntered(MouseEvent e) { }

            @Override
            public void mouseExited(MouseEvent e) { }

            @Override
            public void mouseClicked(MouseEvent e) {
                // Side note: I know I'm referencing the static version of
                // startSet/endSet/hasruns here but I can't refer to the 
                // local version via the EventListener, this is probably 
                // to prevent dangling pointer reference but we should
                // never refer to it after destruction, since this listener
                // will be destroyed along with this MapWindow
                
                // Click actions in order:
                // 1. Set the starting point if point clicked was on the map and not blocked
                // 2. Set the ending point if point clicked was on the map and not blocked
                // 3. Run JPS
                // 4. Run A*
                
                // This is an awful mess but I just had to get this working
                // TODO Pretty much rewrite this whole thing when JPS is actually working
                if (!startSet) {
                    if (e.getX() < 0 || e.getX() > g.nodes[0].length
                     || e.getY() < 0 || e.getY() > g.nodes.length) {
                        return;
                    } else if (g.nodes[e.getY()][e.getX()] == Node.BLOCKED) {
                        return;
                    }
                    System.out.println(String.format("Setting start to (%d, %d)", e.getX(), e.getY()));
                    g.setStart(e.getX(), e.getY());
                    drawStart(e.getX(), e.getY());
                    startSet = true;
                    Toolkit.getDefaultToolkit().beep();
                } else if (!goalSet) {
                    if (e.getX() < 0 || e.getX() > g.nodes[0].length
                     || e.getY() < 0 || e.getY() > g.nodes.length) {
                        return;
                    } else if (g.nodes[e.getY()][e.getX()] == Node.BLOCKED) {
                        return;
                    }
                    drawEnd(e.getX(), e.getY());
                    System.out.println(String.format("Setting goal to (%d, %d)", e.getX(), e.getY()));
                    g.setEnd(e.getX(), e.getY());
                    goalSet = true;
                    Toolkit.getDefaultToolkit().beep();
                } else {
                    if (!jpsHasRun && !jpsRunning) {
                        Thread t = new Thread(jps);
                        t.start();
                        jpsRunning = true;
                    } else if (jpsHasRun && !aStarHasRun) {
                        for(int y = 0; y < bi.getHeight(); y++) {
                            for(int x = 0; x < bi.getWidth(); x++) {
                                bimg.setRGB(x, y, bimg2.getRGB(x, y));
                            }
                        }
                        Thread t = new Thread(aStar);
                        t.start();
                        aStarHasRun = true;
                    }
                }
            }
        });
        
        this.frame.getContentPane().add(label);
        
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

    public MapWindow(BufferedImage bimg, Grid g) {
        this.jpsHasRun = false;
        this.aStarHasRun = false;
        this.startSet = false;
        this.goalSet = false;
        this.tempPixels = new ArrayList<>();
        constructMapWindow(bimg, g);
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
    
    public void setJPS(Runnable jps) {
        this.jps = jps;
    }
    
    public void setAStar(Runnable aStar) {
        this.aStar = aStar;
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
    
    /**
     * Set a pixel to a certain color temporarily.
     * @param x X component of pixel to be changed
     * @param y Y component of pixel to be changed
     */
    public void setTemp(int x, int y) {
        setRGB(x, y, Node.DROPPED.getRGB());
        tempPixels.add(new Point(x, y));
    }
    
    public void flushTemp() {
        for (Point p : tempPixels) {
            setRGB(p.x, p.y, Node.QUEUE.getRGB());
        }
        tempPixels.clear();
    }
    
    // TODO Check out of bounds
    private void drawStart(int x, int y) {
        int d = Math.min(this.bimg.getHeight(), this.bimg.getWidth()) / 100 + 1;
        x -= d / 2;
        y -= d / 2;
        
        for (int dy = 0; dy < d; dy++) {
            for (int dx = 0; dx < d; dx++) {
                this.setRGB(x+dx, y+dy, Node.START.getRGB());
            }
        }
    }
    
    // TODO Check out of bounds
    private void drawEnd(int x, int y) {
        int d = Math.min(this.bimg.getHeight(), this.bimg.getWidth()) / 100 + 1;
        x -= d / 2;
        y -= d / 2;
        
        for (int dy = 0; dy < d; dy++) {
            for (int dx = 0; dx < d; dx++) {
                this.setRGB(x+dx, y+dy, Node.END.getRGB());
            }
        }
    }
    
    /**
     * Draws a path between two points
     * @param x1 X component of point 1
     * @param y1 Y component of point 1
     * @param x2 X component of point 2
     * @param y2 Y component of point 2
     */
    public void drawPath(int x1, int y1, int x2, int y2) {
        int dx = 0;
        int dy = 0;
        
        if (x1 < x2) {
            dx = 1;
        }
        if (x1 > x2) {
            dx = -1;
        }

        if (y1 < y2) {
            dy = 1;
        } else if (y1 > y2) {
            dy = -1;
        }
        
        boolean widen = Math.min(this.bimg.getWidth(), this.bimg.getHeight()) > 500;
        
        while (x1 != x2 || y1 != y2) {
            setRGB(x1, y1, Node.PATH.getRGB());
            x1 += dx;
            y1 += dy;
            
            if (widen) {
                if (dx == 0) {
                    setRGB(x1 + 1, y1, Node.PATH.getRGB());
                    setRGB(x1 - 1, y1, Node.PATH.getRGB());
                } else {                    
                    setRGB(x1, y1 + 1, Node.PATH.getRGB());
                    setRGB(x1, y1 - 1, Node.PATH.getRGB());
                }
            }
        }
        setRGB(x1, y1, Node.PATH.getRGB());
    }
}
