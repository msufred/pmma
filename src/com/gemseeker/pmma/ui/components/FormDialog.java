package com.gemseeker.pmma.ui.components;

import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;

/**
 *
 * @author Gem Seeker
 */
public class FormDialog extends Stage {

    public FormDialog(Parent content){
        initModality(Modality.APPLICATION_MODAL);
        setScene(new Scene(content));
    }
}
