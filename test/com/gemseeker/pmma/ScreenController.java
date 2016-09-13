package com.gemseeker.pmma;

import java.util.HashMap;
import javafx.animation.Transition;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.layout.StackPane;

/**
 * Main screen manager of the application.
 *
 * @author RAFIS-FRED
 */
public abstract class ScreenController {

    private StackPane container;
    private HashMap<String, ControlledScreen> screens;
    private ScreenBackStack backStack;
    private ControlledScreen currentScreen;

    public ScreenController() {
        this(null);
    }

    public ScreenController(StackPane container){
        this.container = container;
        screens = new HashMap<>();
        backStack = new ScreenBackStack();
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
    
    public final void loadScreens(ControlledScreen...screens){
        for(ControlledScreen screen : screens){
            loadScreen(screen);
        }
    }

    protected final void setScreenContainer(StackPane container) {
        this.container = container;
    }
    
    public StackPane getScreenContainer(){
        return container;
    }

    public void setScreen(String key){
        ControlledScreen screen = screens.get(key);
        if(screen == null || currentScreen == screen){
            return;
        }
        
        Node view = screen.contentView;
        if(currentScreen != null){
            currentScreen.onPause();
            container.getChildren().remove(currentScreen.contentView);
        }
        container.getChildren().add(view);
        screen.onResume();
        currentScreen = screen;
    }
    
    public ControlledScreen getScreen(String key){
        return screens.get(key);
    }
    
    public HashMap<String, ControlledScreen> getScreens(){
        return screens;
    }

    public ControlledScreen getCurrentScreen(){
        return currentScreen;
    }
    
    public void setCurrentScreen(ControlledScreen screen){
        currentScreen = screen;
    }
    
    public ScreenBackStack getBackStack(){
        return backStack;
    }
}
