package com.gemseeker.pmma.controllers.viewprojects;

import com.gemseeker.pmma.ControlledScreen;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

/**
 *
 * @author Gem Seeker
 */
public class MapsPage extends ControlledScreen {

    public static final String NAME = "MapsPage";
    private ViewProjectScreen viewProjectScreen;
    
    public MapsPage(ViewProjectScreen viewProjectScreen ){
        super(NAME);
        this.viewProjectScreen = viewProjectScreen;
        VBox box = new VBox();
        box.setAlignment(Pos.CENTER);
        box.getChildren().add(new Label("Maps tab is not yet supported."));
        box.getStyleClass().add("paper-white-z1");
        setContentView(box);
    }
    
}
