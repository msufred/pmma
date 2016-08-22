/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.gemseeker.pmma.controllers;

import com.gemseeker.pmma.ControlledScreen;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

/**
 *
 * @author u
 */
public class ReportsScreen extends ControlledScreen {

    public static final String NAME = "reports";
    
    public ReportsScreen(){
        super(NAME);
        VBox box = new VBox();
        box.setAlignment(Pos.CENTER);
        box.getChildren().add(new Label("Reports screen is not implemented yet."));
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
