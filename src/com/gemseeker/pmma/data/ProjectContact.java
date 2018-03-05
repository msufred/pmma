package com.gemseeker.pmma.data;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;

/**
 *
 * @author Gem Seeker
 */
public class ProjectContact {
    
    private final SimpleIntegerProperty id = new SimpleIntegerProperty();
    private final SimpleIntegerProperty projectId = new SimpleIntegerProperty();
    private final SimpleIntegerProperty contactId = new SimpleIntegerProperty();
    private Contact contact;
    
    public ProjectContact(){}
    
    public ProjectContact(int id, int projectId, int contactId, Contact contact){
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
    
    public final void setProjectId(int projectId){
        this.projectId.set(projectId);
    }
    
    public int getProjectIdValue(){
        return projectId.get();
    }
    
    public SimpleIntegerProperty getProjectId(){
        return projectId;
    }
    
    public final void setContactId(int contactId){
        this.contactId.set(contactId);
    }
    
    public int getContactIdValue(){
        return contactId.get();
    }
    
    public SimpleIntegerProperty getContactId(){
        return contactId;
    }
    
    public final void setContact(Contact contact){
        this.contact = contact;
    }
    
    public Contact getContact(){
        return contact;
    }
}
