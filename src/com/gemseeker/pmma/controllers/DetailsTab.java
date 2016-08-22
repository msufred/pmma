package com.gemseeker.pmma.controllers;

import com.gemseeker.pmma.data.Contact;
import com.gemseeker.pmma.data.Project;
import com.gemseeker.pmma.fxml.ScreenLoader;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

/**
 *
 * @author Gem Seeker
 */
public class DetailsTab extends  ViewProjectScreen.TabScreen {
    
    public static final String NAME = "DetailsTab";

    @FXML ImageView mapView;
    @FXML StackPane detailsContainer;
    @FXML VBox detailsGroup;
    @FXML Label emptyContactsLabel;
    
    public DetailsTab(){
        super(NAME);
        initComponents();
    }
    
    private void initComponents(){
        mContentView = ScreenLoader.loadScreen(this, "project_details_tabview.fxml");
    }
    
    public void refresh(){
        if(project != null){
            ObservableList<Contact> contacts = project.getContacts();
            if(!contacts.isEmpty()){
                emptyContactsLabel.setVisible(false);
                detailsGroup.setVisible(true);
            }else{
                emptyContactsLabel.setVisible(true);
                detailsGroup.setVisible(false);
            }
        }else{
            emptyContactsLabel.setVisible(true);
            detailsGroup.setVisible(false);
        }
    }

    @FXML
    public void locationButtonAction(ActionEvent event){
        
    }
    
    @FXML
    public void detailsButtonAction(ActionEvent event){
        
    }
    
    @FXML
    public void photosButtonAction(ActionEvent event){
        
    }
    
    @FXML
    public void mapsButtonAction(ActionEvent event){
        
    }

    @Override
    public void onResume() {
        refresh();
    }

}
