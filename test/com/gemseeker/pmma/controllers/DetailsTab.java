package com.gemseeker.pmma.controllers;

import com.gemseeker.pmma.fxml.ScreenLoader;

/**
 *
 * @author Gem Seeker
 */
public class DetailsTab extends ViewProjectScreen.TabScreen {

    public static final String NAME = "DetailsTab";
    
    public DetailsTab() {
        super(NAME);
        initComponents();
    }

    private void initComponents(){
        mContentView = ScreenLoader.loadScreen(this, "project_details_tabview.fxml");
    }
}
