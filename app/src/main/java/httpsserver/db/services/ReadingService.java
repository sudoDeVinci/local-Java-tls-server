package httpsserver.db.services;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.sql.Timestamp;

import httpsserver.IO;
import httpsserver.db.DatabaseManager;
import httpsserver.db.entities.ReadingEntity;


/**
 * Represent an entry in the Readings table
 */
public class ReadingService {
    
    /**
     * Get all reading entries in the Readings table.
     * @return
     */
    public static List<ReadingEntity> getAll() {
        String queryString = "SELECT * FROM Readings ;";

        List<ReadingEntity> readings = new ArrayList<>();
        Connection conn = DatabaseManager.getConn();
        try (PreparedStatement preparedStmnt = conn.prepareStatement(queryString)) {
            
            ResultSet result = preparedStmnt.executeQuery();
            
            while (result.next()) {
                ReadingEntity reading = new ReadingEntity(
                    result.getString("MAC"),
                    result.getDouble("temperature"),
                    result.getDouble("relative_humidity"),
                    result.getDouble("pressure"),
                    result.getDouble("dewpoint"),
                    result.getTimestamp("timestamp"),
                    result.getString("filepath"));
                
                readings.add(reading);

            }
            result.close();
        } catch (SQLException e) {
            IO.errOut("Couldn't fetch sensor status list -> ", e);
        }

        return readings;
    }

    /**
     * Check if a Rading has been taken for a given timestamp.
     * @param MAC
     * @param timestamp
     * @return
     */
    public static boolean exists(String MAC, Timestamp timestamp) {
        // First we check if a reading record exists already
        String queryString = "SELECT * FROM Readings WHERE timestamp=(?) AND MAC=(?) LIMIT 1;";

        // If no sensor suite is retrieved, return null device.
        ReadingEntity reading = null;

        Connection conn = DatabaseManager.getConn();
        try(PreparedStatement preparedStmnt = conn.prepareStatement(queryString)) {
            
            preparedStmnt.setTimestamp(1, timestamp);
            preparedStmnt.setString(2, MAC);

            ResultSet result = preparedStmnt.executeQuery();
            
            if (result.next()) {
                reading = new ReadingEntity(
                    MAC,
                    result.getDouble("temperature"),
                    result.getDouble("relative_humidity"),
                    result.getDouble("pressure"),
                    result.getDouble("dewpoint"),
                    timestamp,
                    result.getString("filepath"));
            }
            result.close();
        } catch (SQLException e) {
            IO.errOut("Couldn't check for existence of existing reading -> ", e);
            return false;
        }

        return Objects.nonNull(reading);
    }

    /**
     * Get a Reading record by it's timestamp
     * @param MAC
     * @param timestamp
     * @return
     */
    public static ReadingEntity get(String MAC, Timestamp timestamp) {
        String queryString = "SELECT * FROM Readings WHERE timestamp=(?) AND MAC=(?) LIMIT 1;";
        
       // If no sensor suite is retrieved, return null device.
        ReadingEntity reading = null;

        Connection conn = DatabaseManager.getConn();
        try(PreparedStatement preparedStmnt = conn.prepareStatement(queryString)) {
            
            preparedStmnt.setTimestamp(1, timestamp);
            preparedStmnt.setString(2, MAC);
            
            ResultSet result = preparedStmnt.executeQuery();

            if (result.next()) {
                reading = new ReadingEntity(
                    MAC,
                    result.getDouble("temperature"),
                    result.getDouble("relative_humidity"),
                    result.getDouble("pressure"),
                    result.getDouble("dewpoint"),
                    timestamp,
                    result.getString("filepath"));
            }
            result.close();
        } catch (SQLException e) {
            IO.errOut("Couldn't get sensor reading record -> ", e);
            return reading;
        }

        return reading;
    }


    /**
     * Insert a new Reading record.
     * @param MAC
     * @param temp
     * @param hum
     * @param pres
     * @param dew
     * @param timestamp
     */
    public static void add(String MAC, Double temp, Double hum, Double pres, Double dew, Timestamp timestamp) {
        Connection conn = DatabaseManager.getConn();

        /**
         * Insert a new Reading into the database.
         */
        String insertString = "INSERT INTO Readings VALUES(?,?,?,?,?,?,?);";

        
        try (PreparedStatement preparedStmnt = conn.prepareStatement(insertString)) {

            preparedStmnt.setTimestamp(1, timestamp);
            preparedStmnt.setString(2, MAC);
            preparedStmnt.setDouble(3, temp);
            preparedStmnt.setDouble(4, hum);
            preparedStmnt.setDouble(5, pres);
            preparedStmnt.setDouble(6, dew);
            preparedStmnt.setString(7, "");
            preparedStmnt.executeUpdate();

        }  catch (SQLException e) {
            IO.errOut("Couldn't insert sensor reading record -> ", e);
        }
    }

    /**
     * Insert a new Reading record.
     * @param MAC
     * @param temp
     * @param hum
     * @param pres
     * @param dew
     * @param timestamp
     * @param filepath
     */
    public static void add(String MAC, Double temp, Double hum, Double pres, Double dew, Timestamp timestamp, String filepath) {
        Connection conn = DatabaseManager.getConn();

        /**
         * Insert a new Reading into the database.
         */
        String insertString = "INSERT INTO Readings VALUES(?,?,?,?,?,?,?);";

        
        try (PreparedStatement preparedStmnt = conn.prepareStatement(insertString)) {

            preparedStmnt.setTimestamp(1, timestamp);
            preparedStmnt.setString(2, MAC);
            preparedStmnt.setDouble(3, temp);
            preparedStmnt.setDouble(4, hum);
            preparedStmnt.setDouble(5, pres);
            preparedStmnt.setDouble(6, dew);
            preparedStmnt.setString(7, filepath);
            preparedStmnt.executeUpdate();

        }  catch (SQLException e) {
            IO.errOut("Couldn't insert sensor reading record -> ", e);
        }
    }


    /**
     * Update The filepath to the image associated with a reading record.
     * @param MAC
     * @param timestamp
     * @param filepath
     */
    public static void update(String MAC, Timestamp timestamp, String filepath) {
        Connection conn = DatabaseManager.getConn();

        String updateString = "UPDATE Readings SET filepath=(?) WHERE MAC=(?) AND timestamp=(?);";
        
        try (PreparedStatement preparedStmnt = conn.prepareStatement(updateString)) {
            
            preparedStmnt.setString(1, filepath);
            preparedStmnt.setString(2, MAC);
            preparedStmnt.setTimestamp(3, timestamp);
            preparedStmnt.executeUpdate();

        } catch (SQLException e) {
            IO.errOut("Couldn't update filepath of sensor reading record -> ", e);
        }
    }

   /**
    * Update an existing Readings Entry in DB.
    * @param MAC
    * @param temp
    * @param hum
    * @param pres
    * @param dew
    * @param timestamp
    */
    public static void update(String MAC, Double temp, Double hum, Double pres, Double dew, Timestamp timestamp) {
        Connection conn = DatabaseManager.getConn();

        String updateString = "UPDATE Readings SET temperature=(?), relative_humidity=(?),pressure=(?),dewpoint=(?) WHERE MAC=(?) AND timestamp=(?);";
        
        try (PreparedStatement preparedStmnt = conn.prepareStatement(updateString)) {
            
            preparedStmnt.setDouble(1, temp);
            preparedStmnt.setDouble(2, hum);
            preparedStmnt.setDouble(3, pres);
            preparedStmnt.setDouble(4, dew);
            preparedStmnt.setString(5, MAC);
            preparedStmnt.setTimestamp(6, timestamp);
            preparedStmnt.executeUpdate();

        } catch (SQLException e) {
            IO.errOut("Couldn't update filepath of sensor reading record -> ", e);
        }
    }

}
