//package soft.eng.infrastructure.email;
//
//import static org.junit.jupiter.api.Assertions.*;
//
//import org.junit.jupiter.api.Test;
//
//import soft.eng.infrastructure.config.EmailSettings;
//
//class JavaMailGatewayTest {
//
//
//    @Test
//    void constructor_shouldThrowException_whenSettingsIsNull() {
//
//        assertThrows(
//                NullPointerException.class,
//                () -> new JavaMailGateway(null)
//        );
//    }
//
//
//    @Test
//    void isEnabled_shouldReturnTrue_whenEmailIsEnabled() {
//
//        EmailSettings settings = createEnabledSettings();
//
//        JavaMailGateway gateway = new JavaMailGateway(settings);
//
//        assertTrue(gateway.isEnabled());
//    }
//
//
//    @Test
//    void isEnabled_shouldReturnFalse_whenEmailIsDisabled() {
//
//        EmailSettings settings = createDisabledSettings();
//
//        JavaMailGateway gateway = new JavaMailGateway(settings);
//
//        assertFalse(gateway.isEnabled());
//    }
//
//
//    @Test
//    void send_shouldNotThrowException_whenEmailIsDisabled() {
//
//        EmailSettings settings = createDisabledSettings();
//
//        JavaMailGateway gateway = new JavaMailGateway(settings);
//
//
//        assertDoesNotThrow(() ->
//                gateway.send(
//                        "test@gmail.com",
//                        "Test Subject",
//                        "Test Body"
//                )
//        );
//    }
//
//
//    @Test
//    void send_shouldThrowException_whenToIsNull() {
//
//        JavaMailGateway gateway =
//                new JavaMailGateway(createEnabledSettings());
//
//
//        assertThrows(
//                NullPointerException.class,
//                () -> gateway.send(
//                        null,
//                        "Subject",
//                        "Body"
//                )
//        );
//    }
//
//
//    @Test
//    void send_shouldThrowException_whenSubjectIsNull() {
//
//        JavaMailGateway gateway =
//                new JavaMailGateway(createEnabledSettings());
//
//
//        assertThrows(
//                NullPointerException.class,
//                () -> gateway.send(
//                        "test@gmail.com",
//                        null,
//                        "Body"
//                )
//        );
//    }
//
//
//    @Test
//    void send_shouldThrowException_whenBodyIsNull() {
//
//        JavaMailGateway gateway =
//                new JavaMailGateway(createEnabledSettings());
//
//
//        assertThrows(
//                NullPointerException.class,
//                () -> gateway.send(
//                        "test@gmail.com",
//                        "Subject",
//                        null
//                )
//        );
//    }
//
//
//
//    private EmailSettings createEnabledSettings() {
//
//        return new EmailSettings(
//                true,
//                "smtp.gmail.com",
//                587,
//                true,
//                true,
//                "test@gmail.com",
//                "password",
//                "test@gmail.com", null
//        );
//    }
//
//
//    private EmailSettings createDisabledSettings() {
//
//        return new EmailSettings(
//                false,
//                "smtp.gmail.com",
//                587,
//                false,
//                false,
//                "",
//                "",
//                "test@gmail.com", null
//        );
//    }
//}