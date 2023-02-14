package com.quickstep.data;

import java.io.IOException;
import java.util.List;
import java.util.Properties;

public class DataProperties {

    public MigrationType migrationType;
    public String schema;
    public String url;
    public String user;
    public String password;
    public String sslMode;


    public static DataProperties readFromPropertiesFile(List<String> profiles) {
        final Properties properties = readPropertiesFile(profiles);
        final var result = new DataProperties();
        result.migrationType = MigrationType.valueOf(properties.getProperty("demo.config.db.migrationtype"));
        result.schema = properties.getProperty("demo.config.db.schema");
        result.url = properties.getProperty("demo.config.db.url");
        result.user = properties.getProperty("demo.config.db.user");
        result.password = properties.getProperty("demo.config.db.pass");
        result.sslMode = properties.getProperty("demo.config.db.sslmode");
        return result;
    }

    public static Properties readPropertiesFile(List<String> profiles) {
        try {
            return ApplicationPropertiesLoader.read(profiles);
        } catch (IOException e) {
            throw new RuntimeException("Error reading properties: ", e);
        }
    }

    public enum MigrationType {
        None,
        NukeAndPave,
        Scripts
    }
}
