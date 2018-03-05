package com.gemseeker.pmma.controllers.viewprojects;

import com.gemseeker.pmma.ControlledScreen;
import com.gemseeker.pmma.components.ViewPager;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Node;

/**
 *
 * @author Gem Seeker
 */
public class ViewProjectPageAdapter implements ViewPager.PagerAdapter {

    final ObservableList<String> titles = FXCollections.observableArrayList(
            "SUMMARY", "UPDATES", "PHOTOS", "MAPS"
    );
    final ObservableList<ControlledScreen> pages = FXCollections.observableArrayList();
    
    public ViewProjectPageAdapter(ViewProjectScreen viewProjectScreen){
        pages.addAll(
                new DetailsPage(viewProjectScreen),
                new UpdatesPage(viewProjectScreen),
                new PhotosPage(viewProjectScreen),
                new MapsPage(viewProjectScreen)
        );
    }
    
    @Override
    public Node onCreateView(int position) {
        return pages.get(position).getContentView();
    }

    @Override
    public ControlledScreen getControllerAt(int position) {
        return pages.get(position);
    }

    @Override
    public String getTitleAt(int position) {
        return titles.get(position);
    }

    @Override
    public int getCount() {
        return pages.size();
    }

}
