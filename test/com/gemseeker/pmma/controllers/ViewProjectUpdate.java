package com.gemseeker.pmma.controllers;

import com.gemseeker.pmma.fxml.ScreenLoader;
import javafx.scene.Parent;

/**
 *
 * @author Gem Seeker
 */
public class ViewProjectUpdate {

    private final Parent contentView;
    
    public ViewProjectUpdate(){
        contentView = ScreenLoader.loadScreen(ViewProjectUpdate.this, "view_update.fxml");
        initComponents();
    }
    
    private void initComponents(){
        
    }
    
    public Parent getContentView(){
        return contentView;
    }
}
