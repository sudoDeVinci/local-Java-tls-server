package httpsserver.db.services;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import httpsserver.db.DatabaseManager;

/**
 * Represent the schema for the entire db.
 * 
 */
public class SchemaService {


    public static void initDB(String name) throws Throwable{
        SchemaService.createDB(name);
        SchemaService.createDevicesTable();
        SchemaService.createReadingsTable();
        SchemaService.createStatusTable();
    }

    
    /**
     * Attempt to create and then use database.
     * @param name
     * @throws Throwable
     */
    private static void createDB(String name) throws Throwable{
        Connection conn = DatabaseManager.getConn();
        String query; 

        query = "CREATE DATABASE IF NOT EXISTS (?)";
        try (PreparedStatement preparedStmnt1 = conn.prepareStatement(query)) {
            preparedStmnt1.setString(1, name);
            preparedStmnt1.execute();
        } catch (SQLException e) {
            //IO.errOut("Couldn't connect to db "+ name +" -> ", e);
            throw new Throwable("Couldn't create db "+ name +" -> "+ e.getMessage());
        } 

        query = "USE (?)";
        try (PreparedStatement preparedStmnt2 = conn.prepareStatement(query)) {
            preparedStmnt2.setString(1, name);
            preparedStmnt2.execute();
        } catch (SQLException e) {
            //IO.errOut("Couldn't connect to db "+ name +" -> ", e);
            throw new Throwable("Couldn't connect to db "+ name +" -> "+ e.getMessage());
        } 
    }

    /**
     * Create the Devices table
     * @throws Throwable
     */
    private static void createDevicesTable() throws Throwable{
        Connection conn = DatabaseManager.getConn();

        StringBuilder query = new StringBuilder();
        query.append("CREATE TABLE IF NOT EXISTS Devices(");
        query.append("MAC VARCHAR(20) PRIMARY KEY,");
        query.append("name VARCHAR(20) NOT NULL,");
        query.append("device_model VARCHAR(10) NOT NULL,");
        query.append("camera_model VARCHAR(10) NOT NULL,");
        query.append("altitude DOUBLE(10,6) NOT NULL,");
        query.append("latitude DOUBLE(10,6) NOT NULL,");
        query.append("longitude DOUBLE(10,6) NOT NULL);");
        String querystring = query.toString();
        
        try (PreparedStatement preparedStatement = conn.prepareStatement(querystring)) {
            preparedStatement.execute();
        } catch (SQLException e) {
            throw new Throwable("Couldn't create Devices table  -> "+ e.getMessage());
        }
    }

    /**
     * Create the Readings table.
     * @throws Throwable
     */
    private static void createReadingsTable() throws Throwable {
        Connection conn = DatabaseManager.getConn();

        StringBuilder query = new StringBuilder();
        query.append("CREATE TABLE IF NOT EXISTS Readings(");
        query.append("timestamp DATETIME PRIMARY KEY,");
        query.append("MAC VARCHAR(20),");
        query.append("temperature DOUBLE(10, 4) NOT NULL,");
        query.append("relative_humidity DOUBLE(10,4) NOT NULL,");
        query.append("pressure DOUBLE(10, 4) NOT NULL,");
        query.append("dewpoint DOUBLE(10, 4) NOT NULL,");
        query.append("filepath VARCHAR(100),");
        query.append("FOREIGN KEY (MAC) REFERENCES Devices(MAC));");
        String queryString = query.toString();

        try (PreparedStatement preparedStatement = conn.prepareStatement(queryString)) {
            preparedStatement.execute();
        } catch (SQLException e) {
            throw new Throwable("Couldn't create Readings table  -> "+ e.getMessage());
        }
    }

    /**
     * Create the Status table.
     * @throws Throwable
     */
    private static void createStatusTable() throws Throwable {
        Connection conn = DatabaseManager.getConn();

        StringBuilder query = new StringBuilder();
        query.append("CREATE TABLE IF NOT EXISTS Status(");
        query.append("MAC VARCHAR(20) PRIMARY KEY,");
        query.append("SHT BOOLEAN NOT NULL,");
        query.append("BMP BOOLEAN NOT NULL,");
        query.append("CAM BOOLEAN NOT NULL,");
        query.append("WIFI BOOLEAN NOT NULL,");
        query.append("timestamp DATETIME NOT NULL,");
        query.append("FOREIGN KEY (MAC) REFERENCES Devices(MAC));");
        String queryString = query.toString();

        try (PreparedStatement preparedStatement = conn.prepareStatement(queryString)) {
            preparedStatement.execute();
        } catch (SQLException e) {
            throw new Throwable("Couldn't create Status table  -> "+ e.getMessage());
        }
    }
                                
}
