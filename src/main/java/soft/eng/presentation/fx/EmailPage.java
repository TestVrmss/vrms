package soft.eng.presentation.fx;

import javafx.application.Platform;
import javafx.geometry.*;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.*;

public final class EmailPage {

    private final ToastService toast;

    public EmailPage(ToastService toast) { this.toast = toast; }

    public Node build() {
        var cfg = AppContext.getInstance().emailCfg();

        var chkEnabled  = UI.checkBox("Enable Email Notifications");
        var fldHost     = UI.field("smtp.gmail.com");
        var fldPort     = UI.field("587");
        var fldUser     = UI.field("yourname@gmail.com");
        var fldPass     = UI.passField("App Password (16 characters)");
        var fldFrom     = UI.field("Leave blank to use username");
        var fldPrefix   = UI.field("[VRMS]");
        var chkAuth     = UI.checkBox("SMTP Authentication");
        var chkTls      = UI.checkBox("STARTTLS Encryption");

        chkEnabled.setSelected(cfg.isEnabled());
        fldHost.setText(cfg.getHost());
        fldPort.setText(String.valueOf(cfg.getPort()));
        fldUser.setText(cfg.getUsername());
        fldPass.setText(cfg.getPassword());
        fldFrom.setText(cfg.getFromAddress());
        fldPrefix.setText(cfg.getSubjectPrefix());
        chkAuth.setSelected(cfg.isAuth());
        chkTls.setSelected(cfg.isStartTls());

        for (var f : new TextInputControl[]{fldHost,fldPort,fldUser,fldPass,fldFrom,fldPrefix})
            f.setMaxWidth(Double.MAX_VALUE);

        var statusLbl = new Label("");
        statusLbl.setWrapText(true);
        statusLbl.setMaxWidth(Double.MAX_VALUE);

        var saveBtn = new Button("Save Configuration");
        saveBtn.setStyle(AppTheme.btnPrimary());
        saveBtn.setOnAction(e -> {
            applyCfg(cfg,chkEnabled,fldHost,fldPort,fldUser,fldPass,fldFrom,fldPrefix,chkAuth,chkTls);
            try {
                cfg.saveToFile();
                AppContext.getInstance().reloadEmail();
                toast.show("Settings saved!", ToastService.Type.SUCCESS);
                statusLbl.setStyle("-fx-text-fill:"+AppTheme.GREEN+";-fx-font-size:12px;");
                statusLbl.setText("Saved successfully.");
            } catch (Exception ex) {
                statusLbl.setStyle("-fx-text-fill:"+AppTheme.RED+";-fx-font-size:12px;");
                statusLbl.setText("Error: " + ex.getMessage());
            }
        });

        var smtpCard = UI.card(
            new HBox(12, UI.h3("SMTP Configuration"), UI.hSpacer(), chkEnabled),
            new Separator(),
            new HBox(16, grow(UI.formRow("SMTP HOST", fldHost)), UI.formRow("PORT", fldPort)),
            UI.formRow("EMAIL ADDRESS (USERNAME)", fldUser),
            UI.formRow("PASSWORD / APP PASSWORD", fldPass,
                "Gmail: myaccount.google.com > Security > 2-Step > App Passwords"),
            new HBox(16, grow(UI.formRow("FROM ADDRESS", fldFrom)), grow(UI.formRow("SUBJECT PREFIX", fldPrefix))),
            new HBox(32, chkAuth, chkTls),
            new HBox(12, saveBtn, statusLbl));

        // Test panel
        var fldTo      = UI.field("recipient@example.com");
        var fldSubject = UI.field("VRMS Test Email");
        var fldBody    = UI.textArea("Your message here...", 5);
        fldTo.setMaxWidth(Double.MAX_VALUE);
        fldSubject.setMaxWidth(Double.MAX_VALUE);
        if (!cfg.getUsername().isBlank()) fldTo.setText(cfg.getUsername());
        fldSubject.setText("VRMS - Test Email");
        fldBody.setText("Hello,\n\nThis is a test from VRMS.\nIf you see this, email works!\n\n- VRMS System");

        var sendStatusLbl = new Label("");
        sendStatusLbl.setWrapText(true);
        sendStatusLbl.setMaxWidth(500);

        var sendBtn = new Button("Send Test Email");
        sendBtn.setStyle(AppTheme.btnSecondary());
        sendBtn.setOnAction(e -> {
            var to   = fldTo.getText().trim();
            var user = fldUser.getText().trim();
            var pass = fldPass.getText();
            var host = fldHost.getText().trim();
            if (to.isEmpty())   { toast.show("Enter recipient.", ToastService.Type.WARNING); return; }
            if (user.isEmpty()) { toast.show("Enter Gmail address.", ToastService.Type.WARNING); return; }
            if (pass.isEmpty()) { toast.show("Enter App Password.", ToastService.Type.WARNING); return; }

            sendBtn.setDisable(true); sendBtn.setText("Sending...");
            sendStatusLbl.setStyle("-fx-text-fill:"+AppTheme.ACCENT+";-fx-font-size:12px;");
            sendStatusLbl.setText("Connecting to " + host + "...");

            var tmp = new EmailConfigService();
            tmp.setEnabled(true);
            tmp.setHost(host);
            try { tmp.setPort(Integer.parseInt(fldPort.getText().trim())); } catch (Exception ex) { tmp.setPort(587); }
            tmp.setUsername(user);
            tmp.setPassword(pass);
            tmp.setFromAddress(fldFrom.getText().trim().isEmpty() ? user : fldFrom.getText().trim());
            tmp.setSubjectPrefix(fldPrefix.getText().trim());
            tmp.setAuth(chkAuth.isSelected());
            tmp.setStartTls(chkTls.isSelected());

            Thread t = new Thread(() -> {
                try {
                    tmp.buildGateway().send(to, fldSubject.getText(), fldBody.getText());
                    Platform.runLater(() -> {
                        toast.show("Email sent to " + to + "!", ToastService.Type.SUCCESS);
                        sendStatusLbl.setStyle("-fx-text-fill:"+AppTheme.GREEN+";-fx-font-size:12px;");
                        sendStatusLbl.setText("Delivered to " + to);
                        sendBtn.setDisable(false); sendBtn.setText("Send Test Email");
                    });
                } catch (Exception ex) {
                    Throwable root = ex.getCause() != null ? ex.getCause() : ex;
                    String msg = diagnose(root.getMessage());
                    System.err.println("[VRMS Email Error] " + ex.getMessage());
                    if (ex.getCause() != null) System.err.println("[VRMS Cause] " + ex.getCause().getMessage());
                    Platform.runLater(() -> {
                        toast.show(msg, ToastService.Type.ERROR);
                        sendStatusLbl.setStyle("-fx-text-fill:"+AppTheme.RED+";-fx-font-size:12px;");
                        sendStatusLbl.setText(msg);
                        sendBtn.setDisable(false); sendBtn.setText("Send Test Email");
                    });
                }
            }, "vrms-mail");
            t.setDaemon(true); t.start();
        });

        var testCard = UI.card(
            UI.h3("Send Test Email"), new Separator(),
            UI.formRow("RECIPIENT (TO)", fldTo),
            UI.formRow("SUBJECT", fldSubject),
            UI.formRow("MESSAGE", fldBody),
            new HBox(14, sendBtn, sendStatusLbl));

        var helpLbl = new Label(
            "1. myaccount.google.com > Security > 2-Step Verification > App Passwords\n" +
            "2. Create App Password for 'Mail' and copy the 16-character code\n" +
            "3. Host: smtp.gmail.com | Port: 587 | Enable Auth + STARTTLS");
        helpLbl.setStyle("-fx-text-fill:"+AppTheme.ACCENT+";-fx-font-size:12px;-fx-line-spacing:4;");
        helpLbl.setWrapText(true);
        var helpCard = new VBox(8, UI.caption("GMAIL SETUP GUIDE"), helpLbl);
        helpCard.setPadding(new Insets(14,18,14,18));
        helpCard.setStyle("-fx-background-color:rgba(129,140,248,0.05);-fx-background-radius:10;" +
            "-fx-border-color:rgba(129,140,248,0.15);-fx-border-radius:10;-fx-border-width:1;");

        var page = UI.pageRoot();
        page.getChildren().addAll(
            new VBox(4, UI.h1("Email Settings"), UI.muted("Configure SMTP and send real emails")),
            smtpCard, testCard, helpCard);
        return UI.pageScroll(page);
    }

    private void applyCfg(EmailConfigService cfg, CheckBox en, TextField host, TextField port,
            TextField user, PasswordField pass, TextField from, TextField prefix,
            CheckBox auth, CheckBox tls) {
        cfg.setEnabled(en.isSelected());
        cfg.setHost(host.getText().trim());
        try { cfg.setPort(Integer.parseInt(port.getText().trim())); } catch (Exception e) { cfg.setPort(587); }
        cfg.setUsername(user.getText().trim());
        cfg.setPassword(pass.getText());
        cfg.setFromAddress(from.getText().trim());
        cfg.setSubjectPrefix(prefix.getText().trim());
        cfg.setAuth(auth.isSelected());
        cfg.setStartTls(tls.isSelected());
    }

    private VBox grow(VBox v) { HBox.setHgrow(v, Priority.ALWAYS); return v; }

    private static String diagnose(String m) {
        if (m == null) return "Unknown error. Check Eclipse Console for details.";
        String ml = m.toLowerCase();
        if (ml.contains("535") || ml.contains("username and password") || ml.contains("authentication"))
            return "Authentication failed. Use App Password, not your regular Gmail password.";
        if (ml.contains("534") || ml.contains("less secure"))
            return "Google blocked login. Enable 2-Step Verification then create App Password.";
        if (ml.contains("connection refused") || ml.contains("connectexception"))
            return "Cannot connect. Check host: smtp.gmail.com and port: 587.";
        if (ml.contains("timeout") || ml.contains("timed out"))
            return "Connection timed out. Check internet or firewall.";
        if (ml.contains("ssl") || ml.contains("tls") || ml.contains("handshake"))
            return "TLS error. Enable STARTTLS and use port 587.";
        return m.length() > 160 ? m.substring(0, 160) + "..." : m;
    }
}