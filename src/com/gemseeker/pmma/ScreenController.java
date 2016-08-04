package com.gemseeker.pmma;

import java.util.HashMap;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.layout.StackPane;

/**
 * <p/>
 * Main screen manager of the application.
 *
 * @author RAFIS-FRED
 */
public abstract class ScreenController {

    protected StackPane container;
    protected HashMap<String, ControlledScreen> screens;
    protected ScreenBackStack backStack;
    protected HashMap<String, EventHandler<ActionEvent>> onFinishEvents;

    public ScreenController() {
        screens = new HashMap<>();
        backStack = new ScreenBackStack();
        onFinishEvents = new HashMap<>();
    }

    /**
     * Loads the ControlledScreen and it's associated on-load action.
     *
     * @param screen
     */
    protected void loadScreen(ControlledScreen screen) {
        screen.setScreenController(this);
        onFinishEvents.put(screen.name, screen.getOnLoadEvent());
        screens.put(screen.name, screen);
    }

    protected void setScreenContainer(StackPane container) {
        this.container = container;
    }

    public abstract void setScreen(String key);

    public abstract ControlledScreen getCurrentScreen();

    public abstract ScreenController onBackPressed();

}
