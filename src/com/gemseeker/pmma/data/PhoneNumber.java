package com.gemseeker.pmma.data;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;

/**
 *
 * @author Gem Seeker
 */
public class PhoneNumber {

    private int id;
    private final SimpleIntegerProperty contactIdProperty = new SimpleIntegerProperty();
    private final SimpleStringProperty phoneNumberProperty = new SimpleStringProperty();
    private final SimpleStringProperty typeProperty = new SimpleStringProperty();
    
    public PhoneNumber(){}
    
    public void setId(int id){
        this.id = id;
    }
    
    public int getId(){
        return id;
    }
    
    public void setContactId(int contactId){
        contactIdProperty.set(contactId);
    }
    
    public SimpleIntegerProperty getContactIdProperty(){
        return contactIdProperty;
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
