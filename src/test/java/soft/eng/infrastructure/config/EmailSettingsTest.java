package soft.eng.infrastructure.config;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Properties;

import org.junit.jupiter.api.Test;

class EmailSettingsTest {

    @Test
    void exposesConfiguredValues() {

        EmailSettings settings = new EmailSettings(
                true,
                "smtp.example.com",
                587,
                true,
                true,
                "user@example.com",
                "password",
                "from@example.com",
                "[VRMS]"
        );

        assertTrue(settings.isEnabled());
        assertEquals(
                "smtp.example.com",
                settings.getHost()
        );
        assertEquals(
                587,
                settings.getPort()
        );
        assertTrue(
                settings.isAuthenticationEnabled()
        );
        assertTrue(
                settings.isStartTlsEnabled()
        );
        assertEquals(
                "user@example.com",
                settings.getUsername()
        );
        assertEquals(
                "password",
                settings.getPassword()
        );
        assertEquals(
                "from@example.com",
                settings.getFromAddress()
        );
        assertEquals(
                "[VRMS]",
                settings.getSubjectPrefix()
        );
    }

    @Test
    void buildsDisabledSettingsFromConfig() {
<<<<<<< HEAD
        EmailSettings settings = EmailSettings.from(ApplicationConfig.getInstance());
        assertTrue(settings.isEnabled()); 
        assertEquals(587, settings.getPort());
        
        
=======

        /*
         * ننشئ إعدادات خاصة بالاختبار،
         * بدل قراءة config/email.properties الحقيقي.
         */
        Properties properties = new Properties();

        properties.setProperty(
                "mail.enabled",
                "false"
        );

        ApplicationConfig config =
                ApplicationConfig.fromProperties(properties);

        EmailSettings settings =
                EmailSettings.from(config);

        assertFalse(settings.isEnabled());
        assertEquals(
                587,
                settings.getPort()
        );
        assertEquals(
                "smtp.gmail.com",
                settings.getHost()
        );
>>>>>>> origin/main
    }

    @Test
    void buildsEnabledSettingsFromConfig() {

        Properties properties = new Properties();

        properties.setProperty(
                "mail.enabled",
                "true"
        );

        properties.setProperty(
                "mail.smtp.host",
                "smtp.example.com"
        );

        properties.setProperty(
                "mail.smtp.port",
                "587"
        );

        properties.setProperty(
                "mail.username",
                "user@example.com"
        );

        properties.setProperty(
                "mail.password",
                "password"
        );

        properties.setProperty(
                "mail.from",
                "from@example.com"
        );

        ApplicationConfig config =
                ApplicationConfig.fromProperties(properties);

        EmailSettings settings =
                EmailSettings.from(config);

        assertTrue(settings.isEnabled());
        assertEquals(
                "smtp.example.com",
                settings.getHost()
        );
        assertEquals(
                "user@example.com",
                settings.getUsername()
        );
    }

    @Test
    void rejectsInvalidSettings() {

        assertThrows(
                IllegalArgumentException.class,
                () -> new EmailSettings(
                        false,
                        "host",
                        0,
                        true,
                        true,
                        "",
                        "",
                        "",
                        ""
                )
        );

        assertThrows(
                IllegalArgumentException.class,
                () -> new EmailSettings(
                        false,
                        "host",
                        70000,
                        true,
                        true,
                        "",
                        "",
                        "",
                        ""
                )
        );

        assertThrows(
                NullPointerException.class,
                () -> new EmailSettings(
                        false,
                        null,
                        587,
                        true,
                        true,
                        "",
                        "",
                        "",
                        ""
                )
        );

        assertThrows(
                NullPointerException.class,
                () -> new EmailSettings(
                        false,
                        "host",
                        587,
                        true,
                        true,
                        null,
                        "",
                        "",
                        ""
                )
        );

        assertThrows(
                NullPointerException.class,
                () -> new EmailSettings(
                        false,
                        "host",
                        587,
                        true,
                        true,
                        "",
                        null,
                        "",
                        ""
                )
        );

        assertThrows(
                NullPointerException.class,
                () -> new EmailSettings(
                        false,
                        "host",
                        587,
                        true,
                        true,
                        "",
                        "",
                        null,
                        ""
                )
        );

        assertThrows(
                NullPointerException.class,
                () -> new EmailSettings(
                        false,
                        "host",
                        587,
                        true,
                        true,
                        "",
                        "",
                        "",
                        null
                )
        );

        assertThrows(
                IllegalStateException.class,
                () -> new EmailSettings(
                        true,
                        "",
                        587,
                        true,
                        true,
                        "",
                        "",
                        "",
                        ""
                )
        );

        assertThrows(
                NullPointerException.class,
                () -> EmailSettings.from(null)
        );
    }
}
