package com.gemseeker.pmma.data;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;

/**
 *
 * @author Gem Seeker
 */
public class Email {

    private int id;
    private final SimpleIntegerProperty contactIdProperty = new SimpleIntegerProperty();
    private final SimpleStringProperty emailAddressProperty = new SimpleStringProperty();
    private final SimpleStringProperty typeProperty = new SimpleStringProperty();
    
    public Email(){}
    
    public void setId(int id){
        this.id = id;
    }
    
    public int getId(){
        return id;
    }
    
    public void setContactId(int userId){
        contactIdProperty.set(userId);
    }
    
    public SimpleIntegerProperty getContactIdProperty(){
        return contactIdProperty;
    }
    
    public void setEmailAddress(String emailAddress){
        emailAddressProperty.set(emailAddress);
    }
    
    public SimpleStringProperty getEmailAddressProperty(){
        return emailAddressProperty;
    }
    
    public void setType(String type){
        typeProperty.set(type);
    }
    
    public SimpleStringProperty getTypeProperty(){
        return typeProperty;
    }
    
}
