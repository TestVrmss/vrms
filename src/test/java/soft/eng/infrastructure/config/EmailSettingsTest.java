package soft.eng.infrastructure.config;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class EmailSettingsTest {

    @Test
    void exposesConfiguredValues() {

        EmailConfiguration configuration = new EmailConfiguration(
                true,
                "smtp.example.com",
                587,
                true,
                true,
                "user@example.com",
                "password",
                "from@example.com",
                "[VRMS]");

        EmailSettings settings = new EmailSettings(configuration);

        assertTrue(settings.isEnabled());
        assertEquals("smtp.example.com", settings.getHost());
        assertEquals(587, settings.getPort());
        assertTrue(settings.isAuthenticationEnabled());
        assertTrue(settings.isStartTlsEnabled());
        assertEquals("user@example.com", settings.getUsername());
        assertEquals("password", settings.getPassword());
        assertEquals("from@example.com", settings.getFromAddress());
        assertEquals("[VRMS]", settings.getSubjectPrefix());
    }

    @Test
    void buildsDisabledSettingsFromConfig() {

        EmailSettings settings = EmailSettings.from(ApplicationConfig.getInstance());

        assertTrue(settings.isEnabled());
        assertEquals(587, settings.getPort());
    }

    @Test
    void rejectsInvalidSettings() {

        assertThrows(
                IllegalArgumentException.class,
                () -> new EmailSettings(new EmailConfiguration(
                        false, "host", 0, true, true,
                        "", "", "", "")));

        assertThrows(
                IllegalArgumentException.class,
                () -> new EmailSettings(new EmailConfiguration(
                        false, "host", 70000, true, true,
                        "", "", "", "")));

        assertThrows(
                NullPointerException.class,
                () -> new EmailSettings(new EmailConfiguration(
                        false, null, 587, true, true,
                        "", "", "", "")));

        assertThrows(
                NullPointerException.class,
                () -> new EmailSettings(new EmailConfiguration(
                        false, "host", 587, true, true,
                        null, "", "", "")));

        assertThrows(
                NullPointerException.class,
                () -> new EmailSettings(new EmailConfiguration(
                        false, "host", 587, true, true,
                        "", null, "", "")));

        assertThrows(
                NullPointerException.class,
                () -> new EmailSettings(new EmailConfiguration(
                        false, "host", 587, true, true,
                        "", "", null, "")));

        assertThrows(
                NullPointerException.class,
                () -> new EmailSettings(new EmailConfiguration(
                        false, "host", 587, true, true,
                        "", "", "", null)));

        assertThrows(
                IllegalStateException.class,
                () -> new EmailSettings(new EmailConfiguration(
                        true, "", 587, true, true,
                        "", "", "", "")));

        assertThrows(
                NullPointerException.class,
                () -> EmailSettings.from(null));
    }
}