package com.gemseeker.pmma.data;

import java.time.LocalDate;

/**
 * Represents an entity from Histories table of the database.
 * 
 * @author RAFIS-FRED
 */
public class History {

    private int id;
    private String projectId;
    private LocalDate created;
    private String notes;
    
    public History(){}
    
    public History(int id, String projectId, LocalDate created, String notes){
        setId(id);
        setProjectId(projectId);
        setDate(created);
        setNotes(notes);
    }
    
    public final void setId(int id){
        this.id = id;
    }
    
    public int getId(){
        return id;
    }
    
    public final void setProjectId(String projectId){
        this.projectId = projectId;
    }
    
    public String getProjectId(){
        return projectId;
    }
    
    public final void setDate(LocalDate created){
        this.created = created;
    }
    
    public LocalDate getDateCreated(){
        return created;
    }
    
    public final void setNotes(String notes){
        this.notes = notes;
    }
    
    public String getNotes(){
        return notes;
    }
    
    @Override
    public String toString(){
        return "History: " + created;
    }
}
