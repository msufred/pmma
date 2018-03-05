package com.gemseeker.pmma.data;

import java.util.Collection;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

/**
 *
 * @author Gem Seeker
 */
public class Contact {

    private final SimpleIntegerProperty contactId = new SimpleIntegerProperty();
    private final SimpleStringProperty firstName = new SimpleStringProperty();
    private final SimpleStringProperty lastName = new SimpleStringProperty();
    private final SimpleStringProperty company = new SimpleStringProperty();
    private final SimpleStringProperty address = new SimpleStringProperty();
    private final SimpleStringProperty imagePath = new SimpleStringProperty();
    
    private ObservableList<PhoneNumber> phones = FXCollections.observableArrayList();
    private ObservableList<Email> emails = FXCollections.observableArrayList();
    
    public Contact(){
    }
    
    public void setContactId(int id){
        contactId.set(id);
    }
    
    public SimpleIntegerProperty getContactIdProperty(){
        return contactId;
    }
    
    public void setFirstName(String name){
        this.firstName.set(name);
    }
    
    public SimpleStringProperty getFirstNameProperty(){
        return firstName;
    }

    public void setLastName(String name){
        this.lastName.set(name);
    }

    public SimpleStringProperty getLastNameProperty(){
        return lastName;
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
