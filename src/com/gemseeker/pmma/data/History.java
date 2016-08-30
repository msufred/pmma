package com.gemseeker.pmma.data;

import java.time.LocalDate;
import java.time.LocalDateTime;
import javafx.beans.property.SimpleStringProperty;

/**
 * Represents an entity from Histories table of the database.
 * 
 * @author RAFIS-FRED
 */
public class History {

    private int id;
    private LocalDateTime created;
    private final SimpleStringProperty notes = new SimpleStringProperty();
    
    public History(){}
    
    public History(int id, LocalDateTime created, String notes){
        setId(id);
        setDate(created);
        setNotes(notes);
    }
    
    public final void setId(int id){
        this.id = id;
    }
    
    public int getId(){
        return id;
    }

    public final void setDate(LocalDateTime created){
        this.created = created;
    }
    
    public LocalDateTime getDateCreated(){
        return created;
    }
    
    public final void setNotes(String notes){
        this.notes.set(notes);
    }
    
    public SimpleStringProperty getNotes(){
        return notes;
    }
    
    @Override
    public String toString(){
        return "History: " + created;
    }
}
