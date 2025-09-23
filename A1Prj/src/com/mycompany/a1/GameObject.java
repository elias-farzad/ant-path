package com.mycompany.a1;

import com.codename1.charts.models.Point;
import com.codename1.charts.util.ColorUtil;

/**
 * Base abstract class for all game objects.
 * Common attributes: immutable size (int), location (Point), and color (int).
 * By default, subclasses may change location and color unless specifically restricted.
 */
public abstract class GameObject {
    /** Length of the bounding square; immutable after construction. */
    private final int size;
    /** Center location within the 1000x1000 world. */
    private Point location;
    /** RGB color as an int; changeable unless subclass forbids it. */
    private int color;

    /**
     * Construct a game object with size, location, and color.
     */
    protected GameObject(int size, float x, float y, int color) {
        this.size = size;
        this.location = new Point(x, y);
        this.color = color;
    }

    // Getters
    /** @return the immutable size (bounding square length). */
    public int getSize() { return size; }
    /** @return the X coordinate of the center. */
    public float getX() { return location.getX(); }
    /** @return the Y coordinate of the center. */
    public float getY() { return location.getY(); }
    /** @return the current location point (reference). */
    public Point getLocation() { return location; }
    /** @return current color as RGB int. */
    public int getColor() { return color; }

    // Setters
    public void setLocation(float x, float y) {
        this.location.setX(x);
        this.location.setY(y);
    }
    public void setColor(int color) { this.color = color; }

    @Override
    public String toString() {
        // CN1 trick to show one digit after decimal using Math.round
        float rx = Math.round(getX() * 10f) / 10f;
        float ry = Math.round(getY() * 10f) / 10f;
        return "loc=" + rx + "," + ry
            + " color=[" + ColorUtil.red(color) + ","
                         + ColorUtil.green(color) + ","
                         + ColorUtil.blue(color) + "]"
            + " size=" + size + " ";
    }
}
