package com.jhursin.keiro.logic;

public enum Node {
    START   (0xFFFF0000),
    END     (0xFF00FF00),
    PATH    (0xFF0000FF),
    QUEUE   (0xFF89CFF0),
    DROPPED (0xFFFF7F7F),
    EMPTY   (0xFFFFFFFF),
    BLOCKED (0xFF000000),
    UNKNOWN (0xBADC0DE);

    private final int rgb;

    Node(final int newrgb) {
        this.rgb = newrgb;
    }

    public int getRGB() {
        return this.rgb;
    }

    public static Node match(int rgb) {
        for (Node n: values()) {
            if (rgb == n.getRGB()) {
                return n;
            }
        }
        return UNKNOWN;
    }
}
