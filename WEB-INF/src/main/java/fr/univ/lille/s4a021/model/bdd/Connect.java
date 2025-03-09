package fr.univ.lille.s4a021.model.bdd;

import fr.univ.lille.s4a021.Config;
import fr.univ.lille.s4a021.controller.MainController;
import fr.univ.lille.s4a021.exception.ConfigErrorException;

import java.sql.Connection;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.SQLException;

import static java.lang.System.exit;

public class Connect {
    // Class to getConnection for DAO constructor
    private static final String DB_URL = "jdbc:postgresql://postgres:5432/sae";
    private static final String DB_USER = "sae";
    private static final String DB_PASSWORD = "sae";

    private static Connection connection;

    public static Connection getConnection(Config conf) throws SQLException {
        Class.forName()
        String url = "jdbc:postgresql://" + conf.getHost() + ":" + conf.getPort() + "/" + conf.getDatabase();
        System.out.println(url);
        connection = DriverManager.getConnection(url, conf.getUser(), conf.getPassword());
        createTableIfNotExist(connection);
        return connection;
    }

    public static void createTableIfNotExist(Connection con) {
        try {
            String query = "CREATE TABLE IF NOT EXISTS Utilisateur (" +
                            "uid SERIAL PRIMARY KEY," +
                            "username VARCHAR(1024) NOT NULL," +
                            "mail VARCHAR(1024) NOT NULL UNIQUE," +
                            "password VARCHAR(1024) NOT NULL," +
                            "CONSTRAINT check_mail_not_empty CHECK (mail <> '')," +
                            "CONSTRAINT check_username_not_empty CHECK (username <> '')," +
                            "CONSTRAINT check_password_not_empty CHECK (password <> '')" +
                            ")";
            con.createStatement().executeUpdate(query);

            query = "CREATE TABLE IF NOT EXISTS isAdmin (" +
                    "uid INT," +
                    "cid INT," +
                    "PRIMARY KEY (uid, cid)," +
                    "FOREIGN KEY (uid) REFERENCES Utilisateur(uid) ON DELETE CASCADE," +
                    "FOREIGN KEY (cid) REFERENCES Channel(cid) ON DELETE CASCADE" +
                    ")";
            con.createStatement().executeUpdate(query);

            query = "CREATE TABLE IF NOT EXISTS Message (" +
                    "mid SERIAL PRIMARY KEY ," +
                    "contenu TEXT NOT NULL," +
                    "timestamp TIMESTAMP DEFAULT CURRENT_TIMESTAMP"+
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

            query = "CREATE TABLE IF NOT EXISTS likes (" +
                    "mid INT," +
                    "uid INT," +
                    "PRIMARY KEY (mid, uid)," +
                    "CONSTRAINT fk_likes_message FOREIGN KEY (mid) REFERENCES Message(mid) ON DELETE CASCADE," +
                    "CONSTRAINT fk_likes_utilisateur FOREIGN KEY (uid) REFERENCES Utilisateur(uid) ON DELETE CASCADE" +
                    ")";

            con.createStatement().executeUpdate(query);    
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

}
