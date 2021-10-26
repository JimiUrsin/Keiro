package com.jhursin.keiro.gui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Toolkit;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.filechooser.FileNameExtensionFilter;

/**
 * This window will be presented to the user when they first start the program (eventually).
 *
 */
public final class MainWindow {

    /**
     * Prevent instantiation.
     */
    private MainWindow() { }

    /**
     * Contains everything in the window that will be presented.
     */
    private static final JFrame FRAME = new JFrame("Keiro");

    /**
     * Create the main GUI window.
     *
     */
    private static void constructGUI() {
        FRAME.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        FRAME.setResizable(false);
        FRAME.setVisible(true);
        FRAME.setLayout(new BoxLayout(FRAME.getContentPane(), BoxLayout.Y_AXIS));

        JLabel label = new JLabel("Keiro");
        JButton button1 = new JButton("Button 1");
        JButton button2 = new JButton("Button 2");

        FRAME.getContentPane().add(label);
        FRAME.getContentPane().add(button1);
        FRAME.getContentPane().add(button2);

        FRAME.pack();
    }

    /**
     * Show the main GUI window.
     *
     */
    public static void show() {
        FRAME.setVisible(true);
    }

    /**
     * Create the main GUI window.
     */
    public static void create() {
        constructGUI();
    }

    /**
     * Hide the main GUI window.
     */
    public static void hide() {
        FRAME.setVisible(false);
    }

    /**
     * Request user to choose a PNG file from their computer.
     * @return If the user chose a file, its absolute filename. Null otherwise
     */
    public static String getFileFromUser() {
        JFileChooser chooser = new JFileChooser();
        FileNameExtensionFilter filter = new FileNameExtensionFilter(
            "PNG images", "png");
        chooser.setFileFilter(filter);
        int returnVal = chooser.showOpenDialog(null);
        if (returnVal == JFileChooser.APPROVE_OPTION) {
            return chooser.getSelectedFile().getAbsolutePath();
        } else {
            System.err.println("No file from user");
        }
        return null;
    }

    /**
     * Request user to choose an algorithm from two options.
     * @return Zero, this method is not yet functional.
     */
    public static int showPopup() {
        JPopupMenu popup = new JPopupMenu("Choose an algorithm");

        JPanel panel = new JPanel();
        JButton aStar = new JButton("A*");
        JButton jps = new JButton("Jump Point Search");

        panel.add(aStar, BorderLayout.NORTH);
        panel.add(jps, BorderLayout.NORTH);

        popup.add(aStar);
        popup.add(jps);

        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        popup.show(null, screenSize.width / 2, screenSize.height / 2);

        return 0;
    }
}
