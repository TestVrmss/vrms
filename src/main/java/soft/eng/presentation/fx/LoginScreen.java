package soft.eng.presentation.fx;

import javafx.application.Platform;
import javafx.geometry.*;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.text.TextAlignment;

public final class LoginScreen {

    private final ToastService toast;

    public LoginScreen(ToastService toast) { this.toast = toast; }

    public Parent build() {
        var root = new HBox();
        root.setStyle("-fx-background-color:" + AppTheme.BG + ";");
        root.setPrefSize(Double.MAX_VALUE, Double.MAX_VALUE);
        root.getChildren().addAll(buildBrandPanel(), buildFormPanel());
        return root;
    }

    private VBox buildBrandPanel() {
        var logoTag = new Label("  VRMS  ");
        logoTag.setStyle("-fx-font-size:11px;-fx-font-weight:bold;-fx-text-fill:" + AppTheme.ACCENT +
            ";-fx-background-color:rgba(129,140,248,0.12);-fx-background-radius:6;" +
            "-fx-padding:4 12;-fx-border-color:rgba(129,140,248,0.20);-fx-border-radius:6;-fx-border-width:1;");

        var t1 = new Label("Vehicle");
        t1.setStyle("-fx-font-size:44px;-fx-font-weight:bold;-fx-text-fill:" + AppTheme.TEXT + ";");
        var t2 = new Label("Rental");
        t2.setStyle("-fx-font-size:44px;-fx-font-weight:bold;-fx-text-fill:" + AppTheme.ACCENT + ";");
        var t3 = new Label("Management");
        t3.setStyle("-fx-font-size:22px;-fx-font-weight:bold;-fx-text-fill:" + AppTheme.TEXT_DIM + ";");

        var tagline = new Label("Complete fleet oversight with real-time\nrental tracking and smart notifications.");
        tagline.setStyle("-fx-font-size:14px;-fx-text-fill:" + AppTheme.TEXT_DIM + ";-fx-line-spacing:4;");
        tagline.setWrapText(true); tagline.setMaxWidth(360);

        var stats = new HBox(16,
            statMini("11", "Vehicles", AppTheme.CYAN),
            statMini("6",  "Types",    AppTheme.GREEN),
            statMini("24/7","Support", AppTheme.ACCENT));

        var line1 = new Region(); line1.setPrefSize(3, 60);
        line1.setStyle("-fx-background-color:linear-gradient(to bottom," + AppTheme.ACCENT + ",transparent);-fx-background-radius:2;");
        var line2 = new Region(); line2.setPrefSize(3, 40);
        line2.setStyle("-fx-background-color:linear-gradient(to bottom," + AppTheme.CYAN + ",transparent);-fx-background-radius:2;");

        var content = new VBox(20, logoTag, new VBox(0, t1, t2, t3), tagline, new HBox(8, line1, line2), stats);
        content.setAlignment(Pos.CENTER_LEFT);
        content.setMaxWidth(440);

        var panel = new VBox();
        panel.setAlignment(Pos.CENTER);
        panel.setPadding(new Insets(60, 60, 60, 70));
        panel.setStyle("-fx-background-color:linear-gradient(to bottom right,#0A0E1A,#080C14);");
        panel.setMinWidth(520);
        HBox.setHgrow(panel, Priority.ALWAYS);
        panel.getChildren().add(content);
        return panel;
    }

    private VBox statMini(String num, String lbl, String color) {
        var n = new Label(num);
        n.setStyle("-fx-font-size:24px;-fx-font-weight:bold;-fx-text-fill:" + color + ";");
        var l = new Label(lbl);
        l.setStyle("-fx-font-size:11px;-fx-text-fill:" + AppTheme.TEXT_DIM + ";");
        var v = new VBox(2, n, l);
        v.setAlignment(Pos.CENTER);
        v.setPadding(new Insets(12, 18, 12, 18));
        v.setStyle("-fx-background-color:rgba(255,255,255,0.04);-fx-background-radius:10;" +
                   "-fx-border-color:rgba(255,255,255,0.07);-fx-border-radius:10;-fx-border-width:1;");
        return v;
    }

    private VBox buildFormPanel() {
        var greeting = new Label("Welcome back");
        greeting.setStyle("-fx-font-size:30px;-fx-font-weight:bold;-fx-text-fill:" + AppTheme.TEXT + ";");
        var sub = new Label("Sign in to your workspace");
        sub.setStyle("-fx-font-size:14px;-fx-text-fill:" + AppTheme.TEXT_DIM + ";");

        var userLbl = UI.caption("USERNAME");
        var userFld = UI.field("Enter username");
        userFld.setMaxWidth(Double.MAX_VALUE);

        var passLbl = UI.caption("PASSWORD");
        var passFld = UI.passField("Enter password");
        passFld.setMaxWidth(Double.MAX_VALUE);

        var errLabel = new Label("");
        errLabel.setStyle("-fx-text-fill:" + AppTheme.RED + ";-fx-font-size:12px;");
        errLabel.setWrapText(true);

        var loginBtn = new Button("Sign In  ->");
        loginBtn.setStyle("-fx-background-color:" + AppTheme.ACCENT_DIM +
            ";-fx-text-fill:white;-fx-font-weight:bold;-fx-font-size:14px;" +
            "-fx-background-radius:8;-fx-padding:13 28;-fx-cursor:hand;-fx-max-width:infinity;" +
            "-fx-effect:dropshadow(gaussian,rgba(99,102,241,0.40),16,0.3,0,4);");
        loginBtn.setMaxWidth(Double.MAX_VALUE);

        var hint = new Label("Credentials are read from config/login.txt");
        hint.setStyle("-fx-font-size:11px;-fx-text-fill:" + AppTheme.TEXT_FAINT + ";");
        hint.setMaxWidth(Double.MAX_VALUE);
        hint.setTextAlignment(TextAlignment.CENTER);

        loginBtn.setOnAction(e -> {
            var u = userFld.getText().trim();
            var p = passFld.getText();
            if (u.isEmpty() || p.isEmpty()) {
                errLabel.setText("Username and password are required.");
                return;
            }
            errLabel.setText("");
            loginBtn.setDisable(true);
            loginBtn.setText("Signing in...");

            Thread t = new Thread(() -> {
                boolean ok = AppContext.getInstance().auth().login(u, p);
                Platform.runLater(() -> {
                    if (ok) {
                        toast.show("Welcome, " + u + "!", ToastService.Type.SUCCESS);
                        App.navigateTo(new MainShell(toast).build());
                    } else {
                        errLabel.setText("Invalid username or password. Please try again.");
                        loginBtn.setDisable(false);
                        loginBtn.setText("Sign In  ->");
                        toast.show("Login failed. Check credentials.", ToastService.Type.ERROR);
                    }
                });
            }, "vrms-login");
            t.setDaemon(true);
            t.start();
        });

        passFld.setOnAction(e -> loginBtn.fire());
        userFld.setOnAction(e -> passFld.requestFocus());

        var form = new VBox(16,
            new VBox(6, greeting, sub),
            new VBox(8, userLbl, userFld),
            new VBox(8, passLbl, passFld),
            errLabel, loginBtn, hint);
        form.setMaxWidth(380);

        var panel = new VBox();
        panel.setAlignment(Pos.CENTER);
        panel.setPadding(new Insets(60));
        panel.setStyle("-fx-background-color:" + AppTheme.SURFACE + ";");
        panel.setMinWidth(460);
        panel.getChildren().add(form);
        return panel;
    }
}