package com.gemseeker.pmma.data;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;

import static com.gemseeker.pmma.data.DBUtil.Columns.*;
import java.sql.DriverManager;
import java.sql.SQLException;
/**
 *
 * @author RAFIS-FRED
 */
public class DBManager {
    
    public static final String DB_NAME = "resources/project_management_db.accdb";
    public static final String DB_DRIVER = "jdbc:ucanaccess://" + DB_NAME;
    
    public static final ArrayList<Project> getProjects(){
        ArrayList<Project> projects = new ArrayList<>();
        try(
                Connection conn = connect();
                Statement statement = conn.createStatement(); 
            )
        {
            ResultSet result = statement.executeQuery("SELECT * FROM Projects");
            while(result.next()){
                Project project = new Project();
                project.setId(result.getString(PROJECT_CODE));
                project.setName(result.getString(PROJECT_NAME));
                project.setLocationId(result.getString(PROJECT_LOCATION_ID));
                project.setDateCreated(result.getDate(PROJECT_DATE_STARTED));
                project.setDateToFinish(result.getDate(PROJECT_DATE_TO_FINISH));
                project.setStatus(result.getString(PROJECT_STATUS));
                projects.add(project);
            }
        }catch(SQLException e){
            System.err.println(e);
        }
        return projects;
    }
    
    public static final ArrayList<History> getHistories(){
        ArrayList<History> histories = new ArrayList<>();
        try(
                Connection conn = connect();
                Statement statement = conn.createStatement();
            )
        {
            ResultSet result = statement.executeQuery("SELECT * FROM Histories");
            while(result.next()){
                History history = new History();
                history.setId(result.getInt(HISTORY_ID));
                history.setProjectId(result.getString(HISTORY_PROJECT_CODE));
                history.setDate(result.getDate(HISTORY_DATE_CREATED));
                history.setNotes(result.getString(HISTORY_NOTES));
                histories.add(history);
            }
        }catch(SQLException e){
            System.err.println(e);
        }
        return histories;
    }
    
    public static ArrayList<Location> getLocations(){
        ArrayList<Location> locations = new ArrayList<>();
        try(
                Connection conn = connect();
                Statement statement = conn.createStatement();
            )
        {
            ResultSet result = statement.executeQuery("SELECT * FROM Locations");
            while(result.next()){
                Location location = new Location();
                location.setId(result.getString(LOCATION_ID));
                location.setStreet(result.getString(LOCATION_STREET));
                location.setCity(result.getString(LOCATION_CITY));
                location.setProvince(result.getString(LOCATION_PROVINCE));
                locations.add(location);
            }
        }catch(SQLException e){
            System.err.println(e);
        }
        return locations;
    }
    
    public static ArrayList<Coordinate> getCoordinates(){
        ArrayList<Coordinate> coordinates = new ArrayList<>();
        try(
                Connection conn = connect();
                Statement statement = conn.createStatement();
            )
        {
            ResultSet result = statement.executeQuery("SELECT * FROM Coordinates");
            while(result.next()){
                Coordinate coordinate = new Coordinate();
                coordinate.setId(result.getInt(COORDINATE_ID));
                coordinate.setProjectId(result.getString(COORDINATE_PROJECT_CODE));
                coordinate.setLatitude(result.getLong(COORDINATE_LATITUDE));
                coordinate.setLongitude(result.getLong(COORDINATE_LONGITUDE));
                coordinates.add(coordinate);
            }
        }catch(SQLException e){
            System.err.println(e);
        }
        return coordinates;
    }
    
    public static ArrayList<Project> getProjectByLocation(String locationCode){
        ArrayList<Project> projects = new ArrayList<>();
        try(
                Connection conn = connect();
                Statement statement = conn.createStatement(); 
            )
        {
            ResultSet result = statement.executeQuery("SELECT * FROM Projects "
                    + "WHERE [Location ID] = " + locationCode);
            while(result.next()){
                Project project = new Project();
                project.setId(result.getString(PROJECT_CODE));
                project.setName(result.getString(PROJECT_NAME));
                project.setLocationId(result.getString(PROJECT_LOCATION_ID));
                project.setDateCreated(result.getDate(PROJECT_DATE_STARTED));
                project.setDateToFinish(result.getDate(PROJECT_DATE_TO_FINISH));
                project.setStatus(result.getString(PROJECT_STATUS));
                projects.add(project);
            }
        }catch(SQLException e){
            System.err.println(e);
        }
        return projects;
    }
    
    public static ArrayList<Coordinate> getCoordinatesByProject(String projectCode){
        ArrayList<Coordinate> coordinates = new ArrayList<>();
        try(
                Connection conn = connect();
                Statement statement = conn.createStatement();
            )
        {
            ResultSet result 
                    = statement.executeQuery("SELECT * FROM Coordinates "
                            + "WHERE [Project ID] = " + projectCode);
            while(result.next()){
                Coordinate coordinate = new Coordinate();
                coordinate.setId(result.getInt(COORDINATE_ID));
                coordinate.setProjectId(result.getString(COORDINATE_PROJECT_CODE));
                coordinate.setLatitude(result.getLong(COORDINATE_LATITUDE));
                coordinate.setLongitude(result.getLong(COORDINATE_LONGITUDE));
                coordinates.add(coordinate);
            }
        }catch(SQLException e){
            System.err.println(e);
        }
        return coordinates;
    }
    
    private static Connection connect() throws SQLException{
        Connection conn = DriverManager.getConnection(DB_DRIVER);
        return conn;
    }
}
