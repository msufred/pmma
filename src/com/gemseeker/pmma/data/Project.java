package com.gemseeker.pmma.data;

import java.time.LocalDate;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

/**
 * Represents an entity of Projects table from the database.
 * 
 * @author RAFIS-FRED
 */
public class Project {

    public static final String FOR_FUNDING = "For Funding";
    public static final String FUNDED = "Funded";
    public static final String ON_GOING = "On Going";
    public static final String POSTPONED = "Postponed";
    public static final String TERMINATED = "Terminated";
    public static final String FINISHED = "Finished";
    public static final String NOT_DEFINED = "Not Defined";

    // -- actual database column represented as observables
    private final SimpleIntegerProperty id = new SimpleIntegerProperty();
    private final SimpleStringProperty name = new SimpleStringProperty();
    private final SimpleStringProperty status = new SimpleStringProperty();

    private LocalDate dateCreated = null;
    private LocalDate dateToFinish = null;

    // -- additional observables
    private SimpleObjectProperty<Location> location = new SimpleObjectProperty<>();
    private ObservableList<Contact> contacts = FXCollections.observableArrayList();
    private ObservableList<History> histories = FXCollections.observableArrayList();;
    private ObservableList<Coordinate> coordinates = FXCollections.observableArrayList();;
    
    public Project(){
    }
    
    public Project(int id, String name, LocalDate created, LocalDate toFinish, String status){
        setId(id);
        setName(name);
        setDateCreated(created);
        setDateToFinish(toFinish);
        setStatus(status);
    }
    
    public final void setId(int id){
        this.id.set(id);
    }
    
    public SimpleIntegerProperty getIdProperty(){
        return id;
    }
    
    public final void setName(String name){
        this.name.set(name == null ? "" : name);
    }
    
    public SimpleStringProperty getNameProperty(){
        return name;
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
        this.status.set(status);
    }
    
    public SimpleStringProperty getStatusProperty(){
        return status;
    }
    
    public String getStatusValue(){
        return status.get();
    }
    
    @Override
    public String toString(){
        return name.get();
    }

    public SimpleObjectProperty<Location> getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location.set(location);
    }

    public void addContact(Contact contact){
        contacts.add(contact);
    }
    
    public void setContacts(ObservableList<Contact> contacts){
        this.contacts.setAll(contacts);
    }
    
    public ObservableList<Contact> getContacts(){
        return contacts;
    }
    
    public void setHistories(ObservableList<History> histories){
        this.histories.setAll(histories);
    }
    
    public ObservableList<History> getHistories(){
        return histories;
    }
    
    public void setCoordinates(ObservableList<Coordinate> coordinates){
        this.coordinates.setAll(coordinates);
    }
    
    public ObservableList<Coordinate> getCoordinates(){
        return coordinates;
    }
}
