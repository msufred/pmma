package com.gemseeker.pmma;

import java.util.HashMap;
import javafx.animation.Transition;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.layout.StackPane;

/**
 * Main screen manager of the application.
 *
 * @author RAFIS-FRED
 */
public abstract class ScreenController {

    protected StackPane container;
    protected HashMap<String, ControlledScreen> screens;
    protected HashMap<String, EventHandler<ActionEvent>> onFinishEvents;
    protected HashMap<String, Transition> transitions;
    protected ScreenBackStack backStack;
    
    private EventHandler<ActionEvent> onSetScreenEvent = null;

    public ScreenController() {
        screens = new HashMap<>();
        backStack = new ScreenBackStack();
        onFinishEvents = new HashMap<>();
        transitions = new HashMap<>();
    }

    /**
     * Loads the ControlledScreen and it's associated on-load action.
     *
     * @param screen
     */
    protected final void loadScreen(ControlledScreen screen) {
        screen.setScreenController(this);
        screen.onStart();
        screens.put(screen.name, screen);
    }

    protected final void setScreenContainer(StackPane container) {
        this.container = container;
    }
    
    public StackPane getScreenContainer(){
        return container;
    }

    public abstract void setScreen(String key);
    
    public ControlledScreen getScreen(String key){
        return screens.get(key);
    }

    public abstract ControlledScreen getCurrentScreen();

    public abstract ScreenController onBackPressed();
    
    public ScreenBackStack getBackStack(){
        return backStack;
    }
    
    public void setOnSetScreenEvent(EventHandler<ActionEvent> onSetScreen){
        onSetScreenEvent = onSetScreen;
    }
    
    public EventHandler<ActionEvent> getOnSetScreenEvent(){
        return onSetScreenEvent;
    }

}
