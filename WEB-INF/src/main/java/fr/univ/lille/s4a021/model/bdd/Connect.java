package fr.univ.lille.s4a021.model.bdd;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Connect {
    // Class to getConnection for DAO constructor
    private static final String DB_URL = "jdbc:postgresql://thebdd.qgroget.com:5432/db";
    private static final String DB_USER = "user1";
    private static final String DB_PASSWORD = "your_password";

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
    }
}
