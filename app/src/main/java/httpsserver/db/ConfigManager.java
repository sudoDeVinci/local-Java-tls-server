package httpsserver.db;

import java.nio.file.Path;
import java.util.Map;

import httpsserver.parser.*;
import httpsserver.IO;

/**
 * This attempts to load the config file of the database 'weather'.
 */
public class ConfigManager {
    public static String host;
    public static String pass = "";
    public static String user;
    public static long port;
    public static String name;

    /**
     * Load the config from toml and populate the ConfigManager object.
     */
    public static void load() {
        try {
            Path tomlPath = Path.of("../db_cfg.toml");
            IO.serverMessage("Using config file located at: " + tomlPath.toString());

            Map<String, Map<String, Object>> tomlData = Toml.parse(tomlPath);
            Map<String, Object> mysqlConfig = tomlData.get("mysql");
            // Extract and initialize the static fields
            name = (String) mysqlConfig.get("name") == null ? "" : (String) mysqlConfig.get("name");
            host = (String) mysqlConfig.get("host");
            pass = (String) mysqlConfig.get("pass") == null ? "" : (String) mysqlConfig.get("pass");
            user = (String) mysqlConfig.get("user");
            port = (long) mysqlConfig.get("port");
        } catch (Exception e) {
            throw new RuntimeException(e.toString() + " -> Couldn't read schema config.");
        }
    }
}
