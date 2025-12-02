package com.logisim.domain;

import java.util.Objects;

/**
 * Represents a 2D position (x, y coordinates).
 * 
 * @author LogiSim Team
 * @version 1.0
 */
public class Position {
    private double x;
    private double y;

    /**
     * Default constructor.
     */
    public Position() {
        this(0, 0);
    }

    /**
     * Constructor with coordinates.
     * 
     * @param x The x coordinate
     * @param y The y coordinate
     */
    public Position(double x, double y) {
        this.x = x;
        this.y = y;
    }

    /**
     * Gets the x coordinate.
     * 
     * @return The x coordinate
     */
    public double getX() {
        return x;
    }

    /**
     * Sets the x coordinate.
     * 
     * @param x The x coordinate
     */
    public void setX(double x) {
        this.x = x;
    }

    /**
     * Gets the y coordinate.
     * 
     * @return The y coordinate
     */
    public double getY() {
        return y;
    }

    /**
     * Sets the y coordinate.
     * 
     * @param y The y coordinate
     */
    public void setY(double y) {
        this.y = y;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Position position = (Position) o;
        return Double.compare(position.x, x) == 0 && Double.compare(position.y, y) == 0;
    }

    @Override
    public int hashCode() {
        return Objects.hash(x, y);
    }

    @Override
    public String toString() {
        return "Position{" +
                "x=" + x +
                ", y=" + y +
                '}';
    }
}



