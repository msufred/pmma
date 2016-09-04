package com.gemseeker.pmma.data;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;

import static com.gemseeker.pmma.data.DBUtil.Columns.*;
import static com.gemseeker.pmma.data.DBUtil.Tables.*;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.Calendar;
import static java.util.Calendar.*;
import static com.gemseeker.pmma.utils.LogUtil.*;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import javafx.collections.ObservableList;

/**
 * DBManager, as the name suggests, it manages the database and its operations.
 * All database operations within this DBManager is a one time access only. "One
 * time access only" means that for each database operation, the connection to
 * the database made is closed immediately after the operation is done. For the
 * time being, a database "connection pool" is not necessary since there is only
 * one client that is able to access the database.
 *
 * NOTE TO MYSELF: On the plan to make this application access its database
 * through a network, the server must create an application with a database
 * connection pool. For now, this application serves as a server and a client
 * at the same time.
 *
 * @author RAFIS-FRED
 */
public class DBManager {

    // NOTE TO MYSELF: Make sure to log all database operations such as adding,
    // updating or deleting an entry on the database. Save the log to a log file.
    
    public static final String DEBUG_NAME = "DBManager";
    public static final String DB_NAME = "resources/project_management_db.accdb";
    public static final String DB_DRIVER = "jdbc:ucanaccess://" + DB_NAME;

    public static final boolean addProject(Project project) {
        try (Connection conn = connect()) {
            String query = "INSERT INTO "
                    + PROJECTS + " (["
                    + PROJECT_CODE + "], [" // (1)
                    + PROJECT_NAME + "], [" // (2)
                    + PROJECT_LOCATION_ID + "], [" // (3)
                    + PROJECT_DATE_STARTED + "], [" // (4)
                    + PROJECT_DATE_TO_FINISH + "], [" // (5)
                    + PROJECT_STATUS + "])" // (6)
                    + " VALUES (?, ?, ?, ?, ?, ?);";
            
            int updateCount;
            try(PreparedStatement ps = conn.prepareStatement(query)){
                ps.setString(1, project.getIdProperty().get()); //(1)
                ps.setString(2, project.getNameProperty().get());//(2)
                ps.setString(3, project.getLocationIdProperty().get());//(3)

                //(4)
                // check if Date is not null
                LocalDate ls = project.getDateCreated();
                Calendar started = Calendar.getInstance();
                // I subtracted 1 from ls.getMonthValue because Calendar and LocalData
                // has different month values. Calendar's month is numbered from 0-11
                // while LocalDate's month is numbered from 1-12
                started.set(ls.getYear(), ls.getMonthValue() - 1, ls.getDayOfMonth());
                ps.setDate(4, new Date(started.getTime().getTime()));

                //(5)
                LocalDate lc = project.getDateToFinish();
                Calendar completion = Calendar.getInstance();
                completion.set(lc.getYear(), lc.getMonthValue() - 1, lc.getDayOfMonth());
                ps.setDate(5, new Date(completion.getTime().getTime()));

                //(6)
                ps.setString(6, project.getStatusValue());
                
                verbose(DEBUG_NAME, "Executing query: " + query);

                updateCount = ps.executeUpdate();
            }
            
            if (updateCount > 0) {
                verbose(DEBUG_NAME, "Project \"" + project.getNameProperty().get()+ "\" is added to the"
                        + " database successfully. (Rows affected [" + updateCount + "])");
                return true;
            }else{
                verbose(DEBUG_NAME, "Failed to add project \"" + project.getNameProperty().get()
                        + "\". (Rows affected [" + updateCount + "])");
                return false;
            }
        } catch (SQLException e) {
            error(DEBUG_NAME, "Failed to add project \"" + project.getNameProperty().get() + "\" to database.", e);
            return false;
        }
    }
    
    public void addProjects(Project...projects){
        
    }
    
    public static final boolean deleteProject(String id){
        try(Connection con = connect()){
            String query = "DELETE FROM " + PROJECTS + " WHERE [" + PROJECT_CODE + "]=\"" + id + "\";";
            
            int updateCount;
            try(PreparedStatement ps = con.prepareStatement(query)){
                verbose(DEBUG_NAME, "Executing query: " + query);
                updateCount = ps.executeUpdate();
            }
            
            if(updateCount > 0){
                verbose(DEBUG_NAME, "Deleted Project with ID = " + id + ". (Rows affected [" + updateCount + "])");
                return true;
            } else {
                verbose(DEBUG_NAME, "Failed to deleted Project with ID = " + id + ". (Rows affected [" + updateCount + "])");
                return false;
            }
        }catch(SQLException e){
            error(DEBUG_NAME, "Failed  to delete Project with ID = " + id, e);
            return false;
        }
    }
    
    public static final boolean updateProject(Project project){
        try(Connection con = connect()){
            String query = "UPDATE "
                        + PROJECTS + " SET ["
                        + PROJECT_NAME + "]=?, [" // (1)
                        + PROJECT_LOCATION_ID + "]=?, [" // (2)
                        + PROJECT_DATE_STARTED + "]=?, [" // (3)
                        + PROJECT_DATE_TO_FINISH + "]=?, [" // (4)
                        + PROJECT_STATUS + "]=? WHERE [" // (5)
                        + PROJECT_CODE + "]=?;"; // (6)
            
            int updateCount;
            try(PreparedStatement ps = con.prepareStatement(query)){
            
                ps.setString(1, project.getNameProperty().get()); // (1)
                ps.setString(2, project.getLocationIdProperty().get()); // (2)

                LocalDate ls = project.getDateCreated();
                Calendar started = Calendar.getInstance();
                started.set(ls.getYear(), ls.getMonthValue() - 1, ls.getDayOfMonth());
                ps.setDate(3, new Date(started.getTime().getTime())); // (3)

                LocalDate lf = project.getDateToFinish();
                Calendar completion = Calendar.getInstance();
                completion.set(lf.getYear(), lf.getMonthValue() - 1, lf.getDayOfMonth());
                ps.setDate(4, new Date(completion.getTime().getTime())); // (4)

                ps.setString(5, project.getStatusValue()); // (5)
                ps.setString(6, project.getIdProperty().get()); // (6)
                
                verbose(DEBUG_NAME, "Executing query: " + query);
                updateCount = ps.executeUpdate();
            }
            
            if (updateCount > 0) {
                verbose(DEBUG_NAME, "Project with ID = " + project.getIdProperty().get() + " was updated successfully. "
                        + "(Rows affected [" + updateCount + "])");
                return true;
            }else{
                verbose(DEBUG_NAME, "Failed to update Project with ID = " + project.getIdProperty().get()
                        + ". (Rows affected [" + updateCount + "])");
                return false;
            }
        }catch(SQLException e){
            error(DEBUG_NAME, "Failed to update Project with ID = " + project.getIdProperty().get() + ".", e);
            return false;
        }
    }

    public static final ArrayList<Project> getProjects() {
        ArrayList<Project> projects = new ArrayList<>();
        try (Connection conn = connect(); Statement statement = conn.createStatement();) {
            String query = "SELECT * FROM " + PROJECTS;
            verbose(DEBUG_NAME, "Executing query: " + query);
            
            ResultSet result = statement.executeQuery(query);
            while (result.next()) {
                Project project = new Project();
                project.setId(result.getString(PROJECT_CODE));
                project.setName(result.getString(PROJECT_NAME));
                project.setLocationId(result.getString(PROJECT_LOCATION_ID));
                
                Date started = result.getDate(PROJECT_DATE_STARTED);
                Calendar cs = Calendar.getInstance();
                cs.setTime(started);
                
                LocalDate dateStarted = LocalDate.of(cs.get(YEAR), cs.get(MONTH) + 1, cs.get(DAY_OF_MONTH));
                project.setDateCreated(dateStarted);

                Date completion = result.getDate(PROJECT_DATE_TO_FINISH);
                Calendar cf = Calendar.getInstance();
                cf.setTime(completion);
                LocalDate dateToFinish = LocalDate.of(cf.get(YEAR), cf.get(MONTH) + 1, cf.get(DAY_OF_MONTH));
                project.setDateToFinish(dateToFinish);
                
                project.setStatus(result.getString(PROJECT_STATUS));
                projects.add(project);
            }
        } catch (SQLException e) {
            error(DEBUG_NAME, "Failed to retrieve projects from database. The returned list is possibly empty.", e);
        }
        return projects;
    }
    
    public static final boolean addHistory(History history){
        try(Connection con = connect()){
            String query = "INSERT INTO "
                    + HISTORIES + " (["
                    + HISTORY_DATE_CREATED + "], ["
                    + HISTORY_NOTES + "]) "
                    + "VALUES (?, ?);";
            int updateCount;
            try(PreparedStatement ps = con.prepareStatement(query)){
                LocalDateTime ls = history.getDateCreated();
                Calendar started = Calendar.getInstance();
                started.set(ls.getYear(), ls.getMonthValue()- 1, ls.getDayOfMonth(), ls.getHour() + 1, ls.getMinute());
                ps.setDate(1, new Date(started.getTime().getTime()));
                ps.setString(2, history.getNotes().get());

                verbose(DEBUG_NAME, "Executing query: " + query);
                updateCount = ps.executeUpdate();
            }
            if (updateCount > 0) {
                verbose(DEBUG_NAME, "Added new History to database. "
                        + "(Rows affected [" + updateCount + "])");
                return true;
            }else{
                verbose(DEBUG_NAME, "Failed to add History to database"
                        + ". (Rows affected [" + updateCount + "])");
                return false;
            }
        }catch(SQLException e){
            error(DEBUG_NAME, "Failed to add History to database.", e);
            return false;
        }
    }

    public static final ArrayList<History> getHistories() {
        ArrayList<History> histories = new ArrayList<>();
        try (Connection conn = connect(); Statement statement = conn.createStatement();) {
            String query = "SELECT * FROM " + HISTORIES;
            verbose(DEBUG_NAME, "Executing query: " + query);
            ResultSet result = statement.executeQuery(query);
            while (result.next()) {
                History history = new History();
                history.setId(result.getInt(HISTORY_ID));
                
                Timestamp timeStamp= result.getTimestamp(HISTORY_DATE_CREATED);
                history.setDate(timeStamp.toLocalDateTime());
                history.setNotes(result.getString(HISTORY_NOTES));
                histories.add(history);
            }
        } catch (SQLException e) {
            error(DEBUG_NAME, "Failed to retrieve histories from database. The return list is possible empty.", e);
        }
        return histories;
    }
    
    public static final boolean addLocation(Location location) {
        try (Connection conn = connect()) {
            String query = "INSERT INTO ["
                    + LOCATIONS + "] (["
                    + LOCATION_ID + "], ["
                    + LOCATION_STREET + "], ["
                    + LOCATION_CITY + "], ["
                    + LOCATION_PROVINCE + "])"
                    + " VALUES (?, ?, ?, ?);";
            
            int updateCount;
            try(PreparedStatement ps = conn.prepareStatement(query)){
                ps.setString(1, location.getId());
                ps.setString(2, location.getStreet());
                ps.setString(3, location.getCity());
                ps.setString(4, location.getProvince());
                
                verbose(DEBUG_NAME, "Executing query: " + query);
                updateCount = ps.executeUpdate();
            }
            if (updateCount > 0) {
                verbose(DEBUG_NAME, "Added location \"" + location.toString() + "\" successfully. "
                        + "(Rows affected [" + updateCount + "])");
                return true;
            }else{
                verbose(DEBUG_NAME, "Failed to add location \"" + location.toString()
                        + "\". (Rows affected [" + updateCount + "])");
                return false;
            }
        } catch (SQLException e) {
            error(DEBUG_NAME, "Failed to add location to database.", e);
            return false;
        }
    }

    public static final boolean updateLocation(Location location){
        try(Connection con = connect()){
            String query = "UPDATE "
                    + LOCATIONS + " SET ["
                    + LOCATION_PROVINCE + "]=?, ["
                    + LOCATION_CITY + "]=?, ["
                    + LOCATION_STREET + "]=? WHERE ["
                    + LOCATION_ID + "]=?;";
            
            int updateCount;
            try(PreparedStatement ps = con.prepareStatement(query)){
                ps.setString(1, location.getProvince());
                ps.setString(2, location.getCity());
                ps.setString(3, location.getStreet());
                ps.setString(4, location.getId());

                verbose(DEBUG_NAME, "Executing query: " + query);
                updateCount = ps.executeUpdate();
            }
            
            if (updateCount > 0) {
                verbose(DEBUG_NAME, "Updated location with id \"" + location.getId() + "\" successfully. "
                        + "(Rows affected [" + updateCount + "])");
                return true;
            }else{
                verbose(DEBUG_NAME, "Failed to update location with id \"" + location.getId() 
                        + "\". (Rows affected [" + updateCount + "])");
                return false;
            }
        }catch(SQLException e){
            error(DEBUG_NAME, "Failed to update location with id \"" + location.getId() + "\"", e);
            return false;
        }
    }
    
    public static final boolean deleteLocation(Location location){
        try( Connection con = connect()){
            String query = "DELETE * FROM "
                    + LOCATIONS + " WHERE ["
                    + LOCATION_ID + "]=\"" + location.getId() + "\";";
            
            int updateCount;
            try(PreparedStatement ps = con.prepareStatement(query)){
                verbose(DEBUG_NAME, "Executing query: " + query);
                updateCount = ps.executeUpdate();
            }
            if (updateCount > 0) {
                verbose(DEBUG_NAME, "Deleted location \"" + location.toString()+ "\" successfully. "
                        + "(Rows affected [" + updateCount + "])");
                return true;
            }else{
                verbose(DEBUG_NAME, "Failed to delete location \"" + location.toString()
                        + "\". (Rows affected [" + updateCount + "])");
                return false;
            }
        }catch(SQLException e){
            error(DEBUG_NAME, "Failed to delete location with ID = " + location.getId(), e);
            return false;
        }
    }

    public static ArrayList<Location> getLocations() {
        ArrayList<Location> locations = new ArrayList<>();
        try (Connection conn = connect(); Statement statement = conn.createStatement();) {
            String query = "SELECT * FROM Locations";
            verbose(DEBUG_NAME, "Executing query: " + query);
            ResultSet result = statement.executeQuery(query);
            while (result.next()) {
                Location location = new Location();
                location.setId(result.getString(LOCATION_ID));
                location.setStreet(result.getString(LOCATION_STREET));
                location.setCity(result.getString(LOCATION_CITY));
                location.setProvince(result.getString(LOCATION_PROVINCE));
                locations.add(location);
            }
        } catch (SQLException e) {
            error(DEBUG_NAME, "Failed to retrieve locations from database. Returned list is possible emppty.", e);
        }
        return locations;
    }

    public static ArrayList<Coordinate> getCoordinates() {
        ArrayList<Coordinate> coordinates = new ArrayList<>();
        try (Connection conn = connect();Statement statement = conn.createStatement()) {
            String query = "SELECT * FROM Coordinates";
            verbose(DEBUG_NAME, "Executing query: " + query);
            ResultSet result = statement.executeQuery(query);
            while (result.next()) {
                Coordinate coordinate = new Coordinate();
                coordinate.setId(result.getInt(COORDINATE_ID));
                coordinate.setProjectId(result.getString(COORDINATE_PROJECT_CODE));
                coordinate.setLatitude(result.getLong(COORDINATE_LATITUDE));
                coordinate.setLongitude(result.getLong(COORDINATE_LONGITUDE));
                coordinates.add(coordinate);
            }
        } catch (SQLException e) {
            error(DEBUG_NAME, "Failed to retrieve coordinates from database. Returned list is possible emppty.", e);
        }
        return coordinates;
    }
    
    public static boolean addContact(Contact contact){
        try(Connection con = connect()){
            String query = "INSERT INTO "
                    + CONTACTS + " (["
                    + CONTACT_ID + "], ["
                    + CONTACT_NAME + "], ["
                    + CONTACT_COMPANY + "], ["
                    + CONTACT_ADDRESS + "]) VALUES "
                    + "(?, ?, ?, ?);";
            
            int updateCount;
            try (PreparedStatement ps = con.prepareStatement(query)) {
                ps.setString(1, contact.getContactIdProperty().get());
                ps.setString(2, contact.getNameProperty().get());
                ps.setString(3, contact.getCompanyProperty().get());
                ps.setString(4, contact.getAddressProperty().get());
                
                verbose(DEBUG_NAME, "Executing query: " + query);
                updateCount = ps.executeUpdate();
            }
            
            if(updateCount > 0){
                verbose(DEBUG_NAME, "Added new contact to database. (Row Affected [" + updateCount + "])");
                return true;
            }else{
                verbose(DEBUG_NAME, "Failed to add new contact to database. (Row Affected [" + updateCount + "])");
                return false;
            }
        }catch(SQLException e){
            error(DEBUG_NAME, "Failed to add new contact to database.", e);
            return false;
        }
    }
    
    public static final boolean deleteContact(Contact contact){
        try(Connection con = connect(); Statement statement = con.createStatement()){
            String query = "DELETE FROM " 
                    + CONTACTS + " WHERE [" 
                    + CONTACT_ID + "]=\"" 
                    + contact.getContactIdProperty().get() + "\";";
            
            verbose(DEBUG_NAME, "Executing query: " + query + "");
            
            int updateCount = statement.executeUpdate(query);
            if(updateCount > 0){
                verbose(DEBUG_NAME, "Deleted contact successfully. (Rows affected [" + updateCount + "])");
                return true;
            }else{
                verbose(DEBUG_NAME, "Failed to delete contact. (Rows affected [" + updateCount + "])");
                return false;
            }
        }catch(SQLException e){
            error(DEBUG_NAME, "Failed to delete contact.", e);
            return false;
        }
    }
    
    public static ArrayList<Contact> getContacts(){
        ArrayList<Contact> contacts = new ArrayList<>();
        try(Connection con = connect();Statement statement = con.createStatement()){
            String query = "SELECT * FROM " + CONTACTS;
            verbose(DEBUG_NAME, "Executing query: " + query);
            ResultSet rs = statement.executeQuery(query);
            while(rs.next()){
                Contact contact = new Contact();
                contact.setContactId(rs.getString(CONTACT_ID));
                contact.setName(rs.getString(CONTACT_NAME));
                contact.setCompany(rs.getString(CONTACT_COMPANY));
                contact.setAddress(rs.getString(CONTACT_ADDRESS));
                contacts.add(contact);
            }
        }catch(SQLException e){
            error(DEBUG_NAME, "Failed to retrieve contacts from database. Returned list is possible empty.", e);
        }
        return contacts;
    }
    
    public static final Contact getContact(String contactId){
        try(Connection con = connect(); Statement statement = con.createStatement()){
            String query = "SELECT * FROM "
                    + CONTACTS + " WHERE ["
                    + CONTACT_ID + "]=\"" + contactId + "\";";
            verbose(DEBUG_NAME, "Executing query: " + query);
            ResultSet rs = statement.executeQuery(query);
            while(rs.next()){
                Contact contact = new Contact();
                contact.setContactId(rs.getString(CONTACT_ID));
                contact.setName(rs.getString(CONTACT_NAME));
                contact.setCompany(rs.getString(CONTACT_COMPANY));
                contact.setAddress(rs.getString(CONTACT_ADDRESS));
                return contact;
            }
        }catch(SQLException e){
            error(DEBUG_NAME, "Failed to retrieve contact from database. Return value is null.", e);
        }
        return null;
    }
    
    /**
     * Retrieves all Contact of the Project.
     * 
     * @param projectId
     * @return 
     */
    public static final ArrayList<Contact> getContactsOfProject(String projectId){
        ArrayList<Contact> contacts = new ArrayList<>();
        try(Connection con = connect(); Statement statement = con.createStatement()){
            String query = "SELECT ["
                    + CONTACTS + "].* FROM ["
                    + CONTACTS + "] INNER JOIN ["
                    + PROJECT_CONTACTS + "] ON ["
                    + PROJECT_CONTACTS + "].["
                    + PC_CONTACT_ID + "] = ["
                    + CONTACTS + "].["
                    + CONTACT_ID + "] WHERE ["
                    + PROJECT_CONTACTS + "].["
                    + PC_PROJECT_ID + "] = \""
                    + projectId + "\";";
            
            verbose(DEBUG_NAME, "Executing query: " + query);
            ResultSet result = statement.executeQuery(query);
            while(result.next()){
                Contact contact = new Contact();
                contact.setContactId(result.getString(CONTACT_ID));
                contact.setName(result.getString(CONTACT_NAME));
                contact.setCompany(result.getString(CONTACT_COMPANY));
                contact.setAddress(result.getString(CONTACT_ADDRESS));
                contacts.add(contact);
            }
        }catch(SQLException e){
            error(DEBUG_NAME, "Failed to retrieve contacts of project \"" + projectId + "\". Returned list is possible empty.", e);
        }
        return contacts;
    }
    
    public static final boolean addPhoneNumbers(ObservableList<PhoneNumber> numbers){
        String format = "INSERT INTO " + PHONE_NUMBERS + " ([" + PHONE_USER_ID + "],[" + PHONE_NUMBER + "],["+ PHONE_TYPE +"]) VALUES (\"%s\", \"%s\", \"%s\");\n";
        if(numbers.isEmpty()){
            return false;
        }else{
            StringBuilder queries = new StringBuilder();
            numbers.stream().forEach(num -> {
                queries.append(String.format(format, num.getUserIdProperty().get(), num.getPhoneNumberProperty().get(), num.getTypeProperty().get()));
            });
            try(Connection con = connect(); Statement statement = con.createStatement()){
                verbose(DEBUG_NAME, "Executing queries:\n" + queries.toString());
                int updateCount = statement.executeUpdate(queries.toString());
                if(updateCount > 0){
                    verbose(DEBUG_NAME, "Added phone numbers to database successfully. "
                            + "(Row affected [" + updateCount + "])");
                    return true;
                }else{
                    verbose(DEBUG_NAME, "Failed to add phone numbers to database. "
                            + "(Row affected [" + updateCount + "])");
                    return false;
                }
            }catch(SQLException e){
                error(DEBUG_NAME, "Failed to add phone numbers to database.", e);
                return false;
            }
        }
    }
    
    public static final boolean addEmails(ObservableList<Email> emails){
        String format = "INSERT INTO " + EMAILS + " ([" + EMAIL_USER_ID + "],[" + EMAIL_ADDRESS + "],["+ EMAIL_TYPE +"]) VALUES (\"%s\", \"%s\", \"%s\");\n";
        if(emails.isEmpty()){
            return false;
        }else{
            StringBuilder queries = new StringBuilder();
            emails.stream().forEach(email -> {
                queries.append(String.format(format, email.getUserIdProperty().get(), email.getEmailAddressProperty().get(), email.getTypeProperty().get()));
            });
            try(Connection con = connect(); Statement statement = con.createStatement()){
                verbose(DEBUG_NAME, "Executing queries:\n" + queries.toString());
                int updateCount = statement.executeUpdate(queries.toString());
                if(updateCount > 0){
                    verbose(DEBUG_NAME, "Added emails to database successfully. "
                            + "(Row affected [" + updateCount + "])");
                    return true;
                }else{
                    verbose(DEBUG_NAME, "Failed to add emails to database. "
                            + "(Row affected [" + updateCount + "])");
                    return false;
                }
            }catch(SQLException e){
                error(DEBUG_NAME, "Failed to add emails to database.", e);
                return false;
            }
        }
    }
    
    public static final ArrayList<ProjectContact> getProjectContacts(){
        ArrayList<ProjectContact> contacts = new ArrayList<>();
        try(Connection con = connect(); Statement statement = con.createStatement()){
            String query = "SELECT * FROM " + PROJECT_CONTACTS;
            verbose(DEBUG_NAME, "Executing query: " + query);
            ResultSet rs = statement.executeQuery(query);
            while(rs.next()){
                ProjectContact pc = new ProjectContact();
                pc.setId(rs.getInt(PC_ID));
                pc.setProjectId(rs.getString(PC_PROJECT_ID));
                pc.setContactId(rs.getString(PC_CONTACT_ID));
                contacts.add(pc);
            }
        }catch(SQLException e){
            error(DEBUG_NAME, "Failed to retrieve project contacts. Returned list is possible empty.", e);
        }
        return contacts;
    }

    public static final boolean addProjectUpdate(ProjectUpdate update){
        try(Connection con = connect()){
            String query = "INSERT INTO "
                    + PROJECT_UPDATES + " (["
                    + PU_DATE + "], [" // (1)
                    + PU_PROJECT_ID + "], [" // (2)
                    + PU_REMARKS + "], [" // (3)
                    + PU_LEVEL + "], [" // (4)
                    + PU_STATUS + "], [" // (5)
                    + PU_ATTACHMENT + "]) " // (6)
                    + "VALUES (?, ?, ?, ?, ?, ?)";
            
            int updateCount;
            try(PreparedStatement ps = con.prepareStatement(query)){
                LocalDateTime ldt = update.getDateCreated();
                Timestamp timeStamp = Timestamp.valueOf(ldt);
                ps.setTimestamp(1, timeStamp);
                ps.setString(2, update.getProjectId().get());
                ps.setString(3, update.getNotes().get());
                ps.setString(4, update.getLevel().get());
                ps.setString(5, update.getStatus().get());
                ps.setInt(6, update.hasAttachment().get() ? 1 : 0);

                verbose(DEBUG_NAME, "Executing query: " + query);
                updateCount = ps.executeUpdate();
            }
            
            if (updateCount > 0) {
                verbose(DEBUG_NAME, "Added new project update successfully. "
                        + "(Rows affected [" + updateCount + "])");
                return true;
            }else{
                verbose(DEBUG_NAME, "Failed to add project update."
                        + ". (Rows affected [" + updateCount + "])");
                return false;
            }
        }catch(SQLException e){
            error(DEBUG_NAME, "Failed to add project update.", e);
            return false;
        }
    }
    
    public static final boolean deleteProjectUpdate(ProjectUpdate projectUpdate){
        try(Connection con = connect()){
            // NOTE: We use the date instead of ID for deletion
            String query = "DELETE FROM "
                    + PROJECT_UPDATES + " WHERE ["
                    + PU_DATE + "]=\""
                    + Timestamp.valueOf(projectUpdate.getDateCreated()) + "\";";
            
            int updateCount;
            try(PreparedStatement ps = con.prepareStatement(query)){
                verbose(DEBUG_NAME, "Executing query: " + query);
                updateCount = ps.executeUpdate();
            }
            if (updateCount > 0) {
                verbose(DEBUG_NAME, "Deleted project update successfully. "
                        + "(Rows affected [" + updateCount + "])");
                return true;
            }else{
                verbose(DEBUG_NAME, "Failed to delete project update."
                        + ". (Rows affected [" + updateCount + "])");
                return false;
            }
        }catch(SQLException e){
            error(DEBUG_NAME, "Failed to delete project update.", e);
            return false;
        }
    }
    
    public static final ArrayList<ProjectUpdate> getProjectUpdates(String projectId){
        ArrayList<ProjectUpdate> updates = new ArrayList<>();
        try(Connection con = connect(); Statement statement = con.createStatement()){
            String query = "SELECT * FROM "
                    + PROJECT_UPDATES + " WHERE ["
                    + PU_PROJECT_ID + "]=\""
                    + projectId + "\";";
            verbose(DEBUG_NAME, "Executing query: " + query);
            ResultSet results = statement.executeQuery(query);
            while(results.next()){
                ProjectUpdate update = new ProjectUpdate();
                update.setId(results.getInt(PU_ID));
                
                Timestamp timeStamp = results.getTimestamp(PU_DATE);
                update.setDateCreated(timeStamp.toLocalDateTime());
                update.setNotes(results.getString(PU_REMARKS));
                update.setLevel(results.getString(PU_LEVEL));
                update.setStatus(results.getString(PU_STATUS));
                update.setHasAttachment(results.getInt(PU_ATTACHMENT) == 1);
                updates.add(update);
            }
        }catch(SQLException e){
            error(DEBUG_NAME, "Failed to retrieve project updates from database.", e);
        }
        return updates;
    }
    
    public static final boolean addAttachments(int updateId, ObservableList<Attachment> attachments){
        String format = "INSERT INTO " + ATTACHMENTS + " ([" + AT_UPDATE_ID + "],[" + AT_PATH + "],["+ AT_TYPE +"]) VALUES (%d, \"%s\", \"%s\");\n";
        if(attachments == null || attachments.isEmpty()){
            return false;
        }else{
            StringBuilder queries = new StringBuilder();
            attachments.stream().forEach(attachment -> {
                queries.append(String.format(format, updateId, attachment.getPathProperty().get(), attachment.getTypeProperty().get()));
            });
            try(Connection con = connect(); Statement statement = con.createStatement()){
                
                verbose(DEBUG_NAME, "Executing queries:\n" + queries.toString());
                
                int updateCount = statement.executeUpdate(queries.toString());
                
                if(updateCount > 0){
                    verbose(DEBUG_NAME, "Added attachments to database successfully. "
                            + "(Row affected [" + updateCount + "])");
                    return true;
                }else{
                    verbose(DEBUG_NAME, "Failed to add attachments to database. "
                            + "(Row affected [" + updateCount + "])");
                    return false;
                }
            }catch(SQLException e){
                error(DEBUG_NAME, "Failed to add attachments to database.", e);
                return false;
            }
        }
    }
    
    public static final boolean clearTable(String tableName){
        try(Connection con = connect(); Statement statement = con.createStatement()){
            String query = "DELETE * FROM " + tableName + ";";
            verbose(DEBUG_NAME, "Executing query: " + query);
            int count = statement.executeUpdate(query);
            if(count > 0){
                verbose(DEBUG_NAME, "Deleted all entry from table " + tableName);
                return true;
            }else{
                verbose(DEBUG_NAME, "Failed to delete all entry from table " + tableName);
                return false;
            }
        }catch(SQLException e){
            error(DEBUG_NAME, "Failed to delete entries from table " + tableName, e);
            return false;
        }
    }

    public static final int getIdOf(ProjectUpdate pu){
        try(Connection con = connect()){
            String query = "SELECT "
                    + PU_ID + " FROM "
                    + PROJECT_UPDATES + " WHERE ["
                    + PU_DATE + "] = ?;";
            
            ResultSet result;
            try(PreparedStatement ps = con.prepareStatement(query)){
                ps.setTimestamp(1, Timestamp.valueOf(pu.getDateCreated()));
                result = ps.executeQuery();
            }
            if(result == null){
                return -1;
            }else{
                if(result.next()){
                    return result.getInt(PU_ID);
                }else{
                    return -1;
                }
            }
        }catch(SQLException e){
            error(DEBUG_NAME, "Failed to retrieve database id of specified project update.", e);
            return -1;
        }
    }

    private static Connection connect() throws SQLException {
        Connection conn = DriverManager.getConnection(DB_DRIVER);
        return conn;
    }
    
}
