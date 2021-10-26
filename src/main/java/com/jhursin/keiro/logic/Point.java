package com.jhursin.keiro.logic;

import java.util.Objects;

/**
 * A Point class that holds an X, Y and priority component.
 */
class Point implements Comparable<Point> {
    // CHECKSTYLE:OFF
    final int x;
    final int y;
    double prio;
    // CHECKSTYLE:ON

    /**
     * Create a new Point.
     * @param x X coordinate of this Point
     * @param y Y coordinate of this Point
     * @param prio Priority value of this point
     */
    Point(final int x, final int y, final double prio) {
        this.x = x;
        this.y = y;
        this.prio = prio;
    }

    /**
     * Create a new Point with a priority of 0.
     * @param x X coordinate of this Point
     * @param y Y coordinate of this Point
     */
    Point(final int x, final int y) {
        this.x = x;
        this.y = y;
        this.prio = 0;
    }

    /**
     * Set the priority for this Point.
     * @param prio New priority of this point
     */
    void setPrio(final double prio) {
        this.prio = prio;
    }

    @Override
    public int hashCode() {
        return Objects.hash(x, y);
    }

    @Override
    public boolean equals(final Object other) {
        if (other == null || other.getClass() != getClass()) {
            return false;
        }
        Point p = (Point) other;
        return this.x == p.x && this.y == p.y;
    }

    @Override
    public int compareTo(final Point p) {
        return Double.compare(this.prio, p.prio);
    }
}
