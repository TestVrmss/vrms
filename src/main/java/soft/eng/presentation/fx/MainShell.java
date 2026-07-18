package soft.eng.presentation.fx;

import javafx.geometry.*;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.layout.*;

public final class MainShell {

    private final ToastService toast;
    private final StackPane contentArea = new StackPane();
    private Button activeBtn;

    public MainShell(ToastService toast) { this.toast = toast; }

    /** Returns Parent so it can be passed to App.navigateTo() */
    public Parent build() {
        var root = new BorderPane();
        root.setStyle("-fx-background-color:" + AppTheme.BG + ";");
        root.setTop(buildTopBar());
        root.setLeft(buildSidebar());
        root.setCenter(contentArea);
        showPage(new DashboardPage(toast).build());
        return root;
    }

    // ── Top Bar ──────────────────────────────────────────────────
    private HBox buildTopBar() {
        var dot = new Region();
        dot.setPrefSize(8, 8);
        dot.setStyle("-fx-background-color:" + AppTheme.ACCENT + ";-fx-background-radius:4;");
        var brandLbl = new Label("VRMS");
        brandLbl.setStyle("-fx-font-size:15px;-fx-font-weight:bold;-fx-text-fill:" + AppTheme.TEXT + ";");
        var brand = new HBox(8, dot, brandLbl);
        brand.setAlignment(Pos.CENTER_LEFT);

        var subtitle = new Label("Vehicle Rental Management System");
        subtitle.setStyle("-fx-font-size:12px;-fx-text-fill:" + AppTheme.TEXT_FAINT + ";");

        var manager = AppContext.getInstance().auth().getCurrentManager();
        var uname   = manager.map(m -> m.getUsername()).orElse("manager");
        var userChip = new Label("  " + uname);
        userChip.setStyle("-fx-font-size:12px;-fx-text-fill:" + AppTheme.TEXT_DIM +
            ";-fx-background-color:" + AppTheme.SURFACE2 +
            ";-fx-background-radius:20;-fx-padding:6 14;" +
            "-fx-border-color:rgba(129,140,248,0.15);-fx-border-radius:20;-fx-border-width:1;");

        String normalLogout = "-fx-background-color:transparent;-fx-text-fill:" + AppTheme.TEXT_DIM +
            ";-fx-font-size:12px;-fx-background-radius:8;-fx-padding:7 14;-fx-cursor:hand;" +
            "-fx-border-color:rgba(148,163,184,0.15);-fx-border-radius:8;-fx-border-width:1;";
        String hoverLogout = "-fx-background-color:rgba(239,68,68,0.08);-fx-text-fill:" + AppTheme.RED +
            ";-fx-font-size:12px;-fx-background-radius:8;-fx-padding:7 14;-fx-cursor:hand;" +
            "-fx-border-color:" + AppTheme.RED + ";-fx-border-radius:8;-fx-border-width:1;";

        var logoutBtn = new Button("Log out");
        logoutBtn.setStyle(normalLogout);
        logoutBtn.setOnMouseEntered(e -> logoutBtn.setStyle(hoverLogout));
        logoutBtn.setOnMouseExited(e  -> logoutBtn.setStyle(normalLogout));
        logoutBtn.setOnAction(e -> {
            AppContext.getInstance().auth().logout();
            App.navigateTo(new LoginScreen(toast).build());
        });

        var bar = new HBox(16, brand, UI.hSpacer(), subtitle, UI.hSpacer(), userChip, logoutBtn);
        bar.setAlignment(Pos.CENTER_LEFT);
        bar.setPadding(new Insets(0, 20, 0, 20));
        bar.setPrefHeight(56);
        bar.setStyle("-fx-background-color:" + AppTheme.SURFACE +
            ";-fx-border-color:transparent transparent rgba(129,140,248,0.12) transparent;" +
            "-fx-border-width:0 0 1 0;" +
            "-fx-effect:dropshadow(gaussian,rgba(0,0,0,0.4),10,0.2,0,2);");
        return bar;
    }

    // ── Sidebar ──────────────────────────────────────────────────
    private VBox buildSidebar() {
        var navLabel  = sectionLabel("NAVIGATION");
        var btnDash    = navBtn("[D]", "Dashboard");
        var btnVehicles= navBtn("[V]", "Vehicles");
        var btnRent    = navBtn("[R]", "Rent Vehicle");
        var btnReturn  = navBtn("[<]", "Return Vehicle");
        var btnHistory = navBtn("[H]", "Rental History");
        var sysLabel   = sectionLabel("SYSTEM");
        var btnEmail   = navBtn("[@]", "Email Settings");

        btnDash.setOnAction(e     -> { activate(btnDash);     showPage(new DashboardPage(toast).build()); });
        btnVehicles.setOnAction(e -> { activate(btnVehicles); showPage(new VehiclesPage(toast).build()); });
        btnRent.setOnAction(e     -> { activate(btnRent);     showPage(new RentPage(toast).build()); });
        btnReturn.setOnAction(e   -> { activate(btnReturn);   showPage(new ReturnPage(toast).build()); });
        btnHistory.setOnAction(e  -> { activate(btnHistory);  showPage(new HistoryPage(toast).build()); });
        btnEmail.setOnAction(e    -> { activate(btnEmail);    showPage(new EmailPage(toast).build()); });

        activate(btnDash);

        var versionLbl = new Label("v3.0 Nebula");
        versionLbl.setStyle("-fx-font-size:10px;-fx-text-fill:" + AppTheme.TEXT_FAINT + ";-fx-padding:8 12;");

        var sidebar = new VBox(4,
            navLabel, btnDash, btnVehicles, btnRent, btnReturn, btnHistory,
            new Separator(), sysLabel, btnEmail,
            UI.vSpacer(), versionLbl);
        sidebar.setPadding(new Insets(16, 8, 16, 8));
        sidebar.setPrefWidth(220);
        sidebar.setStyle("-fx-background-color:" + AppTheme.BG +
            ";-fx-border-color:transparent rgba(129,140,248,0.10) transparent transparent;" +
            "-fx-border-width:0 1 0 0;");
        return sidebar;
    }

    private Label sectionLabel(String text) {
        var l = new Label(text);
        l.setStyle("-fx-font-size:9px;-fx-font-weight:bold;-fx-text-fill:" + AppTheme.TEXT_FAINT +
                   ";-fx-padding:10 10 4 12;");
        return l;
    }

    private Button navBtn(String prefix, String text) {
        var b = new Button(prefix + "   " + text);
        String normal  = "-fx-background-color:transparent;-fx-text-fill:" + AppTheme.TEXT_DIM +
            ";-fx-font-size:13px;-fx-alignment:CENTER_LEFT;-fx-padding:10 12;" +
            "-fx-background-radius:8;-fx-cursor:hand;";
        String hovered = "-fx-background-color:" + AppTheme.SURFACE2 +
            ";-fx-text-fill:" + AppTheme.TEXT +
            ";-fx-font-size:13px;-fx-alignment:CENTER_LEFT;-fx-padding:10 12;" +
            "-fx-background-radius:8;-fx-cursor:hand;";
        b.setStyle(normal); b.setMaxWidth(Double.MAX_VALUE);
        b.setOnMouseEntered(e -> { if (!b.equals(activeBtn)) b.setStyle(hovered); });
        b.setOnMouseExited(e  -> { if (!b.equals(activeBtn)) b.setStyle(normal);  });
        return b;
    }

    private void activate(Button btn) {
        if (activeBtn != null) {
            activeBtn.setStyle("-fx-background-color:transparent;-fx-text-fill:" + AppTheme.TEXT_DIM +
                ";-fx-font-size:13px;-fx-alignment:CENTER_LEFT;-fx-padding:10 12;" +
                "-fx-background-radius:8;-fx-cursor:hand;");
        }
        activeBtn = btn;
        btn.setStyle("-fx-background-color:rgba(129,140,248,0.10);" +
            "-fx-text-fill:" + AppTheme.ACCENT +
            ";-fx-font-size:13px;-fx-font-weight:bold;-fx-alignment:CENTER_LEFT;-fx-padding:10 12;" +
            "-fx-background-radius:0 8 8 0;-fx-cursor:hand;" +
            "-fx-border-color:transparent transparent transparent " + AppTheme.ACCENT + ";" +
            "-fx-border-width:0 0 0 3;");
    }

    private void showPage(Node page) {
        contentArea.getChildren().setAll(page);
    }
}
