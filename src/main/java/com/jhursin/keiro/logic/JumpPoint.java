package com.jhursin.keiro.logic;

import java.util.Objects;

class JumpPoint implements Comparable<JumpPoint>{
    final int x;
    final int y;
    final int dx;
    final int dy;
    final double dst;
    private final double h;
    private final double f;
    private JumpPoint parent;
    
    JumpPoint(int x, int y, double h, double dst) {
        this.x = x;
        this.y = y;
        this.h = h;
        this.dst = dst;
        this.dx = 0;
        this.dy = 0;
        this.f = dst + h;
    }
    
    JumpPoint(int x, int y, int dx, int dy, double h, double dst) {
        this.x = x;
        this.y = y;
        this.dx = dx;
        this.dy = dy;
        this.h = h;
        this.dst = dst;
        this.f = dst + h;
    }
    
    int getX() {
        return this.x;
    }
    
    int getY() {
        return this.y;
    }
    
    void setParent(JumpPoint parent) {
        this.parent = parent;
    }
    
    JumpPoint getParent() {
        return this.parent;
    }
    
    // The hash components of our algorithm wouldn't work if hashing and equals
    // took into account anything other than the position and direction
    @Override
    public int hashCode() {
        return Objects.hash(x, y, dx, dy);
    }
    
    
    @Override
    public boolean equals(Object obj) {
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
    public int compareTo(JumpPoint other) {
        // This will be used by PriorityQueue, so we want it to check just
        // the heuristic value
        return Double.compare(this.f, other.f);
    }
}