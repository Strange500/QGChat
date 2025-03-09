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

    public static Connection getConnection(Config conf) throws SQLException, ClassNotFoundException {
        Class.forName("org.postgresql.Driver");
        String url = "jdbc:postgresql://" + conf.getHost() + ":" + conf.getPort() + "/" + conf.getDatabase();
        System.out.println(url);
        connection = DriverManager.getConnection(url, conf.getUser(), conf.getPassword());
        return connection;
    }


}
