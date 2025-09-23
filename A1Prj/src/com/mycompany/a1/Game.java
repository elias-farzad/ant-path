package com.mycompany.a1;

import com.codename1.ui.Form;
import com.codename1.ui.Label;
import com.codename1.ui.TextField;
import com.codename1.ui.events.ActionEvent;
import com.codename1.ui.events.ActionListener;

/**
 * Controller for text-mode game.
 * Wires keystroke commands via a TextField to GameWorld operations.
 */
public class Game extends Form {
    private final GameWorld gw;
    private boolean awaitingQuitConfirm = false; // set after 'x' until 'y' or 'n'

    public Game() {
        super("Ant-Path");
        gw = new GameWorld();
        gw.init();
        play();
    }

    /** Set up input field and map keystrokes to world actions. */
    private void play() {
        Label prompt = new Label("Enter a Command:");
        final TextField input = new TextField();
        this.addComponent(prompt);
        this.addComponent(input);
        this.show();

        input.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                String s = input.getText().toString();
                input.clear();
                if (s.length() == 0) return;

                char c = s.charAt(0);

                // If awaiting confirmation, only accept y/n
                if (awaitingQuitConfirm) {
                    if (c == 'y') {
                        System.out.println("Exiting...");
                        System.exit(0);
                    } else if (c == 'n') {
                        awaitingQuitConfirm = false;
                        System.out.println("Exit canceled.");
                    } else {
                        System.out.println("Please enter 'y' or 'n'.");
                    }
                    return;
                }

                switch (c) {
                    case 'a': gw.accelerate(); break;
                    case 'b': gw.brake(); break;
                    case 'l': gw.turnLeft(); break;
                    case 'r': gw.turnRight(); break;
                    case 'c': gw.tweakConsumptionRandomly(); break;
                    case 'f': gw.collideFoodStation(); break;
                    case 'g': gw.collideSpider(); break;
                    case 't': gw.tick(); break;
                    case 'd': gw.display(); break;
                    case 'm': gw.map(); break;
                    case 'x':
                        awaitingQuitConfirm = true;
                        System.out.println("Are you sure you want to quit? (y/n)");
                        break;
                    default:
                        if (c >= '1' && c <= '9') {
                            gw.collideFlag(c - '0');
                        } else {
                            System.out.println("Invalid command: " + c);
                        }
                }
            }
        });
    }
}
