package com.gemseeker.pmma.data;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

/**
 *
 * @author Gem Seeker
 */
public class Contact {

    private final SimpleStringProperty contactId, name, company, address;
    private final ObservableList<String> phones;
    private final ObservableList<String> emails;
    
    public Contact(){
        contactId = new SimpleStringProperty();
        name = new SimpleStringProperty();
        company = new SimpleStringProperty();
        address = new SimpleStringProperty();
        phones = FXCollections.observableArrayList();
        emails = FXCollections.observableArrayList();
    }
    
    public void setContactId(String id){
        contactId.set(id);
    }
    
    public String getContactId(){
        return contactId.get();
    }
    
    public void setName(String name){
        this.name.set(name);
    }
    
    public String getName(){
        return name.get();
    }
    
    public void setCompany(String company){
        this.company.set(company);
    }
    
    public String getCompany(){
        return company.get();
    }
    
    public void setAddress(String address){
        this.address.set(address);
    }
    
    public String getAddress(){
        return address.get();
    }
    
    public void addPhone(String phone){
        phones.add(phone);
    }
    
    public ObservableList<String> getPhones(){
        return phones;
    }
    
    public void addEmail(String email){
        emails.add(email);
    }
    
    public ObservableList<String> getEmails(){
        return emails;
    }
}
