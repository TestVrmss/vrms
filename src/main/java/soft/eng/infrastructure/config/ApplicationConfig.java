package soft.eng.infrastructure.config;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Objects;
import java.util.Properties;

public final class ApplicationConfig {

    private static final String CONFIG_PATH_PROPERTY =
            "vrms.email.config";

    private static final String DEFAULT_CONFIG_PATH =
            "config/email.properties";

    private final Properties properties;

 
    private ApplicationConfig() {
        this(loadConfiguredProperties());
    }

  
    private ApplicationConfig(Properties properties) {
        Objects.requireNonNull(
                properties,
                "properties must not be null"
        );

        this.properties = new Properties();
        this.properties.putAll(properties);
    }

    private static final class Holder {

        private static final ApplicationConfig INSTANCE =
                new ApplicationConfig();
    }

 
    public static ApplicationConfig getInstance() {
        return Holder.INSTANCE;
    }

 
    static ApplicationConfig fromProperties(Properties overrides) {

        Objects.requireNonNull(
                overrides,
                "overrides must not be null"
        );

        Properties testProperties = defaultProperties();
        testProperties.putAll(overrides);

        return new ApplicationConfig(testProperties);
    }

    public String getString(String key, String defaultValue) {

        Objects.requireNonNull(
                key,
                "key must not be null"
        );

        Objects.requireNonNull(
                defaultValue,
                "defaultValue must not be null"
        );

        return properties
                .getProperty(key, defaultValue)
                .trim();
    }

    public boolean getBoolean(
            String key,
            boolean defaultValue) {

        String rawValue = getString(
                key,
                Boolean.toString(defaultValue)
        );

        if ("true".equalsIgnoreCase(rawValue)) {
            return true;
        }

        if ("false".equalsIgnoreCase(rawValue)) {
            return false;
        }

        throw new IllegalStateException(
                "Property " + key
                        + " must be either true or false"
        );
    }

    public int getInt(String key, int defaultValue) {

        String rawValue = getString(
                key,
                Integer.toString(defaultValue)
        );

        try {
            return Integer.parseInt(rawValue);
        } catch (NumberFormatException exception) {
            throw new IllegalStateException(
                    "Property " + key + " must be an integer",
                    exception
            );
        }
    }

 
    private static Properties loadConfiguredProperties() {

        Properties loadedProperties = defaultProperties();

        String configuredPath = System.getProperty(
                CONFIG_PATH_PROPERTY,
                DEFAULT_CONFIG_PATH
        );

        Path path = Paths.get(configuredPath);

        if (!Files.isRegularFile(path)) {
            return loadedProperties;
        }

        try (InputStream input = Files.newInputStream(path)) {

            loadedProperties.load(input);
            return loadedProperties;

        } catch (IOException exception) {

            throw new IllegalStateException(
                    "Unable to read email configuration: " + path,
                    exception
            );
        }
    }


    private static Properties defaultProperties() {

        Properties defaults = new Properties();

        defaults.setProperty(
                "mail.enabled",
                "false"
        );

        defaults.setProperty(
                "mail.smtp.host",
                "smtp.gmail.com"
        );

        defaults.setProperty(
                "mail.smtp.port",
                "587"
        );

        defaults.setProperty(
                "mail.smtp.auth",
                "true"
        );

        defaults.setProperty(
                "mail.smtp.starttls.enable",
                "true"
        );

        defaults.setProperty(
                "mail.username",
                ""
        );

        defaults.setProperty(
                "mail.password",
                ""
        );

        defaults.setProperty(
                "mail.from",
                ""
        );

        defaults.setProperty(
                "mail.subject.prefix",
                "[VRMS]"
        );

        return defaults;
    }
}
