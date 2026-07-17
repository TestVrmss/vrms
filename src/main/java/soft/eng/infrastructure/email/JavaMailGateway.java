package soft.eng.infrastructure.email;

import java.util.Objects;
import java.util.Properties;

import jakarta.mail.Authenticator;
import jakarta.mail.Message;
import jakarta.mail.MessagingException;
import jakarta.mail.PasswordAuthentication;
import jakarta.mail.Session;
import jakarta.mail.Transport;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;

import soft.eng.infrastructure.config.EmailSettings;

public class JavaMailGateway implements MailGateway {

    private final EmailSettings settings;

    public JavaMailGateway(EmailSettings settings) {
        this.settings = Objects.requireNonNull(
                settings,
                "settings must not be null");
    }

    @Override
    public boolean isEnabled() {
        return settings.isEnabled();
    }

    @Override
    public void send(String to, String subject, String body) {

        if (!isEnabled()) {
            return;
        }

        Objects.requireNonNull(
                to,
                "to must not be null");

        Objects.requireNonNull(
                subject,
                "subject must not be null");

        Objects.requireNonNull(
                body,
                "body must not be null");

        Properties properties = new Properties();

        properties.put(
                "mail.smtp.host",
                settings.getHost());

        properties.put(
                "mail.smtp.port",
                Integer.toString(settings.getPort()));

        properties.put(
                "mail.smtp.auth",
                Boolean.toString(
                        settings.isAuthenticationEnabled()));

        properties.put(
                "mail.smtp.starttls.enable",
                Boolean.toString(
                        settings.isStartTlsEnabled()));

        Session session;

        if (settings.isAuthenticationEnabled()) {

            session = Session.getInstance(
                    properties,
                    new Authenticator() {

                        @Override
                        protected PasswordAuthentication
                                getPasswordAuthentication() {

                            return new PasswordAuthentication(
                                    settings.getUsername(),
                                    settings.getPassword());
                        }
                    });

        } else {

            session = Session.getInstance(properties);
        }

        try {

            MimeMessage message =
                    new MimeMessage(session);

            message.setFrom(
                    new InternetAddress(
                            settings.getFromAddress()));

            message.setRecipients(
                    Message.RecipientType.TO,
                    InternetAddress.parse(to));

            message.setSubject(
                    subject,
                    "UTF-8");

            message.setText(
                    body,
                    "UTF-8");

            sendMessage(message);

        } catch (MessagingException exception) {

            throw new IllegalStateException(
                    "Unable to send email",
                    exception);
        }
    }

    /**
     * Hook for testing.
     */
    protected void sendMessage(MimeMessage message)
            throws MessagingException {

        Transport.send(message);
    }
}