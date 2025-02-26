package fr.univ.lille.s4a021.model.bdd;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Connect {
    // Class to getConnection for DAO constructor
    private static final String DB_URL = "jdbc:postgresql://postgres:5432/sae";
    private static final String DB_USER = "sae";
    private static final String DB_PASSWORD = "sae";

    public static Connection getConnection() throws SQLException {
        Connection con = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
        Connect.createTableIfNotExist(con);
        return con;
    }


    public static void createTableIfNotExist(Connection con) {
        try {
            String query = "CREATE TABLE IF NOT EXISTS Utilisateur (" +
                    "uid SERIAL PRIMARY KEY," +
                    "username VARCHAR(1024) NOT NULL," +
                    "mail VARCHAR(1024) NOT NULL UNIQUE," +
                    "password VARCHAR(1024) NOT NULL" +
                    ")";
            con.createStatement().executeUpdate(query);

            query = "CREATE TABLE IF NOT EXISTS Channel (" +
                    "cid SERIAL PRIMARY KEY," +
                    "name VARCHAR(1024) NOT NULL" +
                    ")";
            con.createStatement().executeUpdate(query);

            query = "CREATE TABLE IF NOT EXISTS Message (" +
                    "mid SERIAL PRIMARY KEY ," +
                    "contenu VARCHAR(1024) NOT NULL" +
                    ")";
            con.createStatement().executeUpdate(query);

            query = "CREATE TABLE IF NOT EXISTS estAbonne (" +
                    "uid INT," +
                    "cid INT," +
                    "PRIMARY KEY (uid, cid)," +
                    "FOREIGN KEY (uid) REFERENCES Utilisateur(uid) ON DELETE CASCADE," +
                    "FOREIGN KEY (cid) REFERENCES Channel(cid) ON DELETE CASCADE" +
                    ")";
            con.createStatement().executeUpdate(query);

            query = "CREATE TABLE IF NOT EXISTS aEnvoyer (" +
                    "uid INT," +
                    "mid INT," +
                    "PRIMARY KEY (uid, mid)," +
                    "FOREIGN KEY (uid) REFERENCES Utilisateur(uid) ON DELETE CASCADE," +
                    "FOREIGN KEY (mid) REFERENCES Message(mid) ON DELETE CASCADE" +
                    ")";
            con.createStatement().executeUpdate(query);

            query = "CREATE TABLE IF NOT EXISTS contient (" +
                    "cid INT," +
                    "mid INT UNIQUE," +
                    "PRIMARY KEY (cid, mid)," +
                    "FOREIGN KEY (cid) REFERENCES Channel(cid) ON DELETE CASCADE," +
                    "FOREIGN KEY (mid) REFERENCES Message(mid) ON DELETE CASCADE" +
                    ")";
            con.createStatement().executeUpdate(query);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

}
