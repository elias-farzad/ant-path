package com.mycompany.a1;

/**
 * Interface for movable food consumers in the game world.
 * Provides a method to change the amount of food consumed each tick.
 */
public interface IFoodie {
    /**
     * Adjust the food consumption rate by setting a new (positive) value.
     * Implementations must ensure the final rate remains strictly positive.
     */
    void setFoodConsumption(int newRate);
}
