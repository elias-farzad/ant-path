package com.mycompany.a1;

/**
 * Abstract base for movable objects.
 * Adds heading (degrees), speed, and foodLevel fields.
 */
public abstract class Movable extends GameObject {
    /** Heading in compass degrees: 0=N, 90=E, 180=S, 270=W. */
    private int heading;
    /** Speed in arbitrary units per tick. */
    private int speed;
    /** Current food level; may be blocked for non-food consumers. */
    private int foodLevel;

    protected Movable(int size, float x, float y, int color, int heading, int speed, int initialFoodLevel) {
        super(size, x, y, color);
        this.heading = heading;
        this.speed = Math.max(0, speed);
        this.foodLevel = Math.max(0, initialFoodLevel);
    }

    // Getters
    public int getHeading() { return heading; }
    public int getSpeed() { return speed; }
    public int getFoodLevel() { return foodLevel; }

    // Setters
    public void setHeading(int heading) { this.heading = ((heading % 360) + 360) % 360; }
    public void setSpeed(int speed) { this.speed = Math.max(0, speed); }
    public void setFoodLevel(int fl) { this.foodLevel = Math.max(0, fl); }

    /**
     * Update location one tick based on heading and speed, clamped to world bounds.
     * Uses: deltaX = cos(theta)*speed; deltaY = sin(theta)*speed; where theta = 90 - heading.
     */
    public void moveOneTick() {
        // If out of food, speed is forced to zero
        if (foodLevel <= 0) {
            setSpeed(0);
        }

        double theta = Math.toRadians(90 - getHeading());
        double dx = Math.cos(theta) * getSpeed();
        double dy = Math.sin(theta) * getSpeed();

        float nx = (float)(getX() + dx);
        float ny = (float)(getY() + dy);

        // Clamp so centers remain inside world
        nx = Math.max(0f, Math.min(nx, GameWorld.WORLD_WIDTH));
        ny = Math.max(0f, Math.min(ny, GameWorld.WORLD_HEIGHT));

        setLocation(nx, ny);
    }

    @Override
    public String toString() {
        return super.toString() + "heading=" + heading + " speed=" + speed + " ";
    }
}
