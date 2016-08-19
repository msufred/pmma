package com.gemseeker.pmma.ui.components;

import javafx.scene.Node;

/**
 *
 * @author Gem Seeker
 */
public abstract class PagerAdapter {

    public abstract String getTitle(int position);
    
    public abstract Node getContent(int position);
    
}
