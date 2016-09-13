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
        public static final String PHONE_NUMBERS = "PhoneNumbers";
        public static final String PROJECT_CONTACTS = "ProjectContacts";
        public static final String PROJECT_UPDATES = "ProjectUpdates";
        public static final String ATTACHMENTS = "Attachments";
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
        public static final String HISTORY_DATE_CREATED = "Created";
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
        public static final String EMAIL_TYPE = "Type";

        // PHONE NUMBERS
        public static final String PHONE_ID = "ID";
        public static final String PHONE_USER_ID = "User ID";
        public static final String PHONE_NUMBER = "Phone";
        public static final String PHONE_TYPE = "Type";


        // PROJECTCONTACTS
        public static final String PC_ID = "ID";
        public static final String PC_PROJECT_ID = "Project ID";
        public static final String PC_CONTACT_ID = "Contact ID";

        // PROJECT UPDATES
        public static final String PU_ID = "ID";
        public static final String PU_PROJECT_ID = "Project ID";
        public static final String PU_DATE = "Created";
        public static final String PU_REMARKS = "Remarks";
        public static final String PU_STATUS = "Status";
        public static final String PU_LEVEL = "Level";
        public static final String PU_ATTACHMENT = "Attachment";
        
        // ATTACHMENTS
        public static final String AT_ID = "ID";
        public static final String AT_UPDATE_ID = "Update ID";
        public static final String AT_PATH = "Path";
        public static final String AT_TYPE = "Type";
    }
}