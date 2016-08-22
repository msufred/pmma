package com.gemseeker.pmma.controllers;

import com.gemseeker.pmma.ControlledScreen;
import com.gemseeker.pmma.data.Project;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

/**
 *
 * @author Gem Seeker
 */
public class MapsTab extends ViewProjectScreen.TabScreen {

    public static final String NAME = "MapsTab";
    
    private StackPane container;
    private Project project;
    
    public MapsTab(){
        super(NAME);
        VBox box = new VBox();
        box.setAlignment(Pos.CENTER);
        box.getChildren().add(new Label("Maps tab is not yet supported."));
        box.getStyleClass().add("paper-white-z1");
        mContentView = box;
    }

}
