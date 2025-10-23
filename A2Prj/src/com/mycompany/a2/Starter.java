package com.mycompany.a2;

import com.codename1.ui.Display;
import com.codename1.ui.Form;
import com.codename1.ui.Dialog;
import com.codename1.ui.plaf.UIManager;
import com.codename1.ui.util.Resources;
import com.codename1.io.Log;

public class Starter {
    private Form current;
    private Resources theme;

    public void init(Object context) {
        try {
            theme = UIManager.initFirstTheme("/theme");
        } catch (Exception e) {
            Log.e(e);
        }
        // Optional: enable crash protection logs
        Log.bindCrashProtection(true);
    }

    public void start() {
        if (current != null) {
            current.show();
            return;
        }
        // Launch controller (it builds MVC + shows itself)
        new Game(); // Game extends Form and calls show()
    }

    public void stop() {
        current = Display.getInstance().getCurrent();
        if (current instanceof Dialog) {
            ((Dialog) current).dispose();
            current = Display.getInstance().getCurrent();
        }
    }

    public void destroy() {
        // no-op
    }
}
