package com.mycompany.a1;

import java.util.Random;
import java.util.Vector;

/**
 * Game model: holds world state and objects and exposes operations invoked by controller.
 * Keeps track of lives and clock, and implements all commands.
 */
public class GameWorld {
    public static final int WORLD_WIDTH = 1000;
    public static final int WORLD_HEIGHT = 1000;

    private final Vector<GameObject> objects = new Vector<>();
    private int livesRemaining = 3;
    private int clock = 0;
    private int maxFlagNumber = 4; // recomputed in init()

    /**
     * Create initial world: >=4 flags, 1 ant at flag #1 (heading 0, nonzero speed),
     * >=2 spiders (random loc/size/heading/speed), >=2 food stations (random loc/size).
     */
    public void init() {
        objects.clear();

        // Flags define the path (these positions make easy testing)
        Flag f1 = new Flag(1, 10, 200f, 200f);
        Flag f2 = new Flag(2, 10, 200f, 800f);
        Flag f3 = new Flag(3, 10, 700f, 800f);
        Flag f4 = new Flag(4, 10, 900f, 400f);
        objects.add(f1); objects.add(f2); objects.add(f3); objects.add(f4);
        maxFlagNumber = 4;

        // Ant: at flag 1, heading 0, initial speed 5, max speed 50, size 2
        Ant ant = new Ant(40, f1.getX(), f1.getY(), 0, 5, 50, 2);
        objects.add(ant);

        Random rnd = new Random();

        // 2 spiders with random attributes
        for (int i = 0; i < 2; i++) {
            int size = 10 + rnd.nextInt(41 - 10); // 10..40
            float x = randX(rnd);
            float y = randY(rnd);
            int heading = randHeading(rnd);
            int speed = randSpeedSpider(rnd);
            objects.add(new Spider(size, x, y, heading, speed));
        }

        // 2 food stations with random sizes and locations
        for (int i = 0; i < 2; i++) {
            int size = 10 + rnd.nextInt(41 - 10); // 10..40
            objects.add(new FoodStation(size, randX(rnd), randY(rnd)));
        }
    }

    // Helpers
    private float randX(Random rnd) { return rnd.nextFloat() * WORLD_WIDTH; }
    private float randY(Random rnd) { return rnd.nextFloat() * WORLD_HEIGHT; }
    private int randHeading(Random rnd) { return rnd.nextInt(360); }
    private int randSpeedSpider(Random rnd) { return 5 + rnd.nextInt(6); } // 5..10

    public int getLivesRemaining() { return livesRemaining; }
    public int getClock() { return clock; }
    public Vector<GameObject> getObjects() { return objects; }

    public Ant getAnt() {
        for (GameObject go : objects) if (go instanceof Ant) return (Ant) go;
        return null;
    }

    // Commands

    /** 'a' Accelerate ant by a small amount, respecting max speed and health cap. */
    public void accelerate() {
        Ant a = getAnt(); if (a == null) return;
        a.setSpeed(a.getSpeed() + 1);
        System.out.println("Ant accelerated.");
    }

    /** 'b' Brake the ant (min 0). */
    public void brake() {
        Ant a = getAnt(); if (a == null) return;
        a.setSpeed(Math.max(0, a.getSpeed() - 1));
        System.out.println("Brake applied.");
    }

    /** 'l' Turn left by 5 degrees. */
    public void turnLeft() {
        Ant a = getAnt(); if (a == null) return;
        a.setHeading(a.getHeading() - 5);
        System.out.println("Ant turned left.");
    }

    /** 'r' Turn right by 5 degrees. */
    public void turnRight() {
        Ant a = getAnt(); if (a == null) return;
        a.setHeading(a.getHeading() + 5);
        System.out.println("Ant turned right.");
    }

    /** 'c' Change food consumption rate by random +/- x, enforcing positive final value. */
    public void tweakConsumptionRandomly() {
        Ant a = getAnt(); if (a == null) return;
        int x = 3; // reasonable positive value
        Random rand = new Random();
        int delta = rand.nextInt(2 * x + 1) - x; // -x..+x
        int candidate = a.getFoodConsumptionRate() + delta;
        if (candidate <= 0) candidate = a.getFoodConsumptionRate() + 1;
        a.setFoodConsumption(candidate);
        System.out.println("Ant food consumption rate adjusted by " + delta + " -> " + candidate + ".");
    }

    /**
     * '1'..'9' Pretend collision with flag x.
     * Only counts if x == lastFlagReached + 1.
     */
    public void collideFlag(int x) {
        if (x < 1 || x > 9) { System.out.println("Invalid flag number."); return; }
        Ant a = getAnt(); if (a == null) return;
        if (x == a.getLastFlagReached() + 1) {
            a.setLastFlagReached(x);
            System.out.println("Ant reached flag #" + x + ".");
            if (x > maxFlagNumber) maxFlagNumber = x;
            // If reached last flag, game over (win)
            if (x == maxFlagNumber) {
                System.out.println("Game over, you win! Total time: " + clock);
                System.exit(0);
            }
        } else {
            System.out.println("Ignored out-of-order flag #" + x + ".");
        }
    }

    /**
     * 'f' Pretend collision with a (random) non-empty food station:
     * Increase ant's food by capacity, set station to 0, fade it, and add a new random station.
     */
    public void collideFoodStation() {
        Ant a = getAnt(); if (a == null) return;
        FoodStation fs = null;
        for (GameObject go : objects) {
            if (go instanceof FoodStation) {
                FoodStation cand = (FoodStation)go;
                if (cand.getCapacity() > 0) { fs = cand; break; }
            }
        }
        if (fs == null) { System.out.println("No non-empty food station found."); return; }

        a.setFoodLevel(a.getFoodLevel() + fs.getCapacity());
        fs.setCapacity(0);
        fs.fadeWhenEmpty();
        // Add a new random food station
        Random rnd = new Random();
        objects.add(new FoodStation(10 + rnd.nextInt(41 - 10), randX(rnd), randY(rnd)));

        System.out.println("Ant refueled from food station.");
    }

    /**
     * 'g' Pretend a spider collided with the ant:
     * Decrease ant health by one, fade ant color, and ensure speed respects new cap.
     */
    public void collideSpider() {
        Ant a = getAnt(); if (a == null) return;
        a.degradeHealthByOneAndEnforceSpeedCap();
        System.out.println("Spider collision: health now " + a.getHealthLevel() + ".");
    }

    /**
     * 't' Advance one clock tick:
     *  1) spiders update heading (handled in their move)
     *  2) all movables move
     *  3) ant food level decreases by its consumption rate
     *  4) clock increments by one
     * Also checks for loss-of-life conditions (food=0 or health=0).
     */
    public void tick() {
        // 1 & 2: move all movables
        for (GameObject go : objects) {
            if (go instanceof Movable) {
                ((Movable)go).moveOneTick();
            }
        }

        // 3: ant food drop
        Ant a = getAnt(); if (a != null) {
            int newFood = Math.max(0, a.getFoodLevel() - a.getFoodConsumptionRate());
            a.setFoodLevel(newFood);
            if (newFood == 0 || a.getHealthLevel() == 0) {
                // Lose a life: re-init world immediately without resetting clock
                livesRemaining -= 1;
                if (livesRemaining <= 0) {
                    System.out.println("Game over, you failed!");
                    System.exit(0);
                } else {
                    System.out.println("Life lost. Lives remaining: " + livesRemaining + ". Reinitializing world...");
                    init(); // keep clock as-is
                }
            }
        }

        // 4: increment clock
        clock += 1;
        System.out.println("Clock ticked. t=" + clock);
    }

    /** 'd' Display current game/ant state values. */
    public void display() {
        Ant a = getAnt();
        System.out.println("Lives: " + livesRemaining);
        System.out.println("Clock: " + clock);
        if (a != null) {
            System.out.println("Last flag reached: " + a.getLastFlagReached());
            System.out.println("Ant food level: " + a.getFoodLevel());
            System.out.println("Ant health level: " + a.getHealthLevel());
        }
    }

    /** 'm' Print the text map by calling each object's toString. */
    public void map() {
        for (GameObject go : objects) {
            System.out.println(go.toString());
        }
    }
}
