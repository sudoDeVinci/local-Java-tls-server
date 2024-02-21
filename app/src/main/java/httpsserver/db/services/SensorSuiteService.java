package httpsserver.db.services;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import httpsserver.IO;
import httpsserver.db.DatabaseManager;
import httpsserver.db.entities.SensorSuiteStatusEntity;


/**
 * Represent an entry in the Status table.
 * Perform various operations related to devices.
 */
public class SensorSuiteService {

    /**
     * Get all sensor status entries in the Status table.
     * @return
     */
    public static List<SensorSuiteStatusEntity> getAll() {
        String queryString = "SELECT * FROM Status ;";
        List<SensorSuiteStatusEntity> sensors = new ArrayList<>();
        Connection conn = DatabaseManager.getConn();
        try (PreparedStatement preparedStmnt = conn.prepareStatement(queryString)) {
            
            ResultSet result = preparedStmnt.executeQuery();
            
            while (result.next()) {
                SensorSuiteStatusEntity sensor = new SensorSuiteStatusEntity(
                        result.getString("MAC"),
                        result.getBoolean("SHT"),
                        result.getBoolean("BMP"),
                        result.getBoolean("CAM"),
                        result.getBoolean("WIFI"),
                        result.getTimestamp("timestamp"));

                sensors.add(sensor);
            }

            result.close();
        } catch (SQLException e) {
            IO.errOut("Couldn't fetch sensor status list -> ", e);
        }

        return sensors;
    }

    /**
     * Get the sensor statuses of a given device by its MAC.
     * @param MAC
     * @return
     */
    public static SensorSuiteStatusEntity get(String MAC) {
        String queryString = "SELECT * FROM Status WHERE MAC=(?) LIMIT 1;";
        
        // If no sensor is retrieved, return null sensor.
        SensorSuiteStatusEntity sensor = null;

        Connection conn = DatabaseManager.getConn();
        try (PreparedStatement preparedStmnt = conn.prepareStatement(queryString)) {
            
            preparedStmnt.setString(1, MAC);
            
            ResultSet result = preparedStmnt.executeQuery();
            
            if (result.next()) {
                sensor = new SensorSuiteStatusEntity(
                    MAC,
                    result.getBoolean("SHT"), 
                    result.getBoolean("BMP"), 
                    result.getBoolean("CAM"),
                    result.getBoolean("WIFI"),
                    result.getTimestamp("timestamp"));
            }

            result.close();
        } catch (SQLException e) {
            IO.errOut("Couldn't fetch sensor status by MAC -> ", e);
        }

        return sensor;
    }

    /**
     * Insert a new Status Record.
     * @param MAC
     * @param SHT
     * @param BMP
     * @param CAM
     * @param WIFI
     * @param timestamp
     */
    public static void add(String MAC, Boolean SHT, Boolean BMP, Boolean CAM, Boolean WIFI, Timestamp timestamp) {
        Connection conn = DatabaseManager.getConn();

        /*
         * Insert records into the database.
         */
        String insertString = "INSERT INTO Status VALUES(?,?,?,?,?,?);";

        try (PreparedStatement preparedStmnt = conn.prepareStatement(insertString)) {
            preparedStmnt.setString(1, MAC);
            preparedStmnt.setBoolean(2, SHT);
            preparedStmnt.setBoolean(3, BMP);
            preparedStmnt.setBoolean(4, CAM);
            preparedStmnt.setBoolean(5, WIFI);
            preparedStmnt.setTimestamp(6, timestamp);

            preparedStmnt.executeUpdate();

        } catch (SQLException e) {
            IO.errOut("Couldn't insert sensor status record -> ", e);
        }
    }


    /**
     * Update an existing status record.
     * @param MAC
     * @param SHT
     * @param BMP
     * @param CAM
     * @param WIFI
     * @param timestamp
     */
    public static void update(String MAC, Boolean SHT, Boolean BMP, Boolean CAM, Boolean WIFI, Timestamp timestamp) {

        Connection conn = DatabaseManager.getConn();

        String updateString = "UPDATE Status SET SHT=(?), BMP=(?), CAM=(?), WIFI=(?), timestamp=(?) WHERE MAC=(?);";

        try (PreparedStatement preparedStmnt = conn.prepareStatement(updateString)) {

            preparedStmnt.setBoolean(1, SHT);
            preparedStmnt.setBoolean(2, BMP);
            preparedStmnt.setBoolean(3, CAM);
            preparedStmnt.setBoolean(4, WIFI);
            preparedStmnt.setTimestamp(5, timestamp);
            preparedStmnt.setString(6, MAC);
            preparedStmnt.executeUpdate();

        } catch (SQLException e) {
            IO.errOut("Couldn't update sensor status record -> ", e);
        }
    }

    /**
     * Return true if a record is found.
     * This really shouldn't instantiate an object just to check. Fix this.
     * @param MAC
     * @return
     */
    public static boolean exists(String MAC) {
        // First we check if a sensor suite record exists already
        String queryString = "SELECT * FROM Status WHERE MAC=(?) LIMIT 1;";

        // If no sensor suite is retrieved, return null device.
        SensorSuiteStatusEntity sensor = null;

        Connection conn = DatabaseManager.getConn();
        try(PreparedStatement preparedStmnt = conn.prepareStatement(queryString)) {
            preparedStmnt.setString(1, MAC);
            ResultSet result = preparedStmnt.executeQuery();
            if (result.next()) {
                sensor = new SensorSuiteStatusEntity(
                    MAC,
                    result.getBoolean("SHT"), 
                    result.getBoolean("BMP"), 
                    result.getBoolean("CAM"),
                    result.getBoolean("WIFI"),
                    result.getTimestamp("timestamp"));
            }
            result.close();
        } catch (SQLException e) {
            IO.errOut("Couldn't check for existence of sensor suite record -> ", e);
            return false;
        }

        return Objects.nonNull(sensor);
    }
}
