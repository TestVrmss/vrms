package soft.eng.infrastructure.email;

import static org.junit.jupiter.api.Assertions.*;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.junit.jupiter.api.Test;

import soft.eng.infrastructure.config.EmailSettings;

class JavaMailGatewayTest {

    private EmailSettings disabledSettings() {
        return new EmailSettings(
                false,
                "smtp.gmail.com",
                587,
                true,
                true,
                "user@gmail.com",
                "password",
                "user@gmail.com",
                "[VRMS]");
    }

    private EmailSettings enabledSettings() {
        return new EmailSettings(
                true,
                "smtp.gmail.com",
                587,
                false,
                true,
                "user@gmail.com",
                "password",
                "user@gmail.com",
                "[VRMS]");
    }

    static class FakeGateway extends JavaMailGateway {

        boolean called = false;

        FakeGateway(EmailSettings settings) {
            super(settings);
        }

        @Override
        protected void sendMessage(MimeMessage message)
                throws MessagingException {
            called = true;
        }
    }

    static class ExceptionGateway extends JavaMailGateway {

        ExceptionGateway(EmailSettings settings) {
            super(settings);
        }

        @Override
        protected void sendMessage(MimeMessage message)
                throws MessagingException {
            throw new MessagingException("failed");
        }
    }

    @Test
    void constructorRejectsNull() {
        assertThrows(
                NullPointerException.class,
                () -> new JavaMailGateway(null));
    }

    @Test
    void isEnabledReturnsFalse() {
        assertFalse(
                new JavaMailGateway(disabledSettings())
                        .isEnabled());
    }

    @Test
    void isEnabledReturnsTrue() {
        assertTrue(
                new JavaMailGateway(enabledSettings())
                        .isEnabled());
    }

    @Test
    void sendReturnsImmediatelyWhenDisabled() {

        JavaMailGateway gateway =
                new JavaMailGateway(disabledSettings());

        assertDoesNotThrow(() ->
                gateway.send(
                        "a@test.com",
                        "subject",
                        "body"));
    }

    @Test
    void sendRejectsNullRecipient() {

        JavaMailGateway gateway =
                new JavaMailGateway(enabledSettings());

        assertThrows(
                NullPointerException.class,
                () -> gateway.send(
                        null,
                        "subject",
                        "body"));
    }

    @Test
    void sendRejectsNullSubject() {

        JavaMailGateway gateway =
                new JavaMailGateway(enabledSettings());

        assertThrows(
                NullPointerException.class,
                () -> gateway.send(
                        "a@test.com",
                        null,
                        "body"));
    }

    @Test
    void sendRejectsNullBody() {

        JavaMailGateway gateway =
                new JavaMailGateway(enabledSettings());

        assertThrows(
                NullPointerException.class,
                () -> gateway.send(
                        "a@test.com",
                        "subject",
                        null));
    }

    @Test
    void sendCallsSendMessage() {

        FakeGateway gateway =
                new FakeGateway(enabledSettings());

        gateway.send(
                "a@test.com",
                "subject",
                "body");

        assertTrue(gateway.called);
    }

    @Test
    void sendWrapsMessagingException() {

        ExceptionGateway gateway =
                new ExceptionGateway(enabledSettings());

        IllegalStateException ex =
                assertThrows(
                        IllegalStateException.class,
                        () -> gateway.send(
                                "a@test.com",
                                "subject",
                                "body"));

        assertTrue(
                ex.getCause() instanceof MessagingException);
    }
}