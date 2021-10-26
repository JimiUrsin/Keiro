package com.jhursin.keiro.logic;

import java.util.Objects;

/**
 * A container for all things a JumpPoint needs.
 * Specifically, location, direction, g, h, and f values and a parent JumpPoint
 */
class JumpPoint implements Comparable<JumpPoint> {

    // CHECKSTYLE:OFF
    final int x;
    final int y;
    final int dx;
    final int dy;

    // Distance travelled so far - the "g" value
    final double dst;

    private final double h;
    private final double f;

    private JumpPoint parent;
    // CHECKSTYLE:ON

    /**
     * Create a new JumpPoint.
     * This constructor should be used for special cases
     * where a direction is not needed, such as for the goal
     * @param x X coordinate of JumpPoint
     * @param y Y coordinate of JumpPoint
     * @param h Heuristic value of JumpPoint (distance to goal)
     * @param dst Distance travelled so far (g-value)
     */
    JumpPoint(final int x, final int y, final double h, final double dst) {
        this.x = x;
        this.y = y;
        this.h = h;
        this.dst = dst;
        this.dx = 0;
        this.dy = 0;
        this.f = dst + h;
    }

    /**
     * Create a new JumpPoint.
     * @param x X coordinate of JumpPoint
     * @param y Y coordinate of JumpPoint
     * @param dx X delta of this JumpPoint (-1 or 1 when going left or right, respectively)
     * @param dy Y delta of this JumpPoint (-1 or 1 when going up or down, respectively)
     * @param h Heuristic value of JumpPoint (distance to goal)
     * @param dst Distance travelled so far (g-value)
     */
    JumpPoint(final int x, final int y, final int dx, final int dy, final double h, final double dst) {
        this.x = x;
        this.y = y;
        this.dx = dx;
        this.dy = dy;
        this.h = h;
        this.dst = dst;
        this.f = dst + h;
    }

    // CHECKSTYLE:OFF
    int getX() {
        return this.x;
    }

    int getY() {
        return this.y;
    }

    void setParent(final JumpPoint parent) {
        this.parent = parent;
    }

    JumpPoint getParent() {
        return this.parent;
    }
    // CHECKSTYLE:ON

    // The hash components of our algorithm wouldn't work if hashing and equals
    // took into account anything other than the position and direction
    @Override
    public int hashCode() {
        return Objects.hash(x, y, dx, dy);
    }


    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final JumpPoint other = (JumpPoint) obj;
        if (this.x != other.x) {
            return false;
        }
        if (this.y != other.y) {
            return false;
        }
        if (this.dx != other.dx) {
            return false;
        }
        if (this.dy != other.dy) {
            return false;
        }
        return true;
    }

    @Override
    public int compareTo(final JumpPoint other) {
        // This will be used by PriorityQueue, so we want it to check just
        // the heuristic value
        return Double.compare(this.f, other.f);
    }
}
