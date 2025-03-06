package fr.univ.lille.s4a021;

import org.yaml.snakeyaml.Yaml;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.Properties;

public class Config {

    private  String PG_HOST;
    private  String PG_PORT;
    private  String PG_DB;
    private  String PG_USER;
    private  String PG_PASSWORD;

    public Config() {
        Properties prop = new Properties();
        try (InputStream input = getClass().getClassLoader().getResourceAsStream("config.yml")) {
            if (input == null) {

                throw new FileNotFoundException("config.yml not found in the current directory");
            }
            Yaml yaml = new Yaml();
            Map<String, Object> config = yaml.load(input);
            Map<String, String> databaseConfig = (Map<String, String>) config.get("database");

            this.PG_HOST = databaseConfig.get("host");
            this.PG_PORT = databaseConfig.get("port");
            this.PG_DB = databaseConfig.get("database");
            this.PG_USER = databaseConfig.get("user");
            this.PG_PASSWORD = databaseConfig.get("password");
        } catch (IOException e) {
            // print working directory
            System.out.println("Current dir: " + System.getProperty("user.dir"));
            e.printStackTrace();
        }
    }

    public String getHost() {
        return PG_HOST;
    }

    public String getPort() {
        return PG_PORT;
    }

    public String getDatabase() {
        return PG_DB;
    }

    public String getUser() {
        return PG_USER;
    }

    public String getPassword() {
        return PG_PASSWORD;
    }
}
