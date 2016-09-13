package com.gemseeker.pmma.controllers;

import com.gemseeker.pmma.data.Contact;
import com.gemseeker.pmma.fxml.ScreenLoader;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.shape.Circle;

/**
 *
 * @author Gem Seeker
 */
public class ChatForm {

    @FXML Label contactLabel;
    @FXML Circle status;
    
    private final Parent contentView;
    private Contact mContact;
    
    public ChatForm(){
        contentView = ScreenLoader.loadScreen(ChatForm.this, "chat.fxml");
    }
    
    public Parent getContentView(){
        return contentView;
    }
    
    public void setContact(Contact contact){
        mContact = contact;
        contactLabel.setText(mContact.getNameProperty().get());
    }
    
    public void loadConversations(){
        // TODO: Load conversations here. Loading action must be done in a
        // background thread.
    }
}
