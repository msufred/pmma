package com.gemseeker.pmma.controllers;

import com.gemseeker.pmma.ui.*;
import com.gemseeker.pmma.ControlledScreen;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

/**
 *
 * @author u
 */
public class SettingsScreen extends ControlledScreen {
    
    public static final String NAME = "settings";
    
    public SettingsScreen(){
        super(NAME);
        VBox box = new VBox();
        box.setAlignment(Pos.CENTER);
        box.getChildren().add(new Label("Settings screen is not implemented yet."));
        box.getStyleClass().add("paper-white-z1");
        setContentView(box);
    }

}
