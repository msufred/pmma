package com.gemseeker.pmma.data;

import java.time.LocalDate;
import javafx.collections.ObservableList;

/**
 * Represents an entity of Projects table from the database.
 * 
 * @author RAFIS-FRED
 */
public class Project {

    public static final String ON_GOING = "On Going";
    public static final String POSTPONED = "Postponed";
    public static final String TERMINATED = "Terminated";
    public static final String FINISHED = "Finished";
    
    private String id;
    private String name;
    private String status;
    private LocalDate dateCreated = null;
    private LocalDate dateToFinish = null;
    private String locationId;
    private String contractorId;
    
    private Location location;
    private ObservableList<Contact> contacts;
    private ObservableList<History> histories;
    private ObservableList<Coordinate> coordinates;
    
    public Project(){}
    
    public Project(String id, String name, String locationId,
            LocalDate created, LocalDate toFinish, String status){
        setId(id);
        setName(name);
        setLocationId(locationId);
        setDateCreated(created);
        setDateToFinish(toFinish);
        setStatus(status);
    }
    
    public final void setId(String id){
        this.id = id;
    }
    
    public String getId(){
        return id;
    }
    
    public final void setName(String name){
        this.name = name == null ? "" : name;
    }
    
    public String getName(){
        return name;
    }
    
    public final void setLocationId(String locationId){
        this.locationId = locationId;
    }
    
    public String getLocationId(){
        return locationId;
    }
    
    public final void setDateCreated(LocalDate created){
        this.dateCreated = created;
    }
    
    public LocalDate getDateCreated(){
        return dateCreated;
    }

    public final void setDateToFinish(LocalDate toFinish){
        this.dateToFinish = toFinish;
    }
    
    public LocalDate getDateToFinish(){
        return dateToFinish;
    }
    
    public final void setStatus(String status){
        this.status = status;
    }
    
    public String getStatus(){
        return status;
    }
    
    @Override
    public String toString(){
        return name;
    }
    
    public void setLocation(Location location){
        this.location = location;
    }
    
    public Location getLocation(){
        return location;
    }
    
    public void addContact(Contact contact){
        contacts.add(contact);
    }
    
    public void setContacts(ObservableList<Contact> contacts){
        this.contacts = contacts;
    }
    
    public void setHistories(ObservableList<History> histories){
        this.histories = histories;
    }
    
    public ObservableList<History> getHistories(){
        return histories;
    }
    
    public void setCoordinates(ObservableList<Coordinate> coordinates){
        this.coordinates = coordinates;
    }
    
    public ObservableList<Coordinate> getCoodinates(){
        return coordinates;
    }
}
