package httpsserver.db.services;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import httpsserver.IO;
import httpsserver.db.DatabaseManager;
import httpsserver.db.entities.DeviceEntity;

/**
 * Represent entries in the Devices table.
 * Perform various operations related to devices.
 */
public class DeviceService {
    
    /**
     * Get all device entries in the devices table.
     * @return
     */
    public static List<DeviceEntity> getAll() {
        String queryString = "SELECT * FROM Devices ;";
        List<DeviceEntity> devices = new ArrayList<>();
        Connection conn = DatabaseManager.getConn();

        try(PreparedStatement preparedStmnt = conn.prepareStatement(queryString)){

            ResultSet result = preparedStmnt.executeQuery();
            while (result.next()) {
                DeviceEntity device = new DeviceEntity(
                    result.getString("MAC"), 
                    result.getString("name"), 
                    result.getString("device_model"), 
                    result.getString("camera_model"),
                    result.getDouble("altitude"),
                    result.getDouble("latitude"),
                    result.getDouble("longitude"));
                
                devices.add(device);
            }

            result.close();

        } catch (SQLException e) {
            IO.errOut("Couldn't fetch device list -> ", e);
        }

        return devices;
    }


    /**
     * Get a device by its MAC.
     * @param MAC Device MAC address in a String. 
     * @return
     */
    public static DeviceEntity get(String MAC) {
        String queryString = "SELECT * FROM Devices WHERE MAC=(?) LIMIT 1;";
        
        // If no user is retrieved, return null user.
        DeviceEntity device = null;

        Connection conn = DatabaseManager.getConn();
        try (PreparedStatement preparedStmnt = conn.prepareStatement(queryString)) {
            preparedStmnt.setString(1, MAC);
            ResultSet result = preparedStmnt.executeQuery();
            if (result.next()) {
                device = new DeviceEntity(
                    MAC, 
                    result.getString("name"), 
                    result.getString("device_model"), 
                    result.getString("camera_model"),
                    result.getDouble("altitude"),
                    result.getDouble("latitude"),
                    result.getDouble("longitude"));
            }

            result.close();
        } catch (SQLException e) {
            IO.errOut("Couldn't fetch device by MAC -> ", e);
        }

        return device;
    }

    /**
     * Get a device by device name.
     * @param name Device name.
     * @return
     */
    public static DeviceEntity getByName(String name) {
        String queryString = "SELECT * FROM Devices WHERE name=(?) LIMIT 1;";
        
        // If no user is retrieved, return null user.
        DeviceEntity device = null;

        Connection conn = DatabaseManager.getConn();
        try (PreparedStatement preparedStmnt = conn.prepareStatement(queryString)) {
            preparedStmnt.setString(1, name);
            ResultSet result = preparedStmnt.executeQuery();
            if (result.next()) {
                device = new DeviceEntity(
                    result.getString("MAC"),
                    name, 
                    result.getString("device_model"), 
                    result.getString("camera_model"),
                    result.getDouble("altitude"),
                    result.getDouble("latitude"),
                    result.getDouble("longitude"));
            }

            result.close();
        } catch (SQLException e) {
            IO.errOut("Couldn't fetch device by name -> ", e);
        }
        
        return device;
    }

    /**
     * Delete an existing device.
     * @param device
     */
    public static void delete(String MAC) {

        String deleteString = "DELETE FROM Devices WHERE MAC=(?);";

        Connection conn = DatabaseManager.getConn();
        try (PreparedStatement preparedStmnt = conn.prepareStatement(deleteString)) {
            preparedStmnt.setString(1, MAC);
            preparedStmnt.executeUpdate();

        } catch (SQLException e) {
            IO.errOut("Couldn't delete device by MAC -> ", e);
        }
    }


    /**
     * Add a new device to the records.
     * @param MAC
     * @param name
     * @param dev_model
     * @param cam_model                                 
     * @param altitiude
     */
    public static void add(String MAC, String name, String dev_model, String cam_model, double altitiude, double latitude, double longitude) {
        Connection conn = DatabaseManager.getConn();

        /*
         * Insert records into the database.
         */
        String insertString = "INSERT INTO Devices VALUES(?,?,?,?,?,?,?);";

        try (PreparedStatement preparedStmnt = conn.prepareStatement(insertString)) {
            preparedStmnt.setString(1, MAC);
            preparedStmnt.setString(2, name);
            preparedStmnt.setString(3, dev_model);
            preparedStmnt.setString(4, cam_model);
            preparedStmnt.setDouble(5, altitiude);
            preparedStmnt.setDouble(6, latitude);
            preparedStmnt.setDouble(7, longitude);

            preparedStmnt.executeUpdate();

        } catch (SQLException e) {
            IO.errOut("Couldn't insert device record -> ", e);
        } 
    }


    /**
     * Check if a device exists.
     * @param name Device name
     * @return
     */
    public static boolean exists(String MAC) {
        // First we check if the name used exists already
        String queryString = "SELECT * FROM Devices WHERE MAC=(?) LIMIT 1;";

        // If no device is retrieved, return null device.
        DeviceEntity device = null;

        Connection conn = DatabaseManager.getConn();
        try(PreparedStatement preparedStmnt = conn.prepareStatement(queryString)) {
            preparedStmnt.setString(1, MAC);
            ResultSet result = preparedStmnt.executeQuery();
            if (result.next()) {
                device = new DeviceEntity(
                    MAC, 
                    result.getString("name"),
                    result.getString("device_model"), 
                    result.getString("camera_model"),
                    result.getDouble("altitude"),
                    result.getDouble("latitude"),
                    result.getDouble("longitude"));
            }
            result.close();

        } catch (SQLException e) {
            IO.errOut("Couldn't check for existence of device record -> ", e);
            return false;
        }

        return Objects.nonNull(device);
    }

    /**
     * Update the name of a device.
     * @param MAC
     * @param name
     */
    public static void updateName(String MAC, String name) {
        Connection conn = DatabaseManager.getConn();

        String updateString = "UPDATE Devices SET name=(?) WHERE MAC=(?);";

        try (PreparedStatement preparedStmnt = conn.prepareStatement(updateString)) {

            preparedStmnt.setString(1, name);
            preparedStmnt.setString(2, MAC);
            preparedStmnt.executeUpdate();

        } catch (SQLException e) {
            IO.errOut("Couldn't update device name -> ", e);
        }

    }
}
