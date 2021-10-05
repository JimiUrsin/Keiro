package com.jhursin.keiro.gui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.io.File;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.filechooser.FileNameExtensionFilter;

/**
 * This window will be presented to the user when they first start the program.
 *
 */
public final class MainWindow {

    private MainWindow() { }


    private static final JFrame frame = new JFrame("Keiro");
    
    /**
     * Create the main GUI window.
     *
     */
    private static void constructGUI() {
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setResizable(false);
        frame.setVisible(true);
        frame.setLayout(new BoxLayout(frame.getContentPane(), BoxLayout.Y_AXIS));

        JLabel label = new JLabel("Keiro");
        JButton button1 = new JButton("Button 1");
        JButton button2 = new JButton("Button 2");

        frame.getContentPane().add(label);
        frame.getContentPane().add(button1);
        frame.getContentPane().add(button2);

        frame.pack();
    }

    /**
     * Show the main GUI window.
     *
     */
    public static void show() {
        frame.setVisible(true);
    }
    
    public static void create() {
        constructGUI();
    }
    
    public static void hide() {
        frame.setVisible(false);
    }
    
    public static String getFileFromUser() {
        JFileChooser chooser = new JFileChooser();
        FileNameExtensionFilter filter = new FileNameExtensionFilter(
            "PNG images", "png");
        chooser.setFileFilter(filter);
        int returnVal = chooser.showOpenDialog(null);
        if(returnVal == JFileChooser.APPROVE_OPTION) {
            return chooser.getSelectedFile().getAbsolutePath();
        } else {
            System.err.println("No file from user");
        }
        return null;
    }
    
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
