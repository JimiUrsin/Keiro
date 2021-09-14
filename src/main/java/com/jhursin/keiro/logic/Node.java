package com.jhursin.keiro.logic;

public enum Node {
    START   (0xFFFF0000),
    END     (0xFF00FF00),
    PATH    (0xFF000000),
    QUEUE   (0xFF000000),
    DROPPED (0xFF000000),
    EMPTY   (0xFF000000),
    BLOCKED (0xFFFFFFFF),
    UNKNOWN (0xBADC0DE);
    
    private final int rgb;
    
    Node(final int rgb) {
        this.rgb = rgb;
    }
    
    public int getRGB() {
        return this.rgb;
    }
    
    public static Node match(int rgb) {
        for (Node n: values()) {
            if (rgb == n.getRGB()) 
                return n;
        }
        return UNKNOWN;
    }    
}
