package fr.univ.lille.s4a021.model.bdd;

import fr.univ.lille.s4a021.Config;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Connect {
    private static Connection connection;

    public static Connection getConnection(Config conf) throws SQLException, ClassNotFoundException {
        Class.forName("org.postgresql.Driver");
        String url = "jdbc:postgresql://" + conf.getHost() + ":" + conf.getPort() + "/" + conf.getDatabase();
        System.out.println(url);
        connection = DriverManager.getConnection(url, conf.getUser(), conf.getPassword());
        return connection;
    }


}
