package com.gemseeker.pmma.components;

import com.gemseeker.pmma.data.Contact;
import com.gemseeker.pmma.fxml.ScreenLoader;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;

/**
 *
 * @author RAFIS-FRED
 */
public class ContactNode {

    private final Parent contentView;
    
    @FXML ImageView imageView;
    @FXML Label nameLabel;
    @FXML Label companyLabel;
    @FXML Label phoneLabel;
    @FXML Label emailLabel;
    private Contact mContact;
    
    public ContactNode(){
        contentView = ScreenLoader.loadScreen(ContactNode.this, "contact_node.fxml");
    }
    
    public void setContact(Contact contact){
        nameLabel.setText(contact.getName());
        companyLabel.setText(contact.getCompany());
        mContact = contact;
    }
    
    public Parent getContentView(){
        return contentView;
    }
    
}
