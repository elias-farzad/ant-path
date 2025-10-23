package com.mycompany.a2;

import com.codename1.ui.Container;
import com.codename1.ui.Label;
import com.codename1.ui.layouts.BoxLayout;
import java.util.Observer;
import java.util.Observable;

/**
 * Score/status labels across the top.
 * Observer of GameWorld: updates labels when the model changes.
 */
public class ScoreView extends Container implements Observer {
    private final Label lives = new Label("Lives: 0");
    private final Label time = new Label("Time: 0");
    private final Label lastFlag = new Label("Last Flag: 1");
    private final Label food = new Label("Food: 0");
    private final Label health = new Label("Health: 0");
    private final Label sound = new Label("Sound: OFF");

    public ScoreView() {
        setLayout(new BoxLayout(BoxLayout.X_AXIS));
        addAll(lives, time, lastFlag, food, health, sound);
    }

    public void update(Observable observable, Object data) {
        if (observable instanceof GameWorld) {
            GameWorld gw = (GameWorld)observable;
            Ant a = gw.getAnt();
            lives.setText("Lives: " + gw.getLivesRemaining());
            time.setText("Time: " + gw.getClock());
            if (a != null) {
                lastFlag.setText("Last Flag: " + a.getLastFlagReached());
                food.setText("Food: " + a.getFoodLevel());
                health.setText("Health: " + a.getHealthLevel());
            }
            sound.setText("Sound: " + (gw.isSoundOn()?"ON":"OFF"));
            revalidate();
        }
    }
}
