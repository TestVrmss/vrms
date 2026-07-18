package soft.eng.presentation.fx;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.geometry.*;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import soft.eng.domain.model.Rental;

public final class DashboardPage {

    private final ToastService toast;
    private static final DateTimeFormatter FMT = DateTimeFormatter.ofPattern("dd MMM yyyy");

    public DashboardPage(ToastService toast) { this.toast = toast; }

    public Node build() {
        var ctx          = AppContext.getInstance();
        var activeRentals = ctx.rental().getActiveRentals();
        int available    = ctx.catalog().getAvailableVehicles().size();
        int total        = ctx.catalog().getAllVehicles().size();
        int rented       = (int) ctx.catalog().getAllVehicles().stream()
                                .filter(v -> !v.isAvailable()).count();
        long expiring    = activeRentals.stream()
                                .filter(r -> r.expiresWithin(LocalDate.now(), 3)).count();

        // ─ Header
        var dateStr   = LocalDate.now().format(DateTimeFormatter.ofPattern("EEEE, dd MMMM yyyy"));
        var pageTitle = UI.h1("Dashboard");
        var dateLbl   = UI.muted("Today: " + dateStr);

        // ─ Buttons
        var remBtn = new Button("Send Reminders");
        remBtn.setStyle(AppTheme.btnPrimary());
        remBtn.setOnAction(e -> {
            if (expiring == 0) {
                toast.show("No rentals expiring within 3 days.", ToastService.Type.INFO);
                return;
            }
            remBtn.setDisable(true);
            remBtn.setText("Sending...");
            Thread t = new Thread(() -> {
                int sent = ctx.reminder().checkExpiringRentals(3);
                Platform.runLater(() -> {
                    remBtn.setDisable(false);
                    remBtn.setText("Send Reminders");
                    if (sent > 0) {
                        toast.show("Reminders sent for " + sent + " rental(s)!",
                            ToastService.Type.SUCCESS);
                    } else {
                        toast.show(expiring + " rental(s) expiring soon — " +
                            "check Email Settings to enable notifications.",
                            ToastService.Type.WARNING);
                    }
                });
            }, "vrms-reminders");
            t.setDaemon(true);
            t.start();
        });

        var refreshBtn = new Button("Refresh");
        refreshBtn.setStyle(AppTheme.btnSecondary());
        refreshBtn.setOnAction(e -> App.navigateTo(new MainShell(toast).build()));

        var headerRow = new HBox(16,
            new VBox(4, pageTitle, dateLbl),
            UI.hSpacer(), remBtn, refreshBtn);
        headerRow.setAlignment(Pos.CENTER_LEFT);

        // ─ KPI Cards
        var kpiRow = buildKpiRow(available, rented, total, (int) expiring);

        // ─ Rentals table
        var tableCard = buildRentalsTable(activeRentals);

        var page = UI.pageRoot();
        page.getChildren().addAll(headerRow, kpiRow, tableCard);
        return UI.pageScroll(page);
    }

    private HBox buildKpiRow(int available, int rented, int total, int expiring) {
        var c1 = kpiCard("AVAILABLE",     String.valueOf(available), "Ready to rent",    AppTheme.CYAN);
        var c2 = kpiCard("RENTED",        String.valueOf(rented),    "Active contracts",  AppTheme.ACCENT);
        var c3 = kpiCard("TOTAL FLEET",   String.valueOf(total),     "All vehicles",      AppTheme.GREEN);
        var c4 = kpiCard("EXPIRING SOON", String.valueOf(expiring),  "Within 3 days",
                         expiring > 0 ? AppTheme.RED : AppTheme.TEXT_DIM);
        var row = new HBox(14, c1, c2, c3, c4);
        row.setFillHeight(true);
        return row;
    }

    private VBox kpiCard(String label, String value, String sub, String color) {
        var lbl = UI.caption(label);
        var val = new Label(value);
        val.setStyle("-fx-font-size:42px;-fx-font-weight:bold;-fx-text-fill:" + color + ";");
        var subL = UI.muted(sub);
        var bar  = new Region();
        bar.setPrefHeight(3); bar.setPrefWidth(50);
        bar.setStyle("-fx-background-color:" + color + ";-fx-background-radius:2;");
        var card = new VBox(10, lbl, val, bar, subL);
        card.setStyle(AppTheme.card());
        card.setPadding(new Insets(20, 22, 20, 22));
        HBox.setHgrow(card, Priority.ALWAYS);
        return card;
    }

    private VBox buildRentalsTable(List<Rental> rentals) {
        var title      = UI.h3("Active Rentals");
        var countBadge = UI.badge(rentals.size() + " active",
            rentals.isEmpty() ? "rgba(148,163,184,0.12)" : "rgba(129,140,248,0.15)",
            rentals.isEmpty() ? AppTheme.TEXT_FAINT : AppTheme.ACCENT);
        var hdr = new HBox(12, title, countBadge);
        hdr.setAlignment(Pos.CENTER_LEFT);

        TableView<Rental> table = new TableView<>();
        table.setPlaceholder(new Label("No active rentals - fleet fully available"));
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        table.setPrefHeight(320);

        var cId    = col("RENTAL ID",  80);  cId.setCellValueFactory(   c -> new SimpleStringProperty(c.getValue().getId()));
        var cCust  = col("CUSTOMER",  140);  cCust.setCellValueFactory(  c -> new SimpleStringProperty(c.getValue().getCustomer().getFullName()));
        var cVeh   = col("VEHICLE",   150);  cVeh.setCellValueFactory(   c -> new SimpleStringProperty(c.getValue().getVehicle().getBrand() + " " + c.getValue().getVehicle().getModel()));
        var cStart = col("START",     110);  cStart.setCellValueFactory( c -> new SimpleStringProperty(c.getValue().getStartDate().format(FMT)));
        var cEnd   = col("END",       110);  cEnd.setCellValueFactory(   c -> new SimpleStringProperty(c.getValue().getEndDate().format(FMT)));
        var cDays  = col("DAYS",       60);  cDays.setCellValueFactory(  c -> new SimpleStringProperty(c.getValue().getRentalDays() + "d"));
        var cStat  = col("STATUS",     90);  cStat.setCellValueFactory(  c -> new SimpleStringProperty(c.getValue().getStatus().name()));

        table.getColumns().addAll(cId, cCust, cVeh, cStart, cEnd, cDays, cStat);
        table.getItems().addAll(rentals);

        var card = new VBox(14, hdr, new Separator(), table);
        card.setStyle(AppTheme.card());
        card.setPadding(new Insets(20, 22, 20, 22));
        return card;
    }

    private TableColumn<Rental, String> col(String title, int minW) {
        var c = new TableColumn<Rental, String>(title);
        c.setMinWidth(minW);
        return c;
    }
}