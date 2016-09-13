package com.gemseeker.pmma.data;

import java.util.Collection;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

/**
 *
 * @author Gem Seeker
 */
public class Contact {

    private final SimpleStringProperty contactId = new SimpleStringProperty();
    private final SimpleStringProperty name = new SimpleStringProperty();
    private final SimpleStringProperty company = new SimpleStringProperty();
    private final SimpleStringProperty address = new SimpleStringProperty();
    private final SimpleStringProperty imagePath = new SimpleStringProperty();
    
    private ObservableList<PhoneNumber> phones = FXCollections.observableArrayList();
    private ObservableList<Email> emails = FXCollections.observableArrayList();
    
    public Contact(){
    }
    
    public void setContactId(String id){
        contactId.set(id);
    }
    
    public SimpleStringProperty getContactIdProperty(){
        return contactId;
    }
    
    public void setName(String name){
        this.name.set(name);
    }
    
    public SimpleStringProperty getNameProperty(){
        return name;
    }
    
    public void setCompany(String company){
        this.company.set(company);
    }
    
    public SimpleStringProperty getCompanyProperty(){
        return company;
    }
    
    public void setAddress(String address){
        this.address.set(address);
    }
    
    public SimpleStringProperty getAddressProperty(){
        return address;
    }
    
    public void setImagePath(String path){
        this.imagePath.set(path);
    }
    
    public SimpleStringProperty getImagePathProperty(){
        return imagePath;
    }
    
    public void addPhone(PhoneNumber phone){
        phones.add(phone);
    }
    
    public void setPhones(Collection<? extends PhoneNumber> phones){
        this.phones.setAll(phones);
    }
    
    public void setPhones(ObservableList<PhoneNumber> phones){
        this.phones = phones;
    }
    
    public ObservableList<PhoneNumber> getPhones(){
        return phones;
    }
    
    public void addEmail(Email email){
        emails.add(email);
    }
    
    public void setEmails(Collection<? extends Email> emails){
        this.emails.setAll(emails);
    }
    
    public void setEmails(ObservableList<Email> emails){
        this.emails = emails;
    }
    
    public ObservableList<Email> getEmails(){
        return emails;
    }
}
