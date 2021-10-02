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
        // Interpret light grays as empty
        if ((rgb & 0xFF0000) > 0xAA0000
         && (rgb & 0x00FF00) > 0x00AA00
         && (rgb & 0x0000FF) > 0x0000AA) {
            return Node.EMPTY;
        }
        // Interpret dark grays as blocked
        if ((rgb & 0xFF0000) < 0x550000
         && (rgb & 0x00FF00) < 0x005500
         && (rgb & 0x0000FF) < 0x000055) {
            return Node.BLOCKED;
        }
        
        // These are the "grass nodes" in the Moving AI benchmark set
        if ((rgb & 0xFFFFFF) == 0x007F00) {
            return Node.BLOCKED;
        }
        return UNKNOWN;
    }
}
