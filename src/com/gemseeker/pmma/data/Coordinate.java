package com.gemseeker.pmma.data;

/**
 * Represents an entity from Coordinates table of the database.
 * 
 * @author RAFIS-FRED
 */
public class Coordinate {

    private int id;
    private String projectId;
    private long latitude;
    private long longitude;
    
    public Coordinate(){}
    
    public Coordinate(int id, String projectId, long latitude, long longitude){
        setId(id);
        setProjectId(projectId);
        setLatitude(latitude);
        setLongitude(longitude);
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
    
    public final void setLatitude(long lat){
        this.latitude = lat;
    }
    
    public long getLatitude(){
        return latitude;
    }
    
    public final void setLongitude(long longitude){
        this.longitude = longitude;
    }
    
    public final void setCoordinates(long latitude, long longitude){
        setLatitude(latitude);
        setLongitude(longitude);
    }
    
    public long[] getCoordinates(){
        return new long[]{latitude, longitude};
    }
}
