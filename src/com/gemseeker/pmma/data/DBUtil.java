package com.gemseeker.pmma.data;

/**
 *
 * @author RAFIS-FRED
 */
public final class DBUtil {    
    
    public static final class Tables {
        static final String PROJECTS = "Projects";
        static final String HISTORIES = "Histories";
        static final String LOCATIONS = "Locations";
        static final String COORDINATES = "Coordinates";
    }
    
    public static final class Columns {
        // PROJECT
        static final String PROJECT_CODE = "Code";
        static final String PROJECT_NAME = "Project Name";
        static final String PROJECT_LOCATION_ID = "Location ID";
        static final String PROJECT_DATE_STARTED = "Date Started";
        static final String PROJECT_DATE_TO_FINISH = "Date to Finish";
        static final String PROJECT_STATUS = "Status";
        
        // HISTORY
        static final String HISTORY_ID = "ID";
        static final String HISTORY_PROJECT_CODE = "Project Code";
        static final String HISTORY_DATE_CREATED = "Date Created";
        static final String HISTORY_NOTES = "Notes";
        
        // LOCATION
        static final String LOCATION_ID = "ID";
        static final String LOCATION_STREET = "Street";
        static final String LOCATION_CITY = "City";
        static final String LOCATION_PROVINCE = "Province";
        
        // CORRDINATE
        static final String COORDINATE_ID = "ID";
        static final String COORDINATE_PROJECT_CODE = "Project Code";
        static final String COORDINATE_LATITUDE = "Latitude";
        static final String COORDINATE_LONGITUDE = "Longitude";
    }
}
