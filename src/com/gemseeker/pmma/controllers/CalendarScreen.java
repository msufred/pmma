package com.gemseeker.pmma.controllers;

import com.gemseeker.pmma.ControlledScreen;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

/**
 *
 * @author Gem Seeker
 */
public class CalendarScreen extends ControlledScreen {

    public static final String NAME = "CalendarScreen";
    
    public CalendarScreen(){
        super(NAME);
        VBox box = new VBox();
        box.setAlignment(Pos.CENTER);
        box.getChildren().add(new Label("Calendar screen is not implemented yet."));
        box.getStyleClass().add("background");
        setContentView(box);
    }
}
