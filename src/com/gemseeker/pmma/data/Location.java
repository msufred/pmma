package com.gemseeker.pmma.data;

/**
 * Represents an entity from Locations table of the database.
 * 
 * @author RAFIS-FRED
 */
public class Location {

    private int locationId;
    private String barangay;
    private String municipality;
    private String province;
    private String otherLocationInfo;
    private String longitude;
    private String latitude;
    
    public Location(){}
    
    public Location(int id, String street, String municipality, String province){
        setId(id);
        setBarangay(street);
        setMunicipality(municipality);
        setProvince(province);
    }
    
    public final void setId(int id){
        this.locationId = id;
    }
    
    public int getId(){
        return locationId;
    }
    
    public final void setBarangay(String barangay){
        this.barangay = barangay;
    }
    
    public String getBarangay(){
        return barangay;
    }
    
    public final void setMunicipality(String municipality){
        this.municipality = municipality;
    }
    
    public String getMunicipality(){
        return municipality;
    }
    
    public final void setProvince(String province){
        this.province = province;
    }
    
    public String getProvince(){
        return province;
    }
    
    @Override
    public String toString(){
        return String.format("%s, %s, %s", barangay, municipality, province);
    }

    public String getOtherLocationInfo() {
        return otherLocationInfo;
    }

    public void setOtherLocationInfo(String otherLocationInfo) {
        this.otherLocationInfo = otherLocationInfo;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }
}
