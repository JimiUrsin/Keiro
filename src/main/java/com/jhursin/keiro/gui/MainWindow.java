package com.jhursin.keiro.gui;

import java.awt.BorderLayout;
import javax.swing.*;

/**
 * This window will be presented to the user when they first start the program
 * 
 */
public class MainWindow {
    
    /**
     * Create the main GUI window
     * 
     */
    private static void constructGUI() {
        JFrame frame = new JFrame("Keiro");
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
    
    public static void show() {
        javax.swing.SwingUtilities.invokeLater(() -> {
            constructGUI();
        });
    }
}
