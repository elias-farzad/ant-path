package com.mycompany.a1;

import java.util.Random;
import com.codename1.charts.util.ColorUtil;

/**
 * Enemy spider. Movable, not a food consumer. Heading jitters by +/-5 each tick.
 * Bounces off world borders by reflecting heading.
 */
public class Spider extends Movable {
    private static final Random RND = new Random();

    public Spider(int size, float x, float y, int heading, int speed) {
        super(size, x, y, ColorUtil.rgb(0, 0, 0), heading, speed, 0);
    }

    // Spiders cannot change color once created. Do nothing
    @Override
    public final void setColor(int color) {}

    // Non-food consumer: Do nothing
    @Override
    public final void setFoodLevel(int fl) {}

    @Override
    public void moveOneTick() {
        // Apply small random perturbation to heading (-5..+5)
        int delta = RND.nextInt(11) - 5;
        setHeading(getHeading() + delta);

        double theta = Math.toRadians(90 - getHeading());
        double dx = Math.cos(theta) * getSpeed();
        double dy = Math.sin(theta) * getSpeed();

        float nx = (float)(getX() + dx);
        float ny = (float)(getY() + dy);

        // If out of bounds, reflect heading and recompute step
        boolean bounced = false;
        if (nx < 0f || nx > GameWorld.WORLD_WIDTH) {
            setHeading(180 - getHeading());
            bounced = true;
        }
        if (ny < 0f || ny > GameWorld.WORLD_HEIGHT) {
            setHeading(360 - getHeading());
            bounced = true;
        }
        if (bounced) {
            theta = Math.toRadians(90 - getHeading());
            dx = Math.cos(theta) * getSpeed();
            dy = Math.sin(theta) * getSpeed();
            nx = (float)(getX() + dx);
            ny = (float)(getY() + dy);
        }

        // Clamp inside final
        nx = Math.max(0f, Math.min(nx, GameWorld.WORLD_WIDTH));
        ny = Math.max(0f, Math.min(ny, GameWorld.WORLD_HEIGHT));
        setLocation(nx, ny);
    }

    @Override
    public String toString() {
        return "Spider: " + super.toString();
    }
}
