package httpsserver.db;
import java.sql.*;

import httpsserver.db.services.SchemaService;

public class DatabaseManager {

    private static Connection conn;

    public static Connection getConn() {
        return conn;
    }

    public static void connect(boolean dropSchema) throws RuntimeException {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            String url = "jdbc:MySQL://" + ConfigManager.host + ":" + ConfigManager.port + "/";
            conn = DriverManager.getConnection(url, ConfigManager.user, ConfigManager.pass);
            applySchema(dropSchema);

            PreparedStatement st = conn.prepareStatement("USE weather");
            st.execute();
        } catch(ClassNotFoundException |SQLException e) {
            throw new RuntimeException(e.toString() + " -> Couldn't connect to db 'weather'. ");
        }
    }

    private static void applySchema(boolean shouldDropSchema) throws RuntimeException {
        boolean exists = false;
        String query;

        query = "SHOW DATABASES LIKE " + " '" + ConfigManager.name + "'";
        try (PreparedStatement st = conn.prepareStatement(query)) {
            ResultSet result = st.executeQuery();
            exists = result.next();
            result.close();
        } catch (SQLException e) {
            throw new RuntimeException(e.toString() + " -> " + "Couldn't Run DB Query to find DB 'weather'.");
        }

        if (shouldDropSchema && exists) {
            query = "DROP DATABASE " + ConfigManager.name;
            try (PreparedStatement st = conn.prepareStatement("DROP DATABASE weather")) {
                st.execute();
            } catch (SQLException e) {
                throw new RuntimeException(e.toString() + " -> " + "Couldn't drop DB 'weather'");
            }
        }

        if (shouldDropSchema || !exists) {
            try {
                SchemaService.initDB("weather");
            } catch (Throwable e) {
                throw new RuntimeException(e.toString() + " -> " + "Couldn't load schema for Database: 'weather.'");
            }
        }
    }
}