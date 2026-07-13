package soft.eng.infrastructure.email;

public interface MailGateway {
    boolean isEnabled();

    
    void send(String to, String subject, String body);
}
