package com.tpsproject.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public final class DBConnectionUtil {

    private static boolean driverLoaded;

    private DBConnectionUtil() {
    }

    public static Connection getConnection() throws SQLException {
        loadDriver();
        return DriverManager.getConnection(
                DatabaseConfig.URL,
                DatabaseConfig.USERNAME,
                DatabaseConfig.PASSWORD
        );
    }

    private static synchronized void loadDriver() throws SQLException {
        if (driverLoaded) {
            return;
        }

        try {
            Class.forName(DatabaseConfig.DRIVER_CLASS);
            driverLoaded = true;
        } catch (ClassNotFoundException ex) {
            throw new SQLException("MySQL JDBC driver was not found in the project libraries.", ex);
        }
    }
}
