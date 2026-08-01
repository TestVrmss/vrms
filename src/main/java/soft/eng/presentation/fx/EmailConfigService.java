package soft.eng.presentation.fx;

import java.io.*;
import java.nio.file.*;
import java.util.Properties;
import soft.eng.infrastructure.config.EmailConfiguration;
import soft.eng.infrastructure.config.EmailSettings;
import soft.eng.infrastructure.email.JavaMailGateway;
import soft.eng.infrastructure.email.MailGateway;

public final class EmailConfigService {

    private static final Path CFG =
        Paths.get(System.getProperty("vrms.email.config", "config/email.properties"));

    private final Properties props;
    private String passwordInMemory = "";

    public EmailConfigService() {
        props = defaultProps();
        if (Files.isRegularFile(CFG)) {
            try (var in = Files.newInputStream(CFG)) { props.load(in); }
            catch (IOException e) { System.err.println("[email cfg] " + e.getMessage()); }
        }
        var envPwd = System.getenv("VRMS_EMAIL_PASSWORD");
        if (envPwd != null && !envPwd.isBlank()) passwordInMemory = envPwd;
        var filePwd = props.getProperty("mail.password", "");
        if (!filePwd.isBlank()) passwordInMemory = filePwd;
    }

    public boolean isEnabled()        { return bool("mail.enabled", false); }
    public String  getHost()          { return str("mail.smtp.host", "smtp.gmail.com"); }
    public int     getPort()          { return intP("mail.smtp.port", 587); }
    public boolean isAuth()           { return bool("mail.smtp.auth", true); }
    public boolean isStartTls()       { return bool("mail.smtp.starttls.enable", true); }
    public String  getUsername()      { return str("mail.username", ""); }
    public String  getFromAddress()   { return str("mail.from", ""); }
    public String  getSubjectPrefix() { return str("mail.subject.prefix", "[VRMS]"); }
    public String  getPassword()      { return passwordInMemory; }

    public void setEnabled(boolean v)      { props.setProperty("mail.enabled", String.valueOf(v)); }
    public void setHost(String v)          { props.setProperty("mail.smtp.host", v.trim()); }
    public void setPort(int v)             { props.setProperty("mail.smtp.port", String.valueOf(v)); }
    public void setAuth(boolean v)         { props.setProperty("mail.smtp.auth", String.valueOf(v)); }
    public void setStartTls(boolean v)     { props.setProperty("mail.smtp.starttls.enable", String.valueOf(v)); }
    public void setUsername(String v)      { props.setProperty("mail.username", v.trim()); }
    public void setFromAddress(String v)   { props.setProperty("mail.from", v.trim()); }
    public void setSubjectPrefix(String v) { props.setProperty("mail.subject.prefix", v.trim()); }
    public void setPassword(String v)      { this.passwordInMemory = v; }

    public void saveToFile() throws IOException {
        Files.createDirectories(CFG.getParent());
        var save = new Properties(); save.putAll(props); save.remove("mail.password");
        try (var out = new FileOutputStream(CFG.toFile())) {
            save.store(out, "VRMS Email Config");
        }
    }

    public MailGateway buildGateway() {
    	EmailConfiguration configuration = new EmailConfiguration(
    	        isEnabled(),
    	        getHost(),
    	        getPort(),
    	        isAuth(),
    	        isStartTls(),
    	        getUsername(),
    	        passwordInMemory,
    	        getFromAddress().isBlank() ? getUsername() : getFromAddress(),
    	        getSubjectPrefix());

    	return new JavaMailGateway(new EmailSettings(configuration));
    }

    private String  str(String k, String d)  { return props.getProperty(k, d).trim(); }
    private boolean bool(String k, boolean d) { return Boolean.parseBoolean(str(k, String.valueOf(d))); }
    private int     intP(String k, int d) {
        try { return Integer.parseInt(str(k, String.valueOf(d))); }
        catch (NumberFormatException e) { return d; }
    }
    private static Properties defaultProps() {
        var p = new Properties();
        p.setProperty("mail.enabled",              "false");
        p.setProperty("mail.smtp.host",            "smtp.gmail.com");
        p.setProperty("mail.smtp.port",            "587");
        p.setProperty("mail.smtp.auth",            "true");
        p.setProperty("mail.smtp.starttls.enable", "true");
        p.setProperty("mail.username",             "");
        p.setProperty("mail.from",                 "");
        p.setProperty("mail.subject.prefix",       "[VRMS]");
        return p;
    }
}
