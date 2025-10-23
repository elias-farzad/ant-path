package com.mycompany.a2;

/**
 * Abstract base for fixed (non-movable) objects.
 * Blocks location updates after construction.
 */
public abstract class Fixed extends GameObject {
    protected Fixed(int size, float x, float y, int color) {
        super(size, x, y, color);
    }

    // Do nothing
    @Override
    public final void setLocation(float x, float y) {}
}
