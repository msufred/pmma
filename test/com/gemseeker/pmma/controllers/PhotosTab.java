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
public class PhotosTab extends ViewProjectScreen.TabScreen {

    public static final String NAME = "PhotosTab";
    
    private StackPane container;
    private Project project;
    
    public PhotosTab(){
        super(NAME);
        VBox box = new VBox();
        box.setAlignment(Pos.CENTER);
        box.getChildren().add(new Label("Photos tab is not yet supported."));
        box.getStyleClass().add("paper-white-z1");
        mContentView = box;
    }

}
