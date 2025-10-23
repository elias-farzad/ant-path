package com.mycompany.a2;

import com.codename1.charts.util.ColorUtil;

/**
* The player's ant. Movable and a food consumer.
* Enforces:
* - max speed never exceeded
* - dynamic speed cap based on health (0..10 -> 0%..100% of maxSpeed)
* - starts at flag #1, heading 0, non-zero initial speed set by GameWorld
*
* A2 change: make Ant a Singleton (only one Ant in the world). Use getInstance().
*/
public class Ant extends Movable implements IFoodie {
	// === A2 Singleton support ===
	private static Ant instance; // single instance
	
	private final int maximumSpeed;
	private int foodConsumptionRate; // must stay positive
	private int healthLevel = 10; // starts at 10
	private int lastFlagReached = 1; // starts at flag #1
	
	/**
	* Private constructor (Singleton). Use getInstance(x,y).
	*/
	private Ant(int size, float x, float y, int heading, int speed, int maximumSpeed, int foodConsumptionRate) {
		super(size, x, y, ColorUtil.rgb(255, 0, 0), heading, speed, 100);
		this.maximumSpeed = Math.max(1, maximumSpeed);
		this.foodConsumptionRate = Math.max(1, foodConsumptionRate);
	}
	
	/**
	* Create (first call) or return (subsequent) the single Ant at the given location.
	*/
	public static Ant getInstance(float x, float y) {
		if (instance == null) {
			instance = new Ant(40, x, y, 0, 5, 50, 2);
		}
		return instance;
	}
	
	/** Reset state after life loss; keeps singleton identity. */
	public void reset(float x, float y) {
		setLocation(x, y);
		setHeading(0);
		setSpeed(5);
		setFoodLevel(100);
		healthLevel = 10;
		lastFlagReached = 1;
		setColor(ColorUtil.rgb(255, 0, 0));
	}
	
	// Getters
	public int getMaximumSpeed() { return maximumSpeed; }
	public int getFoodConsumptionRate() { return foodConsumptionRate; }
	public int getHealthLevel() { return healthLevel; }
	public int getLastFlagReached() { return lastFlagReached; }
	
	// Use IFoodie
	@Override
	public void setFoodConsumption(int newRate) {
		// If result would be <= 0, set to old + 1 instead
		if (newRate <= 0) {
			this.foodConsumptionRate = this.foodConsumptionRate + 1;
		} else {
			this.foodConsumptionRate = newRate;
		}
	}
	
	// Setter with constraints
	public void setLastFlagReached(int seq) { this.lastFlagReached = seq; }
	
	public void degradeHealthByOneAndEnforceSpeedCap() {
		if (healthLevel > 0) healthLevel -= 1;
			enforceDynamicSpeedCap();
			// Fade ant's color for damage
			int r = Math.min(255, 255);
			int g = Math.min(255, (int)(255 - (healthLevel * 10)));
			int b = Math.min(255, (int)(255 - (healthLevel * 10)));
			setColor(ColorUtil.rgb(r, Math.max(0, g), Math.max(0, b)));
		}

	/** Dynamic cap: health 10 -> 100% max, health 0 -> 0%. */
	private int currentSpeedCap() {
		return (int)Math.floor((healthLevel / 10.0) * maximumSpeed);
	}

	/** Enforce cap after any health change. */
	private void enforceDynamicSpeedCap() {
		if (getSpeed() > currentSpeedCap()) {
			super.setSpeed(currentSpeedCap());
		}
	}

	@Override
	public void setSpeed(int speed) {
		int requested = Math.max(0, Math.min(speed, maximumSpeed));
		int cap = currentSpeedCap();
		super.setSpeed(Math.min(requested, cap));
	}

	@Override
	public String toString() {
		return "Ant: " + super.toString()
		+ "maxSpeed=" + maximumSpeed + " "
		+ "foodConsumptionRate=" + foodConsumptionRate + " "
		+ "lastFlagReached=" + lastFlagReached + " "
		+ "healthLevel=" + healthLevel;
	}
}
