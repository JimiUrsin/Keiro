package com.jhursin.keiro.logic;

import java.util.Objects;

class Point implements Comparable {
    int x;
    int y;
    double prio;

    Point(final int x, final int y, double prio) {
        this.x = x;
        this.y = y;
        this.prio = prio;
    }

    Point(final int newx, final int newy) {
        this.x = newx;
        this.y = newy;
        this.prio = 0;
    }

    void setPrio(double newprio) {
        this.prio = newprio;
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(x, y);
    }

    @Override
    public boolean equals(Object other) {
        if (other == null || other.getClass() != getClass()) {
            return false;
        }
        Point p = (Point) other;
        return this.x == p.x && this.y == p.y;
    }

    @Override
    public int compareTo(Object o) {
        Point other = (Point) o;
        return Double.compare(this.prio, other.prio);
    }
}