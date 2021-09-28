package com.jhursin.keiro;

import com.jhursin.keiro.gui.MainWindow;
import com.jhursin.keiro.gui.MapWindow;
import com.jhursin.keiro.io.FileToImage;
import java.awt.image.BufferedImage;

public class Main {
    public static void main(String[] args) {
        MainWindow.create();
        MainWindow.hide();
        BufferedImage bimg = FileToImage.readFile("test4.png");
        MapWindow mw = new MapWindow(bimg);
        mw.show();
        
    }
}
