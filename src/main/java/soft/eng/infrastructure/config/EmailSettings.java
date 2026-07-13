package soft.eng.infrastructure.config;

import java.util.Objects;

public final class EmailSettings {

    private final boolean enabled;

    private final String host;

    private final int port;

    private final boolean authenticationEnabled;

    private final boolean startTlsEnabled;

    private final String username;

    private final String password;

    private final String fromAddress;

    private final String subjectPrefix;

   
    public EmailSettings(boolean enabled, String host, int port, boolean authenticationEnabled,
                         boolean startTlsEnabled, String username, String password,
                         String fromAddress, String subjectPrefix) {
        if (port <= 0 || port > 65535) {
            throw new IllegalArgumentException("port must be between 1 and 65535");
        }
        this.enabled = enabled;
        this.host = Objects.requireNonNull(host, "host must not be null").trim();
        this.port = port;
        this.authenticationEnabled = authenticationEnabled;
        this.startTlsEnabled = startTlsEnabled;
        this.username = Objects.requireNonNull(username, "username must not be null").trim();
        this.password = Objects.requireNonNull(password, "password must not be null");
        this.fromAddress = Objects.requireNonNull(fromAddress, "fromAddress must not be null").trim();
        this.subjectPrefix = Objects.requireNonNull(subjectPrefix, "subjectPrefix must not be null").trim();
        if (enabled && (this.host.isBlank() || this.username.isBlank()
                || this.password.isBlank() || this.fromAddress.isBlank())) {
            throw new IllegalStateException("enabled email requires host, username, password and from address");
        }
    }

  
    public static EmailSettings from(ApplicationConfig config) {
        Objects.requireNonNull(config, "config must not be null");
        return new EmailSettings(
                config.getBoolean("mail.enabled", false),
                config.getString("mail.smtp.host", "smtp.gmail.com"),
                config.getInt("mail.smtp.port", 587),
                config.getBoolean("mail.smtp.auth", true),
                config.getBoolean("mail.smtp.starttls.enable", true),
                config.getString("mail.username", ""),
                System.getenv().getOrDefault("VRMS_EMAIL_PASSWORD", ""),
                config.getString("mail.from", ""),
                config.getString("mail.subject.prefix", "[VRMS]"));
    }


    public boolean isEnabled() { return enabled; }

    public String getHost() { return host; }

    public int getPort() { return port; }

    public boolean isAuthenticationEnabled() { return authenticationEnabled; }

    public boolean isStartTlsEnabled() { return startTlsEnabled; }

    public String getUsername() { return username; }

    public String getPassword() { return password; }

    public String getFromAddress() { return fromAddress; }
    public String getSubjectPrefix() { return subjectPrefix; }
}
