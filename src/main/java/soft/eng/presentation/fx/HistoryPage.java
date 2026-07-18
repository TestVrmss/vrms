package soft.eng.presentation.fx;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import javafx.beans.property.SimpleStringProperty;
import javafx.geometry.*;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import soft.eng.domain.model.Rental;

public final class HistoryPage {

    private final ToastService toast;
    private static final DateTimeFormatter FMT = DateTimeFormatter.ofPattern("dd MMM yyyy");

    public HistoryPage(ToastService toast) { this.toast = toast; }

    public Node build() {
        var rentals = AppContext.getInstance().rental().getActiveRentals();

        // ─ Search
        var searchFld = UI.field("\uD83D\uDD0D  Search by customer, vehicle or ID\u2026");
        searchFld.setMaxWidth(380);

        // ─ Table
        TableView<Rental> table = buildTable();
        table.getItems().addAll(rentals);
        table.setPrefHeight(500);

        // Live search
        searchFld.textProperty().addListener((obs, ov, nv) -> {
            table.getItems().clear();
            var q = nv.toLowerCase();
            rentals.stream().filter(r ->
                r.getId().toLowerCase().contains(q) ||
                r.getCustomer().getFullName().toLowerCase().contains(q) ||
                r.getVehicle().getBrand().toLowerCase().contains(q) ||
                r.getVehicle().getModel().toLowerCase().contains(q))
               .forEach(table.getItems()::add);
        });

        // ─ Stats
        var totalLbl  = UI.badge(rentals.size() + " Total",  "rgba(148,163,184,0.10)", AppTheme.TEXT_DIM);
        var activeLbl = UI.badge(
            rentals.stream().filter(Rental::isActive).count() + " Active",
            "rgba(16,185,129,0.12)", AppTheme.GREEN);

        var headerRow = new HBox(16,
            new VBox(4, UI.h1("Rental History"), UI.muted("All active rental contracts")),
            UI.hSpacer(), totalLbl, activeLbl, searchFld);
        headerRow.setAlignment(Pos.CENTER_LEFT);

        var tableCard = new VBox(14,
            UI.h3("Active Contracts"), new Separator(), table);
        tableCard.setStyle(AppTheme.card());
        tableCard.setPadding(new Insets(20, 22, 20, 22));

        var page = UI.pageRoot();
        page.getChildren().addAll(headerRow, tableCard);
        return UI.pageScroll(page);
    }

    private TableView<Rental> buildTable() {
        var t = new TableView<Rental>();
        t.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        t.setPlaceholder(new Label("No rental records found."));

        var cId    = sCol("RENTAL ID",    80,  r -> r.getId());
        var cCust  = sCol("CUSTOMER",    120,  r -> r.getCustomer().getFullName());
        var cEmail = sCol("EMAIL",       150,  r -> r.getCustomer().getEmail());
        var cVeh   = sCol("VEHICLE",     140,  r -> r.getVehicle().getBrand() + " " + r.getVehicle().getModel());
        var cStart = sCol("START DATE",  110,  r -> r.getStartDate().format(FMT));
        var cEnd   = sCol("END DATE",    110,  r -> r.getEndDate().format(FMT));
        var cDays  = sCol("DURATION",     80,  r -> r.getRentalDays() + "d");
        var cLeft  = sCol("DAYS LEFT",    90,  r -> {
            long d = java.time.temporal.ChronoUnit.DAYS.between(LocalDate.now(), r.getEndDate());
            return d < 0 ? Math.abs(d) + "d overdue" : d + "d left";
        });
        var cStatus= sCol("STATUS",       90,  r -> r.getStatus().name());

        // Colorize days left
        cLeft.setCellFactory(col -> new TableCell<>() {
            @Override protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty); setText(empty ? null : item);
                if (!empty && item != null) {
                    boolean overdue = item.contains("overdue");
                    boolean warning = !overdue && item.startsWith("0d") || item.startsWith("1d") || item.startsWith("2d");
                    String c = overdue ? AppTheme.RED : warning ? AppTheme.AMBER : AppTheme.GREEN;
                    setStyle("-fx-text-fill:" + c + ";-fx-font-weight:bold;");
                }
            }
        });

        cStatus.setCellFactory(col -> new TableCell<>() {
            @Override protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty); setText(empty ? null : item);
                if (!empty && item != null) {
                    String c = "ACTIVE".equals(item) ? AppTheme.GREEN : AppTheme.TEXT_DIM;
                    setStyle("-fx-text-fill:" + c + ";-fx-font-weight:bold;");
                }
            }
        });

        t.getColumns().addAll(cId, cCust, cEmail, cVeh, cStart, cEnd, cDays, cLeft, cStatus);
        return t;
    }

    private TableColumn<Rental, String> sCol(String title, int min,
            java.util.function.Function<Rental, String> fn) {
        var c = new TableColumn<Rental, String>(title);
        c.setCellValueFactory(cd -> new SimpleStringProperty(fn.apply(cd.getValue())));
        c.setMinWidth(min); return c;
    }
}
