package com.gemseeker.pmma.data;

import java.time.LocalDateTime;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

/**
 *
 * @author RAFIS-FRED
 */
public class ProjectUpdate {
    
    public static final String LEVEL_LOW = "Low";
    public static final String LEVEL_NORMAL = "Normal";
    public static final String LEVEL_HIGH = "High";

    private final SimpleIntegerProperty id = new SimpleIntegerProperty();
    private final SimpleStringProperty projectId = new SimpleStringProperty();
    private LocalDateTime created;
    private final SimpleStringProperty remarks = new SimpleStringProperty("");
    private final ObservableList<Attachment> attachments = FXCollections.observableArrayList();
    private final SimpleBooleanProperty hasAttachment = new SimpleBooleanProperty(false);
    private final SimpleStringProperty level = new SimpleStringProperty(LEVEL_NORMAL);
    private final SimpleStringProperty status = new SimpleStringProperty("Undefined");
    
    public ProjectUpdate(){
    }
    
    public void setId(int id){
        this.id.set(id);
    }
    
    public int getId(){
        return id.get();
    }
    
    public void setProjectId(String projectId){
        this.projectId.set(projectId);
    }
    
    public SimpleStringProperty getProjectId(){
        return projectId;
    }
    
    public void setDateCreated(LocalDateTime date){
        created = date;
    }
    
    public LocalDateTime getDateCreated(){
        return created;
    }
    
    public void setNotes(String notes){
        this.remarks.set(notes);
    }
    
    public SimpleStringProperty getNotes(){
        return remarks;
    }
    
    public ObservableList<Attachment> getAttachments(){
        return attachments;
    }
    
    public void setAttachments(ObservableList<Attachment> attachments){
        this.attachments.setAll(attachments);
    }
    
    public void setHasAttachment(boolean hasAttachment){
        this.hasAttachment.set(hasAttachment);
    }
    
    public SimpleBooleanProperty hasAttachment(){
        return hasAttachment;
    }
    
    public void setLevel(String level){
        this.level.set(level);
    }
    
    public SimpleStringProperty getLevel(){
        return level;
    }
    
    public void setStatus(String status){
        this.status.set(status);
    }
    
    public SimpleStringProperty getStatus(){
        return status;
    }
}
