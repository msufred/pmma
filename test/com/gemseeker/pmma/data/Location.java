package com.gemseeker.pmma.data;

/**
 * Represents an entity from Locations table of the database.
 * 
 * @author RAFIS-FRED
 */
public class Location {

    private String locationId;
    private String street;
    private String city;
    private String province;
    
    public Location(){}
    
    public Location(String id, String street, String city, String province){
        setId(id);
        setStreet(street);
        setCity(city);
        setProvince(province);
    }
    
    public final void setId(String id){
        this.locationId = id;
    }
    
    public String getId(){
        return locationId;
    }
    
    public final void setStreet(String street){
        this.street = street;
    }
    
    public String getStreet(){
        return street;
    }
    
    public final void setCity(String city){
        this.city = city;
    }
    
    public String getCity(){
        return city;
    }
    
    public final void setProvince(String province){
        this.province = province;
    }
    
    public String getProvince(){
        return province;
    }
    
    @Override
    public String toString(){
        return String.format("%s, %s, %s", street, city, province);
    }
}
