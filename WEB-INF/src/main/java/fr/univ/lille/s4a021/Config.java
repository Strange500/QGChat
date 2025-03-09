package fr.univ.lille.s4a021;

import fr.univ.lille.s4a021.dao.*;
import fr.univ.lille.s4a021.dao.impl.*;
import fr.univ.lille.s4a021.exception.ConfigErrorException;
import fr.univ.lille.s4a021.model.bdd.Connect;
import org.yaml.snakeyaml.Yaml;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.util.Map;
import java.util.Objects;

public class Config {

    private static Config instance = null;

    private final DatabaseConfig databaseConfig;

    private final AdminsDAO adminsDAO;
    private final ChannelDAO channelDAO;
    private final FriendDAO friendDAO;
    private final MessageDAO messageDAO;
    private final ReactionDAO reactionDAO;
    private final SubscriptionDAO subscriptionDAO;
    private final UserDAO userDAO;


    public static Config getConfig() throws ConfigErrorException {
        if (instance == null) {
            instance = new Config();
        }
        return instance;
    }

    private Config() throws ConfigErrorException {
        this.databaseConfig = loadDatabaseConfig();
        try {
            Connection connection = Connect.getConnection(this);
            this.userDAO = new UserDAOSql(connection);
            this.channelDAO = new ChannelDAOSql(connection);
            this.messageDAO = new MessageDAOSql(connection);
            this.reactionDAO = new ReactionDaoSql(connection);
            this.subscriptionDAO = new SubscriptionDAOSql(connection);
            this.friendDAO = new FriendDAOSql(connection);
            this.adminsDAO = new AdminsDAOSql(connection);

        } catch (Exception e) {
            e.printStackTrace();
            throw new ConfigErrorException("Error while connecting to the database");
        }
    }

    private DatabaseConfig loadDatabaseConfig() throws ConfigErrorException {
        try (InputStream input = getClass().getClassLoader().getResourceAsStream("config.yml")) {
            if (input == null) {
                throw new ConfigErrorException("Config file not found");
            }
            Yaml yaml = new Yaml();
            Map<String, Object> config = yaml.load(input);
            Map<String, String> databaseConfig = (Map<String, String>) config.get("database");
            return new DatabaseConfig(
                    Objects.requireNonNull(databaseConfig.get("host"), "Host not found"),
                    Objects.requireNonNull(databaseConfig.get("port"), "Port not found"),
                    Objects.requireNonNull(databaseConfig.get("database"), "Database not found"),
                    Objects.requireNonNull(databaseConfig.get("user"), "User not found"),
                    Objects.requireNonNull(databaseConfig.get("password"), "Password not found")
            );
        } catch (IOException e) {
            throw new ConfigErrorException("Error while reading the config file");
        }
    }

    public String getHost() {
        return databaseConfig.getHost();
    }

    public String getPort() {
        return databaseConfig.getPort();
    }

    public String getDatabase() {
        return databaseConfig.getDatabase();
    }

    public String getUser() {
        return databaseConfig.getUser();
    }

    public String getPassword() {
        return databaseConfig.getPassword();
    }

    public UserDAO getUserDAO() {
        return userDAO;
    }

    public ChannelDAO getChannelDAO() {
        return channelDAO;
    }

    public MessageDAO getMessageDAO() {
        return messageDAO;
    }

    public ReactionDAO getReactionDAO() {
        return reactionDAO;
    }

    public SubscriptionDAO getSubscriptionDAO() {
        return subscriptionDAO;
    }

    public FriendDAO getFriendDAO() {
        return friendDAO;
    }

    public AdminsDAO getAdminsDAO() {
        return adminsDAO;
    }

}

class DatabaseConfig {
    private final String host;
    private final String port;
    private final String database;
    private final String user;
    private final String password;

    public DatabaseConfig(String host, String port, String database, String user, String password) {
        this.host = host;
        this.port = port;
        this.database = database;
        this.user = user;
        this.password = password;
    }

    public String getHost() { return host; }
    public String getPort() { return port; }
    public String getDatabase() { return database; }
    public String getUser() { return user; }
    public String getPassword() { return password; }
}