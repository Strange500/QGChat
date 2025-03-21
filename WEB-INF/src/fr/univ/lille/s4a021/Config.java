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
    public static boolean DEBUG;
    public static Integer VIDEO_MAX_SIZE;
    public static Integer AUDIO_MAX_SIZE;
    public static Integer IMAGE_MAX_SIZE;

    private final DatabaseConfig databaseConfig;
    private final Map<String, Object> config;

    private  AdminsDAO adminsDAO;
    private  ChannelDAO channelDAO;
    private  FriendDAO friendDAO;
    private  MessageDAO messageDAO;
    private  ReactionDAO reactionDAO;
    private  SubscriptionDAO subscriptionDAO;
    private  UserDAO userDAO;


    public static Config getConfig() throws ConfigErrorException {
        if (instance == null) {
            instance = new Config();
            try {
                Connection connection = Connect.getConnection(instance);
                instance.userDAO = new UserDAOSql(connection);
                instance.channelDAO = new ChannelDAOSql(connection);
                instance.messageDAO = new MessageDAOSql(connection);
                instance.reactionDAO = new ReactionDaoSql(connection,instance.messageDAO, instance.userDAO);
                instance.subscriptionDAO = new SubscriptionDAOSql(connection,instance.channelDAO, instance.userDAO);
                instance.friendDAO = new FriendDAOSql(connection);
                instance.adminsDAO = new AdminsDAOSql(connection, instance.userDAO, instance.channelDAO);
            } catch (Exception e) {
                e.printStackTrace();
                throw new ConfigErrorException("Error while connecting to the database");
            }
        }
        return instance;
    }

    private Config() throws ConfigErrorException {
        this.config = loadConfig();
        this.databaseConfig = loadDatabaseConfig();
        DEBUG = loadDebugMode();
        Map<String, Integer> uploadConfig = loadUploadConfig();
        VIDEO_MAX_SIZE = uploadConfig.get("video");
        AUDIO_MAX_SIZE = uploadConfig.get("audio");
        IMAGE_MAX_SIZE = uploadConfig.get("image");
    }

    private DatabaseConfig loadDatabaseConfig() {
        Map<String, String> databaseConfig = (Map<String, String>) config.get("database");
        return new DatabaseConfig(
                Objects.requireNonNull(databaseConfig.get("host"), "Host not found"),
                Objects.requireNonNull(databaseConfig.get("port"), "Port not found"),
                Objects.requireNonNull(databaseConfig.get("database"), "Database not found"),
                Objects.requireNonNull(databaseConfig.get("user"), "User not found"),
                Objects.requireNonNull(databaseConfig.get("password"), "Password not found")
        );
    }

    private boolean loadDebugMode() throws ConfigErrorException {
        try {
            return (boolean) config.get("debug");
        } catch (Exception e) {
            throw new ConfigErrorException("Error while reading the debug mode");
        }
    }

    private Map<String, Integer> loadUploadConfig() {
        Map<String, String> uploadConfig = (Map<String, String>) config.get("upload");
        return Map.of(
                "video", convertMoToOctet(uploadConfig.get("video_max_size")),
                "audio", convertMoToOctet(uploadConfig.get("audio_max_size")),
                "image", convertMoToOctet(uploadConfig.get("image_max_size"))
        );
    }

    private Integer convertMoToOctet(String moString) {
        int mo = Integer.parseInt(moString.replace("mo", ""));
        return mo * 1024 * 1024;
    }

    private Map<String, Object> loadConfig() throws ConfigErrorException {
        try (InputStream input = getClass().getClassLoader().getResourceAsStream("config.yml")) {
            if (input == null) {
                throw new ConfigErrorException("Config file not found");
            }
            Yaml yaml = new Yaml();
            return yaml.load(input);
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