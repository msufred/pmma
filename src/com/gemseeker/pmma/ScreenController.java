package com.gemseeker.pmma;

import java.util.HashMap;
import javafx.scene.layout.StackPane;

/**
 *
 * @author RAFIS-FRED
 */
public abstract class ScreenController {

    protected StackPane container;
    protected HashMap<String, ControlledScreen> screens;
    protected ScreenBackStack backStack;
    
    public ScreenController(){
        screens = new HashMap<>();
        backStack = new ScreenBackStack();
    }
    
    protected void loadScreen(String key, ControlledScreen screen){
        screen.setScreenController(this);
        screens.put(key, screen);
    }
    
    protected void setScreenContainer(StackPane container){
        this.container = container;
    }
    
    public abstract void setScreen(String key);
    
    public abstract ControlledScreen getCurrentScreen();
    
    public abstract ScreenController onBackPressed();
    
}
