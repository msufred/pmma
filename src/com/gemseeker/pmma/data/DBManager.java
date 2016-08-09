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
            
            ps.setString(1, project.getId());
            ps.setString(2, project.getName());
            ps.setString(3, project.getLocationId());
            
            LocalDate ls = project.getDateCreated();
            Calendar started = Calendar.getInstance();
            started.set(ls.getYear(), ls.getMonthValue(), ls.getDayOfMonth());
            ps.setDate(4, new Date(started.getTime().getTime()));
            
            LocalDate lc = project.getDateToFinish();
            Calendar completion = Calendar.getInstance();
            completion.set(lc.getYear(), lc.getMonthValue(), lc.getDayOfMonth());
            ps.setDate(5, new Date(completion.getTime().getTime()));
            
            ps.setString(6, project.getStatus());
            
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
            PreparedStatement ps = con.prepareStatement("DELETE FROM Projects WHERE Code=?;");
            ps.setString(1, id);
            int updateCount = ps.executeUpdate();
            if(updateCount > 0){
                System.out.println("Deleted Project " + id);
                return true;
            } else {
                return false;
            }
        }catch(SQLException e){
            System.err.println("Failed  to delete project with id=" + id);
            System.err.println(e);
            return false;
        }
    }
    
    public static final boolean updateProject(Project project){
        try(Connection con = connect()){
            PreparedStatement ps = con.prepareStatement("UPDATE "
                    + PROJECTS + " SET ["
                    + PROJECT_NAME + "]=?, [" // (1)
                    + PROJECT_CONTRACTOR_ID + "]=?, [" // (2)
                    + PROJECT_LOCATION_ID + "]=?, [" // (3)
                    + PROJECT_DATE_STARTED + "]=?, [" // (4)
                    + PROJECT_DATE_TO_FINISH + "]=?, [" // (5)
                    + PROJECT_STATUS + "]=? WHERE [" // (6)
                    + PROJECT_CODE + "]=?;"); // (7)
            
            ps.setString(1, project.getName()); // (1)
            ps.setString(2, project.getContractorId()); // (2)
            ps.setString(3, project.getLocationId()); // (3)
            
            LocalDate ls = project.getDateCreated();
            Calendar started = Calendar.getInstance();
            started.set(ls.getYear(), ls.getMonthValue(), ls.getDayOfMonth());
            ps.setDate(4, new Date(started.getTime().getTime())); // (4)
            
            LocalDate lf = project.getDateToFinish();
            Calendar completion = Calendar.getInstance();
            completion.set(lf.getYear(), lf.getMonthValue(), lf.getDayOfMonth());
            ps.setDate(5, new Date(completion.getTime().getTime())); // (5)
            
            ps.setString(6, project.getStatus()); // (6)
            ps.setString(7, project.getId()); // (7)
            
            int updateCount = ps.executeUpdate();
            
            if(updateCount > 0){
                String str = String.format("Updated %d rows of table %s.", updateCount, PROJECTS);
                System.out.println(str);
                return true;
            }else{
                return false;
            }
        }catch(SQLException e){
            System.err.println("Failed to update project with id=" + project.getId());
            System.err.println(e);
            return false;
        }
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

            boolean success = ps.execute();
            if (success) {
                System.out.println("Added new Location to database successfully.");
            }
            return success;
        } catch (SQLException e) {
            System.err.println("Failed to add new location to database.\n\t" + e);
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
                project.setContractorId(result.getString(PROJECT_CONTRACTOR_ID));
                
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

    public static final ArrayList<History> getHistories() {
        ArrayList<History> histories = new ArrayList<>();
        try (
                Connection conn = connect();
                Statement statement = conn.createStatement();) {
            ResultSet result = statement.executeQuery("SELECT * FROM Histories");
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
    
    public static ArrayList<Contractor> getContractors(){
        ArrayList<Contractor> contractors = new ArrayList<>();
        try(
                Connection con = connect();
                Statement statement = con.createStatement();
        ){
            ResultSet rs = statement.executeQuery("SELECT * FROM " + CONTRACTORS);
            while(rs.next()){
                Contractor contractor = new Contractor();
                contractor.setId(rs.getString(CONTRACTOR_ID));
                contractor.setName(rs.getString(CONTRACTOR_NAME));
                contractor.setContactPerson(rs.getString(CONTRACTOR_CONTACT_PERSON));
                contractor.setAddress(rs.getString(CONTRACTOR_ADDRESS));
            }
        }catch(SQLException e){
            System.err.println(e);
        }
        return contractors;
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
