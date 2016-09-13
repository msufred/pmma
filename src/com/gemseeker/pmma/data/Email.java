package com.gemseeker.pmma.data;

import javafx.beans.property.SimpleStringProperty;

/**
 *
 * @author Gem Seeker
 */
public class Email {

    private int id;
    private final SimpleStringProperty userIdProperty = new SimpleStringProperty();
    private final SimpleStringProperty emailAddressProperty = new SimpleStringProperty();
    private final SimpleStringProperty typeProperty = new SimpleStringProperty();
    
    public Email(){}
    
    public void setId(int id){
        this.id = id;
    }
    
    public int getId(){
        return id;
    }
    
    public void setUserId(String userId){
        userIdProperty.set(userId);
    }
    
    public SimpleStringProperty getUserIdProperty(){
        return userIdProperty;
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
