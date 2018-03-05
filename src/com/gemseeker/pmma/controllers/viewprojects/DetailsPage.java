package com.gemseeker.pmma.controllers.viewprojects;

import com.gemseeker.pmma.ControlledScreen;
import com.gemseeker.pmma.fxml.ScreenLoader;

/**
 *
 * @author Gem Seeker
 */
public class DetailsPage extends ControlledScreen {

    public static final String NAME = "DetailsPage";
    private ViewProjectScreen viewProjectScreen;
    
    public DetailsPage(ViewProjectScreen viewProjectScreen) {
        super(NAME);
        this.viewProjectScreen = viewProjectScreen;
        setContentView(ScreenLoader.loadScreen(DetailsPage.this, "project_details_tabview.fxml"));
    }

    @Override
    public void onResume() {
        super.onResume();
        System.out.println("Called onResume of DetailsPage");
    }

}
