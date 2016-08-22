package com.gemseeker.pmma.data;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;

/**
 *
 * @author Gem Seeker
 */
public class ProjectContact {
    
    private final SimpleIntegerProperty id = new SimpleIntegerProperty();
    private final SimpleStringProperty projectId = new SimpleStringProperty();
    private final SimpleStringProperty contactId = new SimpleStringProperty();
    private Contact contact;
    
    public ProjectContact(){}
    
    public ProjectContact(int id, String projectId, String contactId, Contact contact){
        setId(id);
        setProjectId(projectId);
        setContactId(contactId);
        setContact(contact);
    }
    
    public final void setId(int id){
        this.id.set(id);
    }
    
    public int getIdValue(){
        return id.get();
    }
    
    public SimpleIntegerProperty getId(){
        return id;
    }
    
    public final void setProjectId(String projectId){
        this.projectId.set(projectId);
    }
    
    public String getProjectIdValue(){
        return projectId.get();
    }
    
    public SimpleStringProperty getProjectId(){
        return projectId;
    }
    
    public final void setContactId(String contactId){
        this.contactId.set(contactId);
    }
    
    public String getContactIdValue(){
        return contactId.get();
    }
    
    public SimpleStringProperty getContactId(){
        return contactId;
    }
    
    public final void setContact(Contact contact){
        this.contact = contact;
    }
    
    public Contact getContact(){
        return contact;
    }
}
