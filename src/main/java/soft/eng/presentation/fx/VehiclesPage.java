package soft.eng.presentation.fx;

import javafx.beans.property.SimpleStringProperty;
import javafx.geometry.*;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import soft.eng.domain.model.Vehicle;

public final class VehiclesPage {

    private final ToastService toast;

    public VehiclesPage(ToastService toast) { this.toast = toast; }

    public Node build() {
        var all = AppContext.getInstance().catalog().getAllVehicles();

        // ─ Search
        var searchFld = UI.field("\uD83D\uDD0D  Search by brand, model or ID\u2026");
        searchFld.setMaxWidth(360);

        // ─ Table
        TableView<Vehicle> table = buildTable();
        table.getItems().addAll(all);
        table.setPrefHeight(460);

        // Live search filter
        searchFld.textProperty().addListener((obs, ov, nv) -> {
            table.getItems().clear();
            var q = nv.toLowerCase();
            all.stream().filter(v ->
                v.getId().toLowerCase().contains(q) ||
                v.getBrand().toLowerCase().contains(q) ||
                v.getModel().toLowerCase().contains(q) ||
                v.getType().name().toLowerCase().contains(q))
               .forEach(table.getItems()::add);
        });

        // ─ Stats badges
        long avail = all.stream().filter(Vehicle::isAvailable).count();
        var badges = new HBox(10,
            UI.badge(all.size() + " Total",    "rgba(148,163,184,0.10)", AppTheme.TEXT_DIM),
            UI.badge(avail + " Available",     "rgba(16,185,129,0.12)",  AppTheme.GREEN),
            UI.badge((all.size()-avail) + " Rented", "rgba(129,140,248,0.12)", AppTheme.ACCENT));
        badges.setAlignment(Pos.CENTER_LEFT);

        var headerRow = new HBox(16, new VBox(4, UI.h1("Vehicles"), UI.muted("Complete fleet registry")),
                                     UI.hSpacer(), badges, searchFld);
        headerRow.setAlignment(Pos.CENTER_LEFT);

        var tableCard = new VBox(14,
            new HBox(12, UI.h3("Fleet Overview"),
                UI.badge(all.size() + " vehicles", "rgba(129,140,248,0.12)", AppTheme.ACCENT)),
            new Separator(), table);
        tableCard.setStyle(AppTheme.card());
        tableCard.setPadding(new Insets(20, 22, 20, 22));

        var page = UI.pageRoot();
        page.getChildren().addAll(headerRow, tableCard);
        return UI.pageScroll(page);
    }

    private TableView<Vehicle> buildTable() {
        TableView<Vehicle> t = new TableView<>();
        t.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        t.setPlaceholder(new Label("No vehicles found."));

        var cId    = strCol("ID",         v -> v.getId(),           90);
        var cBrand = strCol("BRAND",      v -> v.getBrand(),        100);
        var cModel = strCol("MODEL",      v -> v.getModel(),        120);
        var cType  = strCol("TYPE",       v -> v.getType().name(),  110);
        var cRate  = strCol("DAILY RATE", v -> "$" + v.getDailyRate().toPlainString(), 100);
        var cStatus= strCol("STATUS",     v -> v.getStatus().name(), 110);

        // Colorize status column
        cStatus.setCellFactory(col -> new TableCell<>() {
            @Override protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty); setText(empty ? null : item);
                if (!empty && item != null) {
                    String color = switch (item) {
                        case "AVAILABLE"   -> AppTheme.GREEN;
                        case "RENTED"      -> AppTheme.ACCENT;
                        case "MAINTENANCE" -> AppTheme.RED;
                        default            -> AppTheme.TEXT_DIM;
                    };
                    setStyle("-fx-text-fill:" + color + ";-fx-font-weight:bold;-fx-font-size:12px;");
                }
            }
        });

        t.getColumns().addAll(cId, cBrand, cModel, cType, cRate, cStatus);
        return t;
    }

    private TableColumn<Vehicle, String> strCol(String title,
            java.util.function.Function<Vehicle, String> fn, int minW) {
        var c = new TableColumn<Vehicle, String>(title);
        c.setCellValueFactory(cd -> new SimpleStringProperty(fn.apply(cd.getValue())));
        c.setMinWidth(minW);
        return c;
    }
}
