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
import static com.gemseeker.pmma.utils.Log.*;
import java.sql.Timestamp;
import java.time.LocalDateTime;

import com.gemseeker.pmma.utils.Log;
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

    /*==================================================================*
     *                                                                  *
     *                              Projects                            *
     *                                                                  *
     *==================================================================*/

    public static int addProject(Project project) {
        try (Connection conn = connect()) {
            String query = "INSERT INTO "
                    + PROJECTS + " (["
                    + PROJECT_NAME + "], [" // (1)
                    + PROJECT_DATE_STARTED + "], [" // (2)
                    + PROJECT_DATE_TO_FINISH + "], [" // (3)
                    + PROJECT_STATUS + "])" // (4)
                    + " VALUES (?, ?, ?, ?);";
            
            int updateCount;
            try(PreparedStatement ps = conn.prepareStatement(query)){
                ps.setString(1, project.getNameProperty().get());//(1)

                //(2)
                // check if Date is not null
                LocalDate ls = project.getDateCreated();
                Calendar started = Calendar.getInstance();
                // I subtracted 1 from ls.getMonthValue because Calendar and LocalData
                // has different month values. Calendar's month is numbered from 0-11
                // while LocalDate's month is numbered from 1-12
                started.set(ls.getYear(), ls.getMonth().getValue(), ls.getDayOfMonth());
                ps.setDate(2, new Date(started.getTime().getTime()));

                //(3)
                LocalDate lc = project.getDateToFinish();
                Calendar completion = Calendar.getInstance();
                completion.set(lc.getYear(), lc.getMonth().getValue(), lc.getDayOfMonth());
                ps.setDate(3, new Date(completion.getTime().getTime()));

                //(4)
                ps.setString(4, project.getStatusValue());
                
                v(DEBUG_NAME, "Executing query: " + query);

                updateCount = ps.executeUpdate();
            }

            Log.v(DEBUG_NAME, "Query result: " + updateCount);
            if (updateCount <= 0) return -1;
        } catch (SQLException e) {
            e(DEBUG_NAME, "Failed to add project \"" + project.getNameProperty().get() + "\" to database.", e);
            return -1;
        }

        // returning the id of the project
        return getLargestIdValue(PROJECT_ID, PROJECTS);
    }
    
    public static boolean deleteProject(int id){
        try(Connection con = connect()){
            String query = "DELETE FROM " + PROJECTS + " WHERE [" + PROJECT_ID + "]=" + id + ";";
            
            int updateCount;
            try(PreparedStatement ps = con.prepareStatement(query)){
                v(DEBUG_NAME, "Executing query: " + query);
                updateCount = ps.executeUpdate();
            }

            Log.v(DEBUG_NAME, "Query result: " + updateCount);
            return updateCount > 0;
        }catch(SQLException e){
            e(DEBUG_NAME, "Failed  to delete Project with ID = " + id, e);
            return false;
        }
    }
    
    public static boolean updateProject(Project project){
        try(Connection con = connect()){
            String query = "UPDATE "
                        + PROJECTS + " SET ["
                        + PROJECT_NAME + "]=?, [" // (1)
                        + PROJECT_DATE_STARTED + "]=?, [" // (2)
                        + PROJECT_DATE_TO_FINISH + "]=?, [" // (3)
                        + PROJECT_STATUS + "]=? WHERE [" // (4)
                        + PROJECT_ID + "]=?;"; // (5)
            
            int updateCount;
            try(PreparedStatement ps = con.prepareStatement(query)){
            
                ps.setString(1, project.getNameProperty().get()); // (1)

                LocalDate ls = project.getDateCreated();
                Calendar started = Calendar.getInstance();
                started.set(ls.getYear(), ls.getMonthValue() - 1, ls.getDayOfMonth());
                ps.setDate(2, new Date(started.getTime().getTime())); // (3)

                LocalDate lf = project.getDateToFinish();
                Calendar completion = Calendar.getInstance();
                completion.set(lf.getYear(), lf.getMonthValue() - 1, lf.getDayOfMonth());
                ps.setDate(3, new Date(completion.getTime().getTime())); // (4)

                ps.setString(4, project.getStatusValue()); // (5)
                ps.setInt(5, project.getIdProperty().get()); // (6)
                
                v(DEBUG_NAME, "Executing query: " + query);
                updateCount = ps.executeUpdate();
            }

            Log.v(DEBUG_NAME, "Query result: " + updateCount);
            return updateCount > 0;
        }catch(SQLException e){
            e(DEBUG_NAME, "Failed to update Project with ID = " + project.getIdProperty().get() + ".", e);
            return false;
        }
    }

    public static ArrayList<Project> getProjects() {
        ArrayList<Project> projects = new ArrayList<>();
        try (Connection conn = connect(); Statement statement = conn.createStatement();) {
            String query = "SELECT * FROM " + PROJECTS;
            v(DEBUG_NAME, "Executing query: " + query);

            // first query to retrieve all Project from database
            try (ResultSet result = statement.executeQuery(query)) {
                while (result.next()) {
                    Project project = new Project();
                    project.setId(result.getInt(PROJECT_ID));
                    project.setName(result.getString(PROJECT_NAME));

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
            }

            // next set their location (if Project has one)
            String locQuery =
                    "SELECT Locations.* " +
                    "FROM Projects " +
                            "INNER JOIN (Locations INNER JOIN ProjectLocation " +
                            "ON Locations.ID = ProjectLocation.[Location ID]) " +
                            "ON Projects.ID = ProjectLocation.[Project ID] " +
                    "WHERE Projects.ID=?;";
            try(PreparedStatement ps = conn.prepareStatement(locQuery)) {
                for (Project p: projects) {
                    ps.setInt(1, p.getIdProperty().get());
                    ResultSet set = ps.executeQuery();
                    if (set.next()) {
                        Location loc = new Location();
                        loc.setId(set.getInt(LOCATION_ID));
                        loc.setProvince(set.getString(LOCATION_PROVINCE));
                        loc.setMunicipality(set.getString(LOCATION_MUNICIPALITY));
                        loc.setBarangay(set.getString(LOCATION_BARANGAY));
                        loc.setOtherLocationInfo(set.getString(LOCATION_OTHER));
                        loc.setLongitude(set.getString(LOCATION_LONGITUDE));
                        loc.setLatitude(set.getString(LOCATION_LATITUDE));
                        p.setLocation(loc);
                    }
                    set.close();
                }
            }
        } catch (SQLException e) {
            e(DEBUG_NAME, "Failed to retrieve projects from database. The returned list is possibly empty.", e);
        }
        return projects;
    }

    /*==================================================================*
     *                                                                  *
     *                              History                             *
     *                                                                  *
     *==================================================================*/

    public static final int addHistory(History history) {
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

                v(DEBUG_NAME, "Executing query: " + query);
                updateCount = ps.executeUpdate();
            }

            Log.v(DEBUG_NAME, "Query result: " + updateCount);
            if (updateCount <= 0) return -1;
        }catch(SQLException e){
            e(DEBUG_NAME, "Failed to add History to database.", e);
            return -1;
        }

        return getLargestIdValue(HISTORY_ID, HISTORIES);
    }

    public static ArrayList<History> getHistories() {
        ArrayList<History> histories = new ArrayList<>();
        try (Connection conn = connect(); Statement statement = conn.createStatement();) {
            String query = "SELECT * FROM " + HISTORIES;
            v(DEBUG_NAME, "Executing query: " + query);
            try(ResultSet result = statement.executeQuery(query)) {
                while (result.next()) {
                    History history = new History();
                    history.setId(result.getInt(HISTORY_ID));

                    Timestamp timeStamp = result.getTimestamp(HISTORY_DATE_CREATED);
                    history.setDate(timeStamp.toLocalDateTime());
                    history.setNotes(result.getString(HISTORY_NOTES));
                    histories.add(history);
                }
            }
        } catch (SQLException e) {
            e(DEBUG_NAME, "Failed to retrieve histories from database. The return list is possible empty.", e);
        }
        return histories;
    }
    
    public static final boolean addLocation(Location location) {
        try (Connection conn = connect()) {
            String query = "INSERT INTO ["
                    + LOCATIONS + "] (["
                    + LOCATION_ID + "], ["
                    + LOCATION_BARANGAY + "], ["
                    + LOCATION_MUNICIPALITY + "], ["
                    + LOCATION_PROVINCE + "])"
                    + " VALUES (?, ?, ?, ?);";
            
            int updateCount;
            try(PreparedStatement ps = conn.prepareStatement(query)){
                ps.setInt(1, location.getId());
                ps.setString(2, location.getBarangay());
                ps.setString(3, location.getMunicipality());
                ps.setString(4, location.getProvince());
                
                v(DEBUG_NAME, "Executing query: " + query);
                updateCount = ps.executeUpdate();
            }

            Log.v(DEBUG_NAME, "Query result: " + updateCount);
            return updateCount > 0;
        } catch (SQLException e) {
            e(DEBUG_NAME, "Failed to add location to database.", e);
            return false;
        }
    }

    public static final boolean updateLocation(Location location){
        try(Connection con = connect()){
            String query = "UPDATE "
                    + LOCATIONS + " SET ["
                    + LOCATION_PROVINCE + "]=?, ["
                    + LOCATION_MUNICIPALITY + "]=?, ["
                    + LOCATION_BARANGAY + "]=? WHERE ["
                    + LOCATION_ID + "]=?;";
            
            int updateCount;
            try(PreparedStatement ps = con.prepareStatement(query)){
                ps.setString(1, location.getProvince());
                ps.setString(2, location.getMunicipality());
                ps.setString(3, location.getBarangay());
                ps.setInt(4, location.getId());

                v(DEBUG_NAME, "Executing query: " + query);
                updateCount = ps.executeUpdate();
            }

            Log.v(DEBUG_NAME, "Query result: " + updateCount);
            return updateCount > 0;
        }catch(SQLException e){
            e(DEBUG_NAME, "Failed to update location with id \"" + location.getId() + "\"", e);
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
                v(DEBUG_NAME, "Executing query: " + query);
                updateCount = ps.executeUpdate();
            }

            Log.v(DEBUG_NAME, "Query result: " + updateCount);
            return updateCount > 0;
        }catch(SQLException e){
            e(DEBUG_NAME, "Failed to delete location with ID = " + location.getId(), e);
            return false;
        }
    }

    public static boolean checkLocationExists(Location match) {
        try(Connection con = connect(); Statement statement = con.createStatement()) {
            String query = "SELECT COUNT(*) AS Count FROM " + LOCATIONS + " WHERE " +
                    LOCATION_PROVINCE + "=" + match.getProvince() + " AND " +
                    LOCATION_MUNICIPALITY + "=" + match.getMunicipality() + " AND " +
                    LOCATION_BARANGAY + "=" + match.getBarangay();

            Log.v(DEBUG_NAME, "Finding location match: " + query);

            try(ResultSet result = statement.executeQuery(query)) {
                if (result.next()) {
                    int count = result.getInt("Count");
                    return count > 0;
                }
            }
        } catch (SQLException e) {
            Log.e(DEBUG_NAME, "Failed to execute query.", e);
        }
        return false;
    }

    public static ArrayList<Location> getLocations() {
        ArrayList<Location> locations = new ArrayList<>();
        try (Connection conn = connect(); Statement statement = conn.createStatement();) {
            String query = "SELECT * FROM Locations";
            v(DEBUG_NAME, "Executing query: " + query);
            ResultSet result = statement.executeQuery(query);
            while (result.next()) {
                Location location = new Location();
                location.setId(result.getInt(LOCATION_ID));
                location.setBarangay(result.getString(LOCATION_BARANGAY));
                location.setMunicipality(result.getString(LOCATION_MUNICIPALITY));
                location.setProvince(result.getString(LOCATION_PROVINCE));
                locations.add(location);
            }
        } catch (SQLException e) {
            e(DEBUG_NAME, "Failed to retrieve locations from database. Returned list is possible emppty.", e);
        }
        return locations;
    }

    public static ArrayList<Coordinate> getCoordinates() {
        ArrayList<Coordinate> coordinates = new ArrayList<>();
        try (Connection conn = connect();Statement statement = conn.createStatement()) {
            String query = "SELECT * FROM Coordinates";
            v(DEBUG_NAME, "Executing query: " + query);
            ResultSet result = statement.executeQuery(query);
            while (result.next()) {
                Coordinate coordinate = new Coordinate();
                coordinate.setId(result.getInt(COORDINATE_ID));
                coordinate.setProjectId(result.getString(COORDINATE_PROJECT_ID));
                coordinate.setLatitude(result.getLong(COORDINATE_LATITUDE));
                coordinate.setLongitude(result.getLong(COORDINATE_LONGITUDE));
                coordinates.add(coordinate);
            }
        } catch (SQLException e) {
            e(DEBUG_NAME, "Failed to retrieve coordinates from database. Returned list is possible emppty.", e);
        }
        return coordinates;
    }
    
    public static int addContact(Contact contact){
        try(Connection con = connect()){
            String query = "INSERT INTO "
                    + CONTACTS + " (["
                    + CONTACT_FIRSTNAME + "], ["
                    + CONTACT_LASTNAME + "], ["
                    + CONTACT_COMPANY + "], ["
                    + CONTACT_ADDRESS + "]) VALUES "
                    + "(?, ?, ?, ?);";
            
            int updateCount;
            try (PreparedStatement ps = con.prepareStatement(query)) {
                ps.setString(1, contact.getFirstNameProperty().get());
                ps.setString(2, contact.getLastNameProperty().get());
                ps.setString(3, contact.getCompanyProperty().get());
                ps.setString(4, contact.getAddressProperty().get());
                
                v(DEBUG_NAME, "Executing query: " + query);
                updateCount = ps.executeUpdate();
            }

            Log.v(DEBUG_NAME, "Query result: " + updateCount);
            if (updateCount <= 0) return -1;
        }catch(SQLException e){
            e(DEBUG_NAME, "Failed to add new contact to database.", e);
            return -1;
        }

        return getLargestIdValue(CONTACT_ID, CONTACTS);
    }
    
    public static final boolean deleteContact(Contact contact){
        try(Connection con = connect(); Statement statement = con.createStatement()){
            String query = "DELETE FROM " 
                    + CONTACTS + " WHERE [" 
                    + CONTACT_ID + "]="
                    + contact.getContactIdProperty().get() + ";";
            
            v(DEBUG_NAME, "Executing query: " + query + "");
            
            int updateCount = statement.executeUpdate(query);

            Log.v(DEBUG_NAME, "Query result: " + updateCount);
            return updateCount > 0;
        }catch(SQLException e){
            e(DEBUG_NAME, "Failed to delete contact.", e);
            return false;
        }
    }
    
    public static ArrayList<Contact> getContacts(){
        ArrayList<Contact> contacts = new ArrayList<>();
        try(Connection con = connect();Statement statement = con.createStatement()){
            String query = "SELECT * FROM " + CONTACTS;
            v(DEBUG_NAME, "Executing query: " + query);
            ResultSet rs = statement.executeQuery(query);
            while(rs.next()){
                Contact contact = new Contact();
                contact.setContactId(rs.getInt(CONTACT_ID));
                contact.setFirstName(rs.getString(CONTACT_FIRSTNAME));
                contact.setLastName(rs.getString(CONTACT_LASTNAME));
                contact.setCompany(rs.getString(CONTACT_COMPANY));
                contact.setAddress(rs.getString(CONTACT_ADDRESS));
                contacts.add(contact);
            }
        }catch(SQLException e){
            e(DEBUG_NAME, "Failed to retrieve contacts from database. Returned list is possible empty.", e);
        }
        return contacts;
    }
    
    public static  Contact getContact(int contactId){
        try(Connection con = connect(); Statement statement = con.createStatement()){
            String query = "SELECT * FROM "
                    + CONTACTS + " WHERE ["
                    + CONTACT_ID + "]=" + contactId + ";";
            v(DEBUG_NAME, "Executing query: " + query);
            ResultSet rs = statement.executeQuery(query);
            if (rs.next()){
                Contact contact = new Contact();
                contact.setContactId(rs.getInt(CONTACT_ID));
                contact.setFirstName(rs.getString(CONTACT_FIRSTNAME));
                contact.setLastName(rs.getString(CONTACT_LASTNAME));
                contact.setCompany(rs.getString(CONTACT_COMPANY));
                contact.setAddress(rs.getString(CONTACT_ADDRESS));
                return contact;
            }
        }catch(SQLException e){
            e(DEBUG_NAME, "Failed to retrieve contact from database. Return value is null.", e);
        }
        return null;
    }
    
    /**
     * Retrieves all Contact of the Project.
     * 
     * @param projectId
     * @return 
     */
    public static ArrayList<Contact> getProjectContacts(String projectId){
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
            
            v(DEBUG_NAME, "Executing query: " + query);
            ResultSet result = statement.executeQuery(query);
            while(result.next()){
                Contact contact = new Contact();
                contact.setContactId(result.getInt(CONTACT_ID));
                contact.setFirstName(result.getString(CONTACT_FIRSTNAME));
                contact.setLastName(result.getString(CONTACT_LASTNAME));
                contact.setCompany(result.getString(CONTACT_COMPANY));
                contact.setAddress(result.getString(CONTACT_ADDRESS));
                contacts.add(contact);
            }
        }catch(SQLException e){
            e(DEBUG_NAME, "Failed to retrieve contacts of project \"" + projectId + "\". Returned list is possible empty.", e);
        }
        return contacts;
    }
    
    public static boolean addPhoneNumbers(ObservableList<PhoneNumber> numbers){
        String format = "INSERT INTO " + PHONE_NUMBERS + " ([" + PHONE_CONTACT_ID + "],[" + PHONE_NUMBER + "],["+ PHONE_TYPE +"]) VALUES (\"%d\", \"%s\", \"%s\");\n";
        if(numbers.isEmpty()){
            return false;
        }else{
            StringBuilder queries = new StringBuilder();
            numbers.stream().forEach(num -> {
                queries.append(String.format(format, num.getContactIdProperty().get(), num.getPhoneNumberProperty().get(), num.getTypeProperty().get()));
            });
            try(Connection con = connect(); Statement statement = con.createStatement()){
                v(DEBUG_NAME, "Executing queries:\n" + queries.toString());
                int updateCount = statement.executeUpdate(queries.toString());

                Log.v(DEBUG_NAME, "Query result: " + updateCount);
                return updateCount > 0;
            }catch(SQLException e){
                e(DEBUG_NAME, "Failed to add phone numbers to database.", e);
                return false;
            }
        }
    }
    
    public static boolean addEmails(ObservableList<Email> emails){
        String format = "INSERT INTO " + EMAILS + " ([" + EMAIL_CONTACT_ID + "],[" + EMAIL_ADDRESS + "],["+ EMAIL_TYPE +"]) VALUES (\"%d\", \"%s\", \"%s\");\n";
        if(emails.isEmpty()){
            return false;
        }else{
            StringBuilder queries = new StringBuilder();
            emails.stream().forEach(email -> {
                queries.append(String.format(format, email.getContactIdProperty().get(), email.getEmailAddressProperty().get(), email.getTypeProperty().get()));
            });
            try(Connection con = connect(); Statement statement = con.createStatement()){
                v(DEBUG_NAME, "Executing queries:\n" + queries.toString());
                int updateCount = statement.executeUpdate(queries.toString());

                Log.v(DEBUG_NAME, "Query result: " + updateCount);
                return updateCount > 0;
            }catch(SQLException e){
                e(DEBUG_NAME, "Failed to add emails to database.", e);
                return false;
            }
        }
    }
    
    public static ArrayList<ProjectContact> getProjectContacts(){
        ArrayList<ProjectContact> contacts = new ArrayList<>();
        try(Connection con = connect(); Statement statement = con.createStatement()){
            String query = "SELECT * FROM " + PROJECT_CONTACTS;
            v(DEBUG_NAME, "Executing query: " + query);
            ResultSet rs = statement.executeQuery(query);
            while(rs.next()){
                ProjectContact pc = new ProjectContact();
                pc.setId(rs.getInt(PC_ID));
                pc.setProjectId(rs.getInt(PC_PROJECT_ID));
                pc.setContactId(rs.getInt(PC_CONTACT_ID));
                contacts.add(pc);
            }
        }catch(SQLException e){
            e(DEBUG_NAME, "Failed to retrieve project contacts. Returned list is possible empty.", e);
        }
        return contacts;
    }

    public static boolean addProjectUpdate(ProjectUpdate update){
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
                ps.setInt(2, update.getProjectId().get());
                ps.setString(3, update.getNotes().get());
                ps.setString(4, update.getLevel().get());
                ps.setString(5, update.getStatus().get());
                ps.setInt(6, update.hasAttachment().get() ? 1 : 0);

                v(DEBUG_NAME, "Executing query: " + query);
                updateCount = ps.executeUpdate();
            }

            Log.v(DEBUG_NAME, "Query result: " + updateCount);
            return updateCount > 0;
        }catch(SQLException e){
            Log.e(DEBUG_NAME, "Failed to add project update.", e);
            return false;
        }
    }
    
    public static boolean deleteProjectUpdate(ProjectUpdate projectUpdate){
        try(Connection con = connect()){
            // NOTE: We use the date instead of ID for deletion
            String query = "DELETE FROM "
                    + PROJECT_UPDATES + " WHERE ["
                    + PU_DATE + "]=\""
                    + Timestamp.valueOf(projectUpdate.getDateCreated()) + "\";";
            
            int updateCount;
            try(PreparedStatement ps = con.prepareStatement(query)){
                v(DEBUG_NAME, "Executing query: " + query);
                updateCount = ps.executeUpdate();
            }

            Log.v(DEBUG_NAME, "Query result: " + updateCount);
            return updateCount > 0;
        }catch(SQLException e){
            e(DEBUG_NAME, "Failed to delete project update.", e);
            return false;
        }
    }
    
    public static ArrayList<ProjectUpdate> getProjectUpdates(int projectId){
        ArrayList<ProjectUpdate> updates = new ArrayList<>();
        try(Connection con = connect(); Statement statement = con.createStatement()){
            String query = "SELECT * FROM "
                    + PROJECT_UPDATES + " WHERE ["
                    + PU_PROJECT_ID + "]=\""
                    + projectId + "\";";
            v(DEBUG_NAME, "Executing query: " + query);
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
            e(DEBUG_NAME, "Failed to retrieve project updates from database.", e);
        }
        return updates;
    }


    public static boolean addAttachments(int updateId, ObservableList<Attachment> attachments){
        String format = "INSERT INTO " + ATTACHMENTS + " ([" + AT_UPDATE_ID + "],[" + AT_PATH + "],["+ AT_TYPE +"]) VALUES (%d, \"%s\", \"%s\");\n";
        if(attachments == null || attachments.isEmpty()){
            return false;
        }else{
            StringBuilder queries = new StringBuilder();
            attachments.stream().forEach(attachment -> {
                queries.append(String.format(format, updateId, attachment.getPathProperty().get(), attachment.getTypeProperty().get()));
            });
            try(Connection con = connect(); Statement statement = con.createStatement()){
                
                v(DEBUG_NAME, "Executing queries:\n" + queries.toString());
                
                int updateCount = statement.executeUpdate(queries.toString());

                Log.v(DEBUG_NAME, "Query result: " + updateCount);
                return updateCount > 0;
            }catch(SQLException e){
                e(DEBUG_NAME, "Failed to add attachments to database.", e);
                return false;
            }
        }
    }
    
    public static boolean clearTable(String tableName){
        try(Connection con = connect(); Statement statement = con.createStatement()){
            String query = "DELETE * FROM " + tableName + ";";
            v(DEBUG_NAME, "Executing query: " + query);
            int count = statement.executeUpdate(query);
            if(count > 0){
                v(DEBUG_NAME, "Deleted all entry from table " + tableName);
                return true;
            }else{
                v(DEBUG_NAME, "Failed to delete all entry from table " + tableName);
                return false;
            }
        }catch(SQLException e){
            e(DEBUG_NAME, "Failed to delete entries from table " + tableName, e);
            return false;
        }
    }

    public static int getIdOf(ProjectUpdate pu){
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
            e(DEBUG_NAME, "Failed to retrieve database id of specified project update.", e);
            return -1;
        }
    }

    /**
     * Assuming that ALL primary keys in the database are set as AUTONUMBER (for MS Access) or AUTOINCREMENT,
     * AND ALL primary key is a number, this function/method will retrieve that last inserted entry to any table.
     *
     * @param idColumn The primary key name column.
     * @param table The table to query
     * @return ID of the last entry inserted
     */
    private static int getLargestIdValue(String idColumn, String table) {
        try(Connection con = connect(); Statement statement = con.createStatement()) {
            ArrayList<Integer> ids = new ArrayList<>();
            String query = "SELECT " + idColumn + " FROM " + table + ";";
            try(ResultSet rs = statement.executeQuery(query)) {
                while (rs.next()) {
                    ids.add(rs.getInt(idColumn));
                }
            }

            Log.v(DEBUG_NAME, "Looking for largest key value from table " + table);
            int largest = -1;
            for (int i: ids) {
                if (i > largest) {
                    largest = i;
                }
            }
            Log.v(DEBUG_NAME, "Returning value: " + largest);
            return largest;
        }catch (SQLException e) {
            Log.e(DEBUG_NAME, "Failed to execute query: ", e);
            return -1;
        }
    }

    /**
     * Creates a connection to the database.
     *
     * @return
     * @throws SQLException
     */
    private static Connection connect() throws SQLException {
        return DriverManager.getConnection(DB_DRIVER);
    }
    
}
