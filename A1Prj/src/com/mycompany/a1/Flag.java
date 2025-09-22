package com.mycompany.a1;

import com.codename1.charts.util.ColorUtil;

/**
 * Numbered waypoint on the path. Fixed location and color.
 */
public class Flag extends Fixed {
    private final int sequenceNumber;

    public Flag(int sequenceNumber, int size, float x, float y) {
        super(size, x, y, ColorUtil.rgb(0, 0, 255)); // blue
        this.sequenceNumber = sequenceNumber;
    }

    public int getSequenceNumber() { return sequenceNumber; }

    /** Flags cannot change color once created. */
    @Override
    public final void setColor(int color) {}

    @Override
    public String toString() {
        return "Flag: " + super.toString() + "seqNum=" + sequenceNumber;
    }
}
