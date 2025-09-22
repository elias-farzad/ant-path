package com.mycompany.a1;

import com.codename1.charts.util.ColorUtil;

/**
 * Fixed food source. Capacity is proportional to size and decreases when consumed.
 */
public class FoodStation extends Fixed {
    private int capacity;

    public FoodStation(int size, float x, float y) {
        super(size, x, y, ColorUtil.rgb(0, 255, 0)); // green
        this.capacity = size; // proportional
    }

    public int getCapacity() { return capacity; }
    public void setCapacity(int capacity) { this.capacity = Math.max(0, capacity); }

    /** Fade color when emptied for convenience. */
    public void fadeWhenEmpty() {
        if (capacity == 0) {
            setColor(ColorUtil.rgb(200, 255, 200));
        }
    }

    @Override
    public String toString() {
        return "FoodStation: " + super.toString() + "capacity=" + capacity;
    }
}
