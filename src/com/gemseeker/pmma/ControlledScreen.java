package com.gemseeker.pmma;

import javafx.scene.Parent;

/**
 *
 * @author RAFIS-FRED
 */
public abstract class ControlledScreen {

    protected ScreenController screenController;
    
    public void setScreenController(ScreenController controller){
        screenController = controller;
    }
    
    public ScreenController getScreenController(){
        return screenController;
    }
    
    public abstract Parent getContentView();
}
