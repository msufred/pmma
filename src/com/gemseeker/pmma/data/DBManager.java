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
import static java.util.Calendar.DAY_OF_MONTH;
import static java.util.Calendar.MONTH;
import static java.util.Calendar.YEAR;

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
            PreparedStatement ps = conn.prepareStatement("INSERT INTO "
                    + PROJECTS + " (["
                    + PROJECT_CODE + "], [" // (1)
                    + PROJECT_NAME + "], [" // (2)
                    + PROJECT_LOCATION_ID + "], [" // (3)
                    + PROJECT_DATE_STARTED + "], [" // (4)
                    + PROJECT_DATE_TO_FINISH + "], [" // (5)
                    + PROJECT_STATUS + "])" // (6)
                    + " VALUES (?, ?, ?, ?, ?, ?);");
            
            ps.setString(1, project.getIdValue());
            ps.setString(2, project.getNameValue());
            ps.setString(3, project.getLocationIdValue());
            
            // check if Date is not null
            LocalDate ls = project.getDateCreated();
            Calendar started = Calendar.getInstance();
            started.set(ls.getYear(), ls.getMonthValue(), ls.getDayOfMonth());
            ps.setDate(4, new Date(started.getTime().getTime()));
            
            LocalDate lc = project.getDateToFinish();
            Calendar completion = Calendar.getInstance();
            completion.set(lc.getYear(), lc.getMonthValue(), lc.getDayOfMonth());
            ps.setDate(5, new Date(completion.getTime().getTime()));
            
            ps.setString(6, project.getStatusValue());
            
            int updateCount = ps.executeUpdate();
            if (updateCount > 0) {
                System.out.println("Added new Project to database successfully.");
                return true;
            }else{
                return false;
            }
        } catch (SQLException e) {
            System.err.println("Failed to add project to database.\n\t" + e);
            return false;
        }
    }
    
    public static final boolean deleteProject(String id){
        try(Connection con = connect()){
            PreparedStatement ps = con.prepareStatement("DELETE FROM "
                    + PROJECTS + " WHERE ["
                    + PROJECT_CODE + "]=?;");
            ps.setString(1, id);
            int updateCount = ps.executeUpdate();
            if(updateCount > 0){
                System.out.println("Deleted Project " + id);
                return true;
            } else {
                return false;
            }
        }catch(SQLException e){
            System.err.println("Failed  to delete project with ID = " + id);
            System.err.println(e);
            return false;
        }
    }
    
    public static final boolean updateProject(Project project){
        try(Connection con = connect()){
            PreparedStatement ps = con.prepareStatement("UPDATE "
                    + PROJECTS + " SET ["
                    + PROJECT_NAME + "]=?, [" // (1)
                    + PROJECT_LOCATION_ID + "]=?, [" // (2)
                    + PROJECT_DATE_STARTED + "]=?, [" // (3)
                    + PROJECT_DATE_TO_FINISH + "]=?, [" // (4)
                    + PROJECT_STATUS + "]=? WHERE [" // (5)
                    + PROJECT_CODE + "]=?;"); // (6)
            
            ps.setString(1, project.getNameValue()); // (1)
            ps.setString(2, project.getLocationIdValue()); // (2)
            
            LocalDate ls = project.getDateCreated();
            Calendar started = Calendar.getInstance();
            started.set(ls.getYear(), ls.getMonthValue(), ls.getDayOfMonth());
            ps.setDate(3, new Date(started.getTime().getTime())); // (3)
            
            LocalDate lf = project.getDateToFinish();
            Calendar completion = Calendar.getInstance();
            completion.set(lf.getYear(), lf.getMonthValue(), lf.getDayOfMonth());
            ps.setDate(4, new Date(completion.getTime().getTime())); // (4)
            
            ps.setString(5, project.getStatusValue()); // (5)
            ps.setString(6, project.getIdValue()); // (6)
            
            int updateCount = ps.executeUpdate();
            
            String str = String.format("Updated %d rows of table %s.", updateCount, PROJECTS);
            System.out.println(str);
            return updateCount > 0;
        }catch(SQLException e){
            System.err.println("Failed to update project with ID = " + project.getId());
            System.err.println(e);
            return false;
        }
    }

    public static final ArrayList<Project> getProjects() {
        ArrayList<Project> projects = new ArrayList<>();
        try (
                Connection conn = connect();
                Statement statement = conn.createStatement();) {
            ResultSet result = statement.executeQuery("SELECT * FROM " + PROJECTS);
            while (result.next()) {
                Project project = new Project();
                project.setId(result.getString(PROJECT_CODE));
                project.setName(result.getString(PROJECT_NAME));
                project.setLocationId(result.getString(PROJECT_LOCATION_ID));
                
                Date started = result.getDate(PROJECT_DATE_STARTED);
                Calendar cs = Calendar.getInstance();
                cs.setTime(started);
                
                LocalDate dateStarted = LocalDate.of(cs.get(YEAR), cs.get(MONTH) + 1 % 12, cs.get(DAY_OF_MONTH));
                project.setDateCreated(dateStarted);

                Date completion = result.getDate(PROJECT_DATE_TO_FINISH);
                Calendar cf = Calendar.getInstance();
                cf.setTime(completion);
                LocalDate dateToFinish = LocalDate.of(cf.get(YEAR), cf.get(MONTH) + 1 % 12, cf.get(DAY_OF_MONTH));
                project.setDateToFinish(dateToFinish);
                
                project.setStatus(result.getString(PROJECT_STATUS));
                projects.add(project);
            }
        } catch (SQLException e) {
            System.err.println(e);
        }
        return projects;
    }

    public static final ArrayList<History> getHistories() {
        ArrayList<History> histories = new ArrayList<>();
        try (
                Connection conn = connect();
                Statement statement = conn.createStatement();) {
            ResultSet result = statement.executeQuery("SELECT * FROM "
                    + HISTORIES);
            while (result.next()) {
                History history = new History();
                history.setId(result.getInt(HISTORY_ID));
                history.setProjectId(result.getString(HISTORY_PROJECT_CODE));
                
                Date date = result.getDate(HISTORY_DATE_CREATED);
                Calendar cal = Calendar.getInstance();
                cal.setTime(date);
                LocalDate created = LocalDate.of(cal.get(YEAR), cal.get(MONTH), cal.get(DAY_OF_MONTH));
                history.setDate(created);
                history.setNotes(result.getString(HISTORY_NOTES));
                histories.add(history);
            }
        } catch (SQLException e) {
            System.err.println(e);
        }
        return histories;
    }
    
    public static final boolean addLocation(Location location) {
        try (Connection conn = connect()) {
            PreparedStatement ps = conn.prepareStatement("INSERT INTO "
                    + LOCATIONS + " (["
                    + LOCATION_ID + "], ["
                    + LOCATION_STREET + "], ["
                    + LOCATION_CITY + "], ["
                    + LOCATION_PROVINCE + "])"
                    + " VALUES (?, ?, ?, ?);");

            ps.setString(1, location.getId());
            ps.setString(2, location.getStreet());
            ps.setString(3, location.getCity());
            ps.setString(4, location.getProvince());

            int updatedRows = ps.executeUpdate();
            if (updatedRows > 0) {
                System.out.println("Added new Location to database successfully.");
                System.out.println(updatedRows + " row(s) affected.");
            }
            return updatedRows > 0;
        } catch (SQLException e) {
            System.err.println("Failed to add new location to database.\n" + e);
            return false;
        }
    }

    public static final boolean updateLocation(Location location){
        try(Connection con = connect()){
            PreparedStatement ps = con.prepareStatement("UPDATE "
                    + LOCATIONS + " SET ["
                    + LOCATION_PROVINCE + "]=?, ["
                    + LOCATION_CITY + "]=?, ["
                    + LOCATION_STREET + "]=? WHERE ["
                    + LOCATION_ID + "=?;");
            ps.setString(1, location.getProvince());
            ps.setString(2, location.getCity());
            ps.setString(3, location.getStreet());
            ps.setString(4, location.getId());
            
            int updatedRows = ps.executeUpdate();
            System.out.println("Update " + updatedRows + " rows of table " + LOCATIONS);
            return updatedRows > 0;
        }catch(SQLException e){
            System.err.println("Failed to update location with ID = " + location.getId());
            System.err.println(e);
            return false;
        }
    }
    
    public static final boolean deleteLocation(Location location){
        try( Connection con = connect()){
            PreparedStatement ps = con.prepareStatement("DELETE * FROM "
                    + LOCATIONS + " WHERE ["
                    + LOCATION_ID + "]=" + location.getId() + ";");
            int affectedRow = ps.executeUpdate();
            if(affectedRow > 0){
                System.out.println("Successfully deleted location with ID = " + location.getId());
            }
            return affectedRow > 0;
        }catch(SQLException e){
            System.err.println("Failed to delete location with ID = " + location.getId());
            System.err.println(e);
            return false;
        }
    }

    public static ArrayList<Location> getLocations() {
        ArrayList<Location> locations = new ArrayList<>();
        try (
                Connection conn = connect();
                Statement statement = conn.createStatement();) {
            ResultSet result = statement.executeQuery("SELECT * FROM Locations");
            while (result.next()) {
                Location location = new Location();
                location.setId(result.getString(LOCATION_ID));
                location.setStreet(result.getString(LOCATION_STREET));
                location.setCity(result.getString(LOCATION_CITY));
                location.setProvince(result.getString(LOCATION_PROVINCE));
                locations.add(location);
            }
        } catch (SQLException e) {
            System.err.println(e);
        }
        return locations;
    }

    public static ArrayList<Coordinate> getCoordinates() {
        ArrayList<Coordinate> coordinates = new ArrayList<>();
        try (
                Connection conn = connect();
                Statement statement = conn.createStatement();) {
            ResultSet result = statement.executeQuery("SELECT * FROM Coordinates");
            while (result.next()) {
                Coordinate coordinate = new Coordinate();
                coordinate.setId(result.getInt(COORDINATE_ID));
                coordinate.setProjectId(result.getString(COORDINATE_PROJECT_CODE));
                coordinate.setLatitude(result.getLong(COORDINATE_LATITUDE));
                coordinate.setLongitude(result.getLong(COORDINATE_LONGITUDE));
                coordinates.add(coordinate);
            }
        } catch (SQLException e) {
            System.err.println(e);
        }
        return coordinates;
    }
    
    public static ArrayList<Contact> getContacts(){
        ArrayList<Contact> contacts = new ArrayList<>();
        try(
                Connection con = connect();
                Statement statement = con.createStatement();
        ){
            ResultSet rs = statement.executeQuery("SELECT * FROM " + CONTACTS);
            while(rs.next()){
                Contact contact = new Contact();
                contact.setContactId(rs.getString(CONTACT_ID));
                contact.setName(rs.getString(CONTACT_NAME));
                contact.setCompany(rs.getString(CONTACT_COMPANY));
                contact.setAddress(rs.getString(CONTACT_ADDRESS));
                contacts.add(contact);
            }
        }catch(SQLException e){
            System.err.println(e);
        }
        return contacts;
    }
    
    public static final Contact getContact(String contactId){
        try(Connection con = connect(); Statement statement = con.createStatement()){
            ResultSet rs = statement.executeQuery("SELECT * FROM "
                    + CONTACTS + " WHERE ["
                    + CONTACT_ID + "]=" + contactId);
            while(rs.next()){
                Contact contact = new Contact();
                contact.setContactId(rs.getString(CONTACT_ID));
                contact.setName(rs.getString(CONTACT_NAME));
                contact.setCompany(rs.getString(CONTACT_COMPANY));
                contact.setAddress(rs.getString(CONTACT_ADDRESS));
                return contact;
            }
        }catch(SQLException e){
            System.err.println(e);
        }
        return null;
    }
    
    public static final ArrayList<ProjectContact> getProjectContacts(){
        ArrayList<ProjectContact> contacts = new ArrayList<>();
        try(Connection con = connect(); Statement statement = con.createStatement()){
            ResultSet rs = statement.executeQuery("SELECT * FROM " + PROJECT_CONTACTS);
            while(rs.next()){
                ProjectContact pc = new ProjectContact();
                pc.setId(rs.getInt(PC_ID));
                pc.setProjectId(rs.getString(PC_PROJECT_ID));
                pc.setContactId(rs.getString(PC_CONTACT_ID));
                contacts.add(pc);
            }
        }catch(SQLException e){
            System.err.println(e);
        }
        return contacts;
    }
    
    public static ArrayList<Project> getProjectByLocation(String locationCode) {
        ArrayList<Project> projects = new ArrayList<>();
        try (
                Connection conn = connect();
                Statement statement = conn.createStatement();) {
            ResultSet result = statement.executeQuery("SELECT * FROM Projects "
                    + "WHERE [Location ID] = " + locationCode);
            while (result.next()) {
                Project project = new Project();
                project.setId(result.getString(PROJECT_CODE));
                project.setName(result.getString(PROJECT_NAME));
                project.setLocationId(result.getString(PROJECT_LOCATION_ID));
                
                Date started = result.getDate(PROJECT_DATE_STARTED);
                Calendar cs = Calendar.getInstance();
                cs.setTime(started);
                LocalDate dateStarted = LocalDate.of(cs.get(YEAR), cs.get(MONTH), cs.get(DAY_OF_MONTH));
                project.setDateCreated(dateStarted);

                Date completion = result.getDate(PROJECT_DATE_TO_FINISH);
                Calendar cf = Calendar.getInstance();
                cf.setTime(completion);
                LocalDate dateToFinish = LocalDate.of(cf.get(YEAR), cf.get(MONTH), cf.get(DAY_OF_MONTH));
                project.setDateToFinish(dateToFinish);
                
                project.setStatus(result.getString(PROJECT_STATUS));
                projects.add(project);
            }
        } catch (SQLException e) {
            System.err.println(e);
        }
        return projects;
    }

    public static ArrayList<Coordinate> getCoordinatesByProject(String projectCode) {
        ArrayList<Coordinate> coordinates = new ArrayList<>();
        try (
                Connection conn = connect();
                Statement statement = conn.createStatement();) {
            ResultSet result
                    = statement.executeQuery("SELECT * FROM Coordinates "
                            + "WHERE [Project ID] = " + projectCode);
            while (result.next()) {
                Coordinate coordinate = new Coordinate();
                coordinate.setId(result.getInt(COORDINATE_ID));
                coordinate.setProjectId(result.getString(COORDINATE_PROJECT_CODE));
                coordinate.setLatitude(result.getLong(COORDINATE_LATITUDE));
                coordinate.setLongitude(result.getLong(COORDINATE_LONGITUDE));
                coordinates.add(coordinate);
            }
        } catch (SQLException e) {
            System.err.println(e);
        }
        return coordinates;
    }
    
    public static final int getLocationCount(String street, String city, String province){
        try(Connection con = connect()){
            PreparedStatement ps = con.prepareStatement("SELECT COUNT(*) FROM "
                    + LOCATIONS + " WHERE "
                    + "Street = " + street + " "
                    + "AND City = " + city + " "
                    + "AND Province = " + province
                    + ";");
            ResultSet rs = ps.executeQuery();
            return rs.getInt(1);
        }catch(SQLException e){
            System.err.println("Failed to execute query: " + e);
        }
        return -1;
    }
    
    public static final void clearTable(String tableName){
        try(Connection con = connect()){
            Statement statement = con.createStatement();
            int count = statement.executeUpdate("DELETE * FROM " + tableName + ";");
            if(count > 0){
                System.out.println("Deleted " + count + " from table " + tableName);
            }
        }catch(SQLException e){
            System.err.println("Failed to delete entries from table " + tableName);
        }
    }

    private static Connection connect() throws SQLException {
        Connection conn = DriverManager.getConnection(DB_DRIVER);
        return conn;
    }
    
}
