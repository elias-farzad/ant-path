package com.mycompany.a1;

/**
 * Abstract base for fixed (non-movable) objects.
 * Blocks location updates after construction per assignment spec.
 */
public abstract class Fixed extends GameObject {
    protected Fixed(int size, float x, float y, int color) {
        super(size, x, y, color);
    }

    /** Fixed objects cannot move; ignore location changes. */
    @Override
    public final void setLocation(float x, float y) {}
}
