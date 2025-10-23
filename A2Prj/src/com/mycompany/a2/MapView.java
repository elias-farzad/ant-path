package com.mycompany.a2;

import com.codename1.ui.Container;
import com.codename1.ui.layouts.BorderLayout;
import com.codename1.ui.plaf.Border;
import java.util.Observer;
import java.util.Observable;

/**
 * Text map view (still prints to console in A2). Shows a red border.
 * Observer of GameWorld: reprints the map whenever the model changes.
 */
public class MapView extends Container implements Observer {
    public MapView() {
        setLayout(new BorderLayout());
        getAllStyles().setBorder(Border.createLineBorder(2, 0xff0000)); // red
    }

    // Java 5 note: no @Override here (interface method)
    public void update(Observable observable, Object data) {
        if (observable instanceof GameWorld) {
            GameWorld gw = (GameWorld)observable;
            gw.map();
        }
    }
}
