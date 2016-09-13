package com.gemseeker.pmma.data;

import javafx.beans.property.SimpleStringProperty;

/**
 *
 * @author Gem Seeker
 */
public class PhoneNumber {

    private int id;
    private final SimpleStringProperty userIdProperty = new SimpleStringProperty();
    private final SimpleStringProperty phoneNumberProperty = new SimpleStringProperty();
    private final SimpleStringProperty typeProperty = new SimpleStringProperty();
    
    public PhoneNumber(){}
    
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
    
    public void setPhoneNumber(String phoneNumber){
        phoneNumberProperty.set(phoneNumber);
    }
    
    public SimpleStringProperty getPhoneNumberProperty(){
        return phoneNumberProperty;
    }
    
    public void setType(String type){
        typeProperty.set(type);
    }
    
    public SimpleStringProperty getTypeProperty(){
        return typeProperty;
    }
    
}
