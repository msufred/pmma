package com.gemseeker.pmma.controllers;

import com.gemseeker.pmma.ControlledScreen;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

/**
 *
 * @author Gem Seeker
 */
public class ContactsScreen extends ControlledScreen {

    public static final String NAME = "ContactsScreen";
    
    public ContactsScreen(){
        super(NAME);
        VBox box = new VBox();
        box.setAlignment(Pos.CENTER);
        box.getChildren().add(new Label("Contacts screen is not implemented yet."));
        box.getStyleClass().add("paper-white-z1");
        setContentView(box);
    }
    
    @Override
    public void onStart() {
    }

    @Override
    public void onPause() {
    }

    @Override
    public void onResume() {
    }

    @Override
    public void onFinish() {
    }
}
