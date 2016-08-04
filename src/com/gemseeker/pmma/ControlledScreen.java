package com.gemseeker.pmma;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Parent;

/**
 *
 * @author RAFIS-FRED
 */
public abstract class ControlledScreen {

    protected ScreenController screenController;
    protected final String name;
    
    public ControlledScreen(String name){
        /*
        name is required, this will be used as key to ScreenController's HashMap
        objects like the map of ControlledScreens and EventHandler<ActionMap>
        on finish actions specific for every screen
        */
        this.name = name;
    }
    
    public void setScreenController(ScreenController controller){
        screenController = controller;
    }
    
    public ScreenController getScreenController(){
        return screenController;
    }
    
    public String getName(){
        return name;
    }
    
    public abstract Parent getContentView();
    
    public abstract EventHandler<ActionEvent> getOnLoadEvent();
}
