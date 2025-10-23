package com.mycompany.a2;

import java.util.Observable;
import java.util.Random;

/**
* Game model (Observable) for Ant-Path.
* Holds a single collection of all GameObjects using a custom iterator.
* Provides command-like methods invoked by GUI Commands.
*/
public class GameWorld extends Observable {
	public static int WORLD_WIDTH = 1000; // updated in Game after show()
	public static int WORLD_HEIGHT = 1000; // updated in Game after show()
	
	private final Random rnd = new Random();
	private final GameObjectCollection objects = new GameObjectCollection();
	
	private int livesRemaining = 3;
	private int clock = 0;
	private boolean sound = false;
	
	public void setWorldSize(int w, int h) { WORLD_WIDTH = Math.max(1, w); WORLD_HEIGHT = Math.max(1, h); }
	public int getWorldWidth() { return WORLD_WIDTH; }
	public int getWorldHeight() { return WORLD_HEIGHT; }
	
	public int getLivesRemaining() { return livesRemaining; }
	public int getClock() { return clock; }
	public boolean isSoundOn() { return sound; }
	public void setSound(boolean on) { sound = on; changed(); }
	
	private void changed() { setChanged(); notifyObservers(this); }
	
	// Initialization
	public void init() {
		// clear existing objects
		objects.clear(); 
		
		// Create flags in order 1..4 at fixed positions within bounds
		add(new Flag(1, 30, 100, 100));
		add(new Flag(2, 30, WORLD_WIDTH - 150, 150));
		add(new Flag(3, 30, 200, WORLD_HEIGHT - 200));
		add(new Flag(4, 30, WORLD_WIDTH - 120, WORLD_HEIGHT - 120));
		
		// Food stations (capacity proportional to size)
		add(new FoodStation(40, rnd.nextInt(Math.max(1, WORLD_WIDTH-40)), rnd.nextInt(Math.max(1, WORLD_HEIGHT-40))));
		add(new FoodStation(60, rnd.nextInt(Math.max(1, WORLD_WIDTH-60)), rnd.nextInt(Math.max(1, WORLD_HEIGHT-60))));
		
		// Spiders
		add(new Spider(40, 300, 300, 90, 5));
		add(new Spider(50, WORLD_WIDTH-200, WORLD_HEIGHT-200, 225, 6));
		
		// Ant singleton at flag 1
		Flag f1 = getFlag(1);
		float ax = f1 != null ? f1.getX() : 50f;
		float ay = f1 != null ? f1.getY() : 50f;
		Ant ant = Ant.getInstance(ax, ay);
		ant.reset(ax, ay);
		add(ant);
		
		changed();
	}
	
	private void add(GameObject go) { objects.add(go); }
	
	// Helpers
	public Ant getAnt() {
		IIterator it = objects.getIterator();
		while (it.hasNext()) {
			Object o = it.getNext();
			if (o instanceof Ant) return (Ant)o;
		}
		return null;
	}

	public Flag getFlag(int n) {
		IIterator it = objects.getIterator();
		while (it.hasNext()) {
			Object o = it.getNext();
			if (o instanceof Flag && ((Flag)o).getSequenceNumber() == n) return (Flag)o;
		}
		return null;
	}
	
	private void loseLifeAndReinitIfAnyLeft() {
	    livesRemaining -= 1;
	    if (livesRemaining <= 0) {
	        System.out.println("Game over, you failed!");
	        System.exit(0);
	    } else {
	        // rebuild the world
	        init();
	        changed();
	    }
	}
	
	// Commands from Controllers
	public void accelerate() {
		Ant a = getAnt();
		if (a != null) { a.setSpeed(a.getSpeed() + 2); changed(); }
	}
	public void brake() {
		Ant a = getAnt();
		if (a != null) { a.setSpeed(Math.max(0, a.getSpeed() - 2)); changed(); }
	}
	public void turnLeft() {
		Ant a = getAnt();
		if (a != null) { a.setHeading((a.getHeading() + 315) % 360); changed(); }
	}
	public void turnRight() {
		Ant a = getAnt();
		if (a != null) { a.setHeading((a.getHeading() + 45) % 360); changed(); }
	}

	public void collideFlag(int n) {
		Ant a = getAnt();
		if (a != null) {
			if (n == a.getLastFlagReached() + 1) {
				a.setLastFlagReached(n);
				if (n >= 4) {
					System.out.println("You win! Total time: " + clock);
					System.exit(0);
				}
			} else {
				System.out.println("Invalid flag order; must reach " + (a.getLastFlagReached()+1));
			}
			changed();
		}
	}

	public void collideFoodStation() {
		Ant a = getAnt();
		if (a != null) {
			FoodStation target = null;
			IIterator it = objects.getIterator();
			while (it.hasNext()) {
				Object o = it.getNext();
				if (o instanceof FoodStation && ((FoodStation)o).getCapacity() > 0) { target = (FoodStation)o; break; }
			}
			if (target != null) {
				int gained = target.getCapacity();
				a.setFoodLevel(a.getFoodLevel() + gained);
				target.setCapacity(0);
				target.fadeWhenEmpty();
				// spawn a new station
				add(new FoodStation(30 + rnd.nextInt(40), rnd.nextInt(Math.max(1, WORLD_WIDTH-60)), rnd.nextInt(Math.max(1, WORLD_HEIGHT-60))));
			}
			changed();
		}
	}

	public void collideSpider() {
	    Ant a = getAnt();
	    if (a != null) {
	        a.degradeHealthByOneAndEnforceSpeedCap();
	        if (a.getHealthLevel() == 0) {
	            loseLifeAndReinitIfAnyLeft();
	            return;
	        }
	        changed();
	    }
	}

	public void tweakConsumptionRandomly() {
		Ant a = getAnt();
		if (a != null) { a.setFoodConsumption(a.getFoodConsumptionRate() + (rnd.nextBoolean()?1:-1)); changed(); }
	}

	public void tick() {
	    // If the ant is already immobile at the start of the tick,
	    // lose a life and re-init right away
	    Ant a = getAnt();
	    if (a != null && (a.getFoodLevel() <= 0 || a.getHealthLevel() == 0)) {
	        loseLifeAndReinitIfAnyLeft();
	        return;
	    }

	    // 1) Move all movables
	    IIterator it = objects.getIterator();
	    while (it.hasNext()) {
	        Object o = it.getNext();
	        if (o instanceof Movable) {
	            ((Movable)o).moveOneTick();
	        }
	    }

	    // 2) Ant food consumption
	    a = getAnt();
	    if (a != null) {
	        int newFood = Math.max(0, a.getFoodLevel() - a.getFoodConsumptionRate());
	        a.setFoodLevel(newFood);

	        // 3) If the ant became immobile during this tick, lose a life and re-init immediately.
	        if (a.getFoodLevel() == 0 || a.getHealthLevel() == 0) {
	            loseLifeAndReinitIfAnyLeft();
	            return;
	        }
	    }

	    // 4) Increment clock and notify observers
	    clock += 1;
	    changed();
	}
	
	// Map and state printouts for MapView
	public void display() {
		System.out.println("Lives: " + livesRemaining + ", time: " + clock + ", sound: " + (sound?"ON":"OFF"));
		Ant a = getAnt();
		if (a != null) {
			System.out.println("Last flag reached: " + a.getLastFlagReached());
			System.out.println("Ant food level: " + a.getFoodLevel());
			System.out.println("Ant health level: " + a.getHealthLevel());
		}
	}

	public void map() {
		IIterator it2 = objects.getIterator();
		while (it2.hasNext()) {
			Object o = it2.getNext();
			System.out.println(o.toString());
		}
	}
}