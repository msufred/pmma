package com.gemseeker.pmma.data;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;

/**
 *
 * @author Gem Seeker
 */
public class Attachment {

    public static final String TYPE_IMAGE = "Image";
    public static final String TYPE_VIDEO = "Video";
    public static final String TYPE_ANY = "Any";
    
    private final SimpleIntegerProperty id = new SimpleIntegerProperty();
    private final SimpleIntegerProperty updateId = new SimpleIntegerProperty();
    private final SimpleStringProperty path = new SimpleStringProperty();
    private final SimpleStringProperty type = new SimpleStringProperty();
    
    public Attachment(){}
    
    public void setId(int id){
        this.id.set(id);
    }
    
    public SimpleIntegerProperty getIdProperty(){
        return id;
    }
    
    public void setUpdateId(int updateId){
        this.updateId.set(updateId);
    }
    
    public SimpleIntegerProperty getUpdateIdProperty(){
        return updateId;
    }
    
    public void setPath(String path){
        this.path.set(path);
    }
    
    public SimpleStringProperty getPathProperty(){
        return path;
    }
    
    public void setType(String type){
        this.type.set(type);
    }
    
    public SimpleStringProperty getTypeProperty(){
        return type;
    }
}
