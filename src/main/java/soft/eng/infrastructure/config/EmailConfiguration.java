package soft.eng.infrastructure.config;

import java.util.Objects;

public final class EmailConfiguration {

    private final boolean enabled;
    private final String host;
    private final int port;
    private final boolean authenticationEnabled;
    private final boolean startTlsEnabled;
    private final String username;
    private final String password;
    private final String fromAddress;
    private final String subjectPrefix;

    public EmailConfiguration(
            boolean enabled,
            String host,
            int port,
            boolean authenticationEnabled,
            boolean startTlsEnabled,
            String username,
            String password,
            String fromAddress,
            String subjectPrefix) {

        this.enabled = enabled;
        this.host = Objects.requireNonNull(host, "host must not be null");
        this.port = port;
        this.authenticationEnabled = authenticationEnabled;
        this.startTlsEnabled = startTlsEnabled;
        this.username = Objects.requireNonNull(username, "username must not be null");
        this.password = Objects.requireNonNull(password, "password must not be null");
        this.fromAddress = Objects.requireNonNull(fromAddress, "fromAddress must not be null");
        this.subjectPrefix = Objects.requireNonNull(subjectPrefix, "subjectPrefix must not be null");
    }

    public boolean isEnabled() {
        return enabled;
    }

    public String getHost() {
        return host;
    }

    public int getPort() {
        return port;
    }

    public boolean isAuthenticationEnabled() {
        return authenticationEnabled;
    }

    public boolean isStartTlsEnabled() {
        return startTlsEnabled;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public String getFromAddress() {
        return fromAddress;
    }

    public String getSubjectPrefix() {
        return subjectPrefix;
    }
}