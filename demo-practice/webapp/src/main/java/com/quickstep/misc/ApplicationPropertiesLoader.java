package com.quickstep.misc;

import com.google.common.base.Joiner;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Properties;

public class ApplicationPropertiesLoader {

    private static final String NOT_SET_VALUE = "$NOTSET";

    private static final String PROPERTIES_DIRECTORY_NAME = "properties";

    private static final String DEFAULT_FILENAME = "app.properties";

    private static final String LOCAL_FILENAME = "app-local.properties";

    private static final String PROFILE_FILENAME_FORMAT = "app-%s.properties";

    private static void loadProperties(final File file, final Properties properties) throws IOException {
        if (file == null || !file.exists()) return;

        try {
            properties.load(new FileInputStream(file));
        } catch (final FileNotFoundException ignored) {
        }
    }

    private static void loadProperties(final String file, final Properties properties) throws IOException {
        if (file == null || file.isBlank()) return;

        loadProperties(new File(file), properties);
    }

    private static void validateProperties(final Properties properties) {
        final List<String> missingProperties = properties.entrySet().stream()
                .map(entry -> Map.entry(entry.getKey().toString(), entry.getValue().toString()))
                .filter(entry -> NOT_SET_VALUE.equals(entry.getValue()))
                .map(Map.Entry::getKey)
                .toList();

        if (!missingProperties.isEmpty()) {
            throw new RuntimeException("Required properties are missing or not set:\n- " + Joiner.on("\n- ").join(missingProperties));
        }
    }

    protected ApplicationPropertiesLoader() {
        super();
    }

    public static Properties read(final List<String> profiles) throws IOException {
        // [Step 1] Get the properties' directory and verify that it exists.
        File propertiesDirectory = new File(new File(".").getAbsoluteFile().getParentFile(), PROPERTIES_DIRECTORY_NAME);

        if (!propertiesDirectory.exists()) {
            propertiesDirectory = new File(new File(".").getAbsoluteFile().getParentFile().getParentFile(), PROPERTIES_DIRECTORY_NAME);

            if(!propertiesDirectory.exists()) {
                throw new RuntimeException("Properties directory [" + propertiesDirectory + "] does not exist.");
            }
        }

        // [Step 2] Load the default properties.
        final Properties properties = new Properties();
        loadProperties(new File(propertiesDirectory, DEFAULT_FILENAME), properties);

        // [Step 3] Load the profile properties.
        if (profiles != null) {
            for (final String profile : profiles) {
                // Ignore `null` elements.
                if (profile == null) continue;

                loadProperties(new File(propertiesDirectory, String.format(PROFILE_FILENAME_FORMAT, profile)), properties);
            }
        }

        // [Step 4] Load the local properties.
        loadProperties(new File(propertiesDirectory, LOCAL_FILENAME), properties);

        // [Step 5] Validate properties.
        validateProperties(properties);

        return properties;
    }
}
