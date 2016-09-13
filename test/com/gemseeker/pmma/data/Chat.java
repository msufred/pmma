package com.gemseeker.pmma.data;

import com.gemseeker.pmma.fxml.ScreenLoader;
import javafx.scene.Parent;

/**
 *
 * @author Gem Seeker
 */
public class Chat {

    private final Parent contentView;
    
    public Chat(){
        contentView = ScreenLoader.loadScreen(Chat.this, "chat.fxml");
    }
    
}
