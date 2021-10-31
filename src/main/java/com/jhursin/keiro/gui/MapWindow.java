package com.jhursin.keiro.gui;

import com.jhursin.keiro.logic.Grid;
import com.jhursin.keiro.logic.Node;
import com.jhursin.keiro.logic.Path;
import java.awt.BorderLayout;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.event.MouseAdapter;
import java.awt.image.BufferedImage;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import java.awt.event.MouseEvent;
import java.util.ArrayList;

//CHECKSTYLE:OFF
class Point {
    final int x;
    final int y;

    Point(final int x, final int y) {
        this.x = x;
        this.y = y;
    }
}
//CHECKSTYLE:ON

/**
 * This window contains a map that will be solved.
 *
 */
public class MapWindow {

    /**
     * First BufferedImage, this is the one that will be displayed and edited.
     */
    BufferedImage bimg;

    /**
     * Second BufferedImage, this will be copied from after each algorithm is done.
     */
    BufferedImage bimg2;

    /**
     * How many times the image will be magnified.
     * 2 means a 10x10 image becomes 20x20 on the screen and so on
     */
    int multiplier = 1;

    /**
     * Pixels that show where JPS is currently searching diagonally.
     * These will be turned into another color later.
     */
    ArrayList<Point> tempPixels;

    // CHECKSTYLE:OFF
    // These are all self-explanatory
    JFrame frame;
    public boolean startSet;
    public boolean goalSet;
    public boolean jpsHasRun;
    public boolean aStarHasRun;
    private boolean jpsRunning = false;
    Runnable jps;
    Runnable aStar;
    // CHECKSTYLE:ON


    /**
     * Copies the contents from one BufferedImage to another.
     * @param to BufferedImage that will be copied into
     * @param from BufferedImage whose contents will be copied
     */
    private void copyBufferedImage(final BufferedImage to, final BufferedImage from) {
        for (int y = 0; y < from.getHeight(); y++) {
            for (int x = 0; x < from.getWidth(); x++) {
                to.setRGB(x, y, from.getRGB(x, y));
            }
        }
    }

    /**
     * Construct a window with a given image in it.
     * @param bi BufferedImage to be displayed
     * @param g Grid for this MapWindow. Will be used to set start and end points
     */
    private void constructMapWindow(final BufferedImage bi, final Grid g) {
        this.frame = new JFrame("Keiro");
        this.bimg = bi;
        this.bimg2 = new BufferedImage(bi.getWidth(), bi.getHeight(), BufferedImage.TYPE_INT_RGB);
        copyBufferedImage(bimg2, bimg);

        // TODO Change this when we use more than one MapWindow
        this.frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.frame.setResizable(false);
        this.frame.setVisible(false);
        this.frame.setLayout(new BorderLayout());

        JLabel label = new JLabel(new ImageIcon(bimg));
        JLabel mousePos = new JLabel("0, 0");

        final MouseAdapter listener = new MouseAdapter() {
            @Override
            public void mousePressed(final MouseEvent e) {
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
                // TODO Still not perfect but getting there

                final int clickX = e.getX();
                final int clickY = e.getY();
                if (!startSet) {
                    if (!Path.valid(clickX, clickY, g)) {
                        return;
                    }
                    g.setStart(clickX, clickY);
                    drawStart(clickX, clickY);

                    startSet = true;
                    Toolkit.getDefaultToolkit().beep();
                } else if (!goalSet) {
                    if (!Path.valid(clickX, clickY, g)) {
                        return;
                    }

                    drawEnd(clickX, clickY);
                    g.setEnd(clickX, clickY);

                    goalSet = true;
                    Toolkit.getDefaultToolkit().beep();
                } else {
                    if (!jpsHasRun && !jpsRunning) {
                        if (jps != null) {
                            Thread t = new Thread(jps);
                            t.start();
                            jpsRunning = true;
                        } else {
                            jpsHasRun = true;
                        }
                    }
                    if (jpsHasRun && !aStarHasRun) {
                        aStarHasRun = true;
                        copyBufferedImage(bimg, bimg2);
                        Thread t = new Thread(aStar);
                        t.start();
                    }
                }
            }

            @Override
            public void mouseMoved(final MouseEvent e) {
                int mouseX = e.getX();
                int mouseY = e.getY();
                mousePos.setText(mouseX + ", " + mouseY);
            }
        };

        label.addMouseListener(listener);
        label.addMouseMotionListener(listener);

        this.frame.getContentPane().add(label, BorderLayout.NORTH);
        this.frame.getContentPane().add(mousePos, BorderLayout.SOUTH);

        this.frame.pack();
    }

    /**
     * Construct a MapWindow with a given multiplier.
     * @param bi BufferedImage that will be displayed in this MapWindow
     * @param multiplier How many times the BufferedImage will be magnified
     */
    private void constructMapWindow(final BufferedImage bi, final int multiplier) {
        if (multiplier < 1) {
            throw new IllegalArgumentException("Multiplier can't be less than 1");
        }

        this.frame = new JFrame("Keiro");
        this.multiplier = multiplier;

        Image newImage = bi.getScaledInstance(
                bi.getWidth() * this.multiplier,
                bi.getHeight() * this.multiplier,
                Image.SCALE_FAST
        );

        this.bimg = new BufferedImage(
                bi.getWidth() * this.multiplier,
                bi.getHeight() * this.multiplier,
                BufferedImage.TYPE_INT_RGB
        );

        this.bimg.getGraphics().drawImage(newImage, 0, 0, null);

        this.frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.frame.setResizable(false);
        this.frame.setVisible(false);


        this.frame.getContentPane().add(new JLabel(new ImageIcon(bimg)));

        this.frame.pack();
    }

    /**
     * Construct a MapWindow.
     * @param bimg BufferedImage that will be displayed in this MapWindow
     * @param g Grid that holds the underlying Nodes of the image
     */
    public MapWindow(final BufferedImage bimg, final Grid g) {
        this.jpsHasRun = false;
        this.aStarHasRun = false;
        this.startSet = false;
        this.goalSet = false;
        this.tempPixels = new ArrayList<>();
        constructMapWindow(bimg, g);
    }

    /**
     * Construct a MapWindow with a given multiplier.
     * @param bimg BufferedImage that will be displayed in this MapWindow
     * @param multiplier How many times the BufferedImage will be magnified
     */
    public MapWindow(final BufferedImage bimg, final int multiplier) {
        constructMapWindow(bimg, multiplier);
    }

    /**
     * Show this MapWindow.
     */
    public final void show() {
        this.frame.setVisible(true);
    }

    /**
     * Hide this MapWindow.
     */
    public final void hide() {
        this.frame.setVisible(false);
    }

    /**
     * Set the JPS Runnable for this MapWindow.
     * It will be run when the start and goal are set
     * and the user clicks the MapWindow after that.
     * @param jps A Runnable that contains a JPS ready to run
     */
    public final void setJPS(final Runnable jps) {
        this.jps = jps;
    }

    /**
     * Set the A* Runnable for this MapWindow.
     * This will be run after the JPS algorithm has run completely
     * and the user clicks the MapWindow after that.
     * @param aStar A Runnable that contains an A* ready to run
     */
    public final void setAStar(final Runnable aStar) {
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
    public final void setRGB(final int x, final int y, final int color) {
        if (this.multiplier == 1) {
            this.bimg.setRGB(x, y, color);
        } else {
            final int multX = x * this.multiplier;
            final int multY = y * this.multiplier;
            for (int dy = 0; dy < this.multiplier; dy++) {
                for (int dx = 0; dx < this.multiplier; dx++) {
                    this.bimg.setRGB(multX + dx, multY + dy, color);
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
    public final void setTemp(final int x, final int y) {
        setRGB(x, y, Node.DROPPED.getRGB());
        tempPixels.add(new Point(x, y));
    }

    /**
     * Change all temporarily changed pixels back.
     * This is to show that the pixels are in the "closed" queue.
     */
    public final void flushTemp() {
        for (Point p : tempPixels) {
            setRGB(p.x, p.y, Node.QUEUE.getRGB());
        }
        tempPixels.clear();
    }

    /**
     * Draws a starting point onto this MapWindow.
     * The starting point grows based on the size of this MapWindow's BufferedImage.
     * @param x X coordinate of the starting point's center.
     * @param y Y coordinate of the starting point's center.
     */
    private void drawStart(final int x, final int y) {

        int d = Math.min(this.bimg.getHeight(), this.bimg.getWidth()) / 100 + 1;

        int startX = x - d / 2;
        int startY = y - d / 2;

        for (int dy = 0; dy < d; dy++) {
            for (int dx = 0; dx < d; dx++) {
                if (startX + dx < 0 || startX + dx >= this.bimg.getWidth()
                 || startY + dy < 0 || startY + dy >= this.bimg.getHeight()) {
                    continue;
                }
                this.setRGB(startX + dx, startY + dy, Node.START.getRGB());
            }
        }
    }

    /**
     * Draws an ending point onto this MapWindow.
     * The ending point grows based on the size of this MapWindow's BufferedImage.
     * @param x X coordinate of the ending point's center.
     * @param y Y coordinate of the ending point's center.
     */
    private void drawEnd(final int x, final int y) {
        int d = Math.min(this.bimg.getHeight(), this.bimg.getWidth()) / 100 + 1;
        int startX = x - d / 2;
        int startY = y - d / 2;

        for (int dy = 0; dy < d; dy++) {
            for (int dx = 0; dx < d; dx++) {
                if (startX + dx < 0 || startX + dx >= this.bimg.getWidth()
                 || startY + dy < 0 || startY + dy >= this.bimg.getHeight()) {
                    continue;
                }
                this.setRGB(startX + dx, startY + dy, Node.END.getRGB());
            }
        }
    }

    /**
     * Draws a path between two points.
     * @param x1 X component of point 1
     * @param y1 Y component of point 1
     * @param x2 X component of point 2
     * @param y2 Y component of point 2
     */
    public final void drawPath(final int x1, final int y1, final int x2, final int y2) {
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

        int cX = x1;
        int cY = y1;

        while (cX != x2 || cY != y2) {

            setRGB(cX, cY, Node.PATH.getRGB());

            cX += dx;
            cY += dy;

            if (widen) {
                if (dx == 0) {
                    setRGB(cX + 1, cY, Node.PATH.getRGB());
                    setRGB(cX - 1, cY, Node.PATH.getRGB());
                } else {
                    setRGB(cX, cY + 1, Node.PATH.getRGB());
                    setRGB(cX, cY - 1, Node.PATH.getRGB());
                }
            }
        }
        setRGB(cX, cY, Node.PATH.getRGB());
    }
}
