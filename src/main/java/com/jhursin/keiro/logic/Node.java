package com.jhursin.keiro.logic;

/**
 * These will be used to represent anything that can be found on a map.
 */
public enum Node {
    // CHECKSTYLE:OFF
    START   (0xFFFF0000),
    END     (0xFF00FF00),
    PATH    (0xFF0000FF),
    QUEUE   (0xFF89CFF0),
    DROPPED (0xFFFF7F7F),
    EMPTY   (0xFFFFFFFF),
    BLOCKED (0xFF000000),
    UNKNOWN (0xBADC0DE);
    // CHECKSTYLE:ON

    /**
     * What color this Node is.
     */
    private final int rgb;

    /**
     * Create a new Node.
     * @param rgb RGB value to be set for this Node
     */
    Node(final int rgb) {
        this.rgb = rgb;
    }

    /**
     * Get the RGB value of this Node.
     * @return The RGB value of this Node.
     */
    public int getRGB() {
        return this.rgb;
    }

    /**
     * Return a Node that best represents the given color.
     * @param rgb Color that will be matched against this enum's colors.
     * @return Node with the closest color representation found in this enum.
     */
    public static Node match(final int rgb) {
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
