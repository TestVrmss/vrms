package soft.eng.infrastructure.config;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Properties;


public final class ApplicationConfig {

    private static final String CONFIG_PATH_PROPERTY = "vrms.email.config";

    private final Properties properties;


    private ApplicationConfig() {
        properties = defaultProperties();
        Path path = Paths.get(System.getProperty(CONFIG_PATH_PROPERTY, "config/email.properties"));
        if (Files.isRegularFile(path)) {
            try (InputStream input = Files.newInputStream(path)) {
                properties.load(input);
            } catch (IOException exception) {
                throw new IllegalStateException("Unable to read email configuration: " + path, exception);
            }
        }
    }


    private static final class Holder {

        private static final ApplicationConfig INSTANCE = new ApplicationConfig();
    }

    
    public static ApplicationConfig getInstance() {
        return Holder.INSTANCE;
    }

    
    public String getString(String key, String defaultValue) {
        return properties.getProperty(key, defaultValue).trim();
    }

    
    public boolean getBoolean(String key, boolean defaultValue) {
        return Boolean.parseBoolean(getString(key, Boolean.toString(defaultValue)));
    }

    
    public int getInt(String key, int defaultValue) {
        String raw = getString(key, Integer.toString(defaultValue));
        try {
            return Integer.parseInt(raw);
        } catch (NumberFormatException exception) {
            throw new IllegalStateException("Property " + key + " must be an integer", exception);
        }
    }

    /* Creates safe built-in defaults. */
    private static Properties defaultProperties() {
        Properties defaults = new Properties();
        defaults.setProperty("mail.enabled", "false");
        defaults.setProperty("mail.smtp.host", "smtp.gmail.com");
        defaults.setProperty("mail.smtp.port", "587");
        defaults.setProperty("mail.smtp.auth", "true");
        defaults.setProperty("mail.smtp.starttls.enable", "true");
        defaults.setProperty("mail.username", "");
        defaults.setProperty("mail.from", "");
        defaults.setProperty("mail.subject.prefix", "[VRMS]");
        return defaults;
    }
}
