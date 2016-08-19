package com.gemseeker.pmma.data;

/**
 *
 * @author RAFIS-FRED
 */
public final class DBUtil {    
    
    public static final class Tables {
        public static final String PROJECTS = "Projects";
        public static final String HISTORIES = "Histories";
        public static final String LOCATIONS = "Locations";
        public static final String COORDINATES = "Coordinates";
        public static final String CONTACTS = "Contacts";
        public static final String EMAILS = "Emails";
        public static final String PHONE_NUMBERS = "Phone Numbers";
        public static final String MOBILE_NUMBERS = "Mobile Numbers";
        public static final String PROJECT_CONTACTS = "ProjectContacts";
    }
    
    public static final class Columns {
        // PROJECT
        public static final String PROJECT_CODE = "Code";
        public static final String PROJECT_NAME = "Project Name";
        public static final String PROJECT_LOCATION_ID = "Location ID";
        public static final String PROJECT_CONTRACTOR_ID = "Contractor ID";
        public static final String PROJECT_DATE_STARTED = "Date Started";
        public static final String PROJECT_DATE_TO_FINISH = "Date to Finish";
        public static final String PROJECT_STATUS = "Status";
        
        // HISTORY
        public static final String HISTORY_ID = "ID";
        public static final String HISTORY_PROJECT_CODE = "Project Code";
        public static final String HISTORY_DATE_CREATED = "Date Created";
        public static final String HISTORY_NOTES = "Notes";
        
        // LOCATION
        public static final String LOCATION_ID = "ID";
        public static final String LOCATION_STREET = "Street";
        public static final String LOCATION_CITY = "City";
        public static final String LOCATION_PROVINCE = "Province";
        
        // CORRDINATE
        public static final String COORDINATE_ID = "ID";
        public static final String COORDINATE_PROJECT_CODE = "Project Code";
        public static final String COORDINATE_LATITUDE = "Latitude";
        public static final String COORDINATE_LONGITUDE = "Longitude";
        
        // CONTACTS
        public static final String CONTACT_ID = "ID";
        public static final String CONTACT_NAME = "Contact Name";
        public static final String CONTACT_COMPANY = "Company Name";
        public static final String CONTACT_ADDRESS = "Address";
        
        // EMAILS
        public static final String EMAIL_ID = "ID";
        public static final String EMAIL_USER_ID = "User ID";
        public static final String EMAIL_ADDRESS = "Email";
        
        // PHONE NUMBERS
        public static final String PHONE_ID = "ID";
        public static final String PHONE_USER_ID = "User ID";
        public static final String PHONE_NUMBER = "Phone";
        
        // MOBILE NUMBERS
        public static final String MOBILE_ID = "ID";
        public static final String MOBILE_USER_ID = "User ID";
        public static final String MOBILE_NUMBER = "Mobile";
        
        // PROJECTCONTACTS
        public static final String PC_ID = "ID";
        public static final String PC_PROJECT_ID = "Project ID";
        public static final String PC_CONTACT_ID = "Contact ID";
    }
}
