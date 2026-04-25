package com.tpsproject.util;

public final class DatabaseConfig {

    public static final String DRIVER_CLASS = "com.mysql.cj.jdbc.Driver";
    public static final String URL = "jdbc:mysql://localhost:3306/tpsproject?useSSL=false&serverTimezone=UTC";
    public static final String USERNAME = "root";
    public static final String PASSWORD = "";

    private DatabaseConfig() {
    }
}
