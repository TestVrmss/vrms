package soft.eng.presentation.fx;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import javafx.geometry.*;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import soft.eng.domain.model.Rental;

public final class ReturnPage {

    private final ToastService toast;
    private static final DateTimeFormatter FMT = DateTimeFormatter.ofPattern("dd MMM yyyy");

    public ReturnPage(ToastService toast) { this.toast = toast; }

    public Node build() {
        // ─ Search section
        var searchFld = UI.field("Enter Rental ID\u2026");
        searchFld.setMaxWidth(Double.MAX_VALUE);
        var searchBtn = new Button("\uD83D\uDD0D  Find Rental");
        searchBtn.setStyle(AppTheme.btnSecondary());

        var searchRow = new HBox(12, searchFld, searchBtn);
        searchRow.setAlignment(Pos.CENTER_LEFT);

        var searchCard = UI.card(
            UI.h3("Find Rental by ID"),
            new Separator(),
            UI.muted("Enter the rental ID to look up and return a vehicle."),
            searchRow);

        // ─ Info panel (shown after search)
        var infoPanel = new VBox(12);
        infoPanel.setVisible(false); infoPanel.setManaged(false);

        // ─ Confirm panel
        var confirmPanel = new VBox(12);
        confirmPanel.setVisible(false); confirmPanel.setManaged(false);

        // ─ Or pick from active rentals
        var activeRentals = AppContext.getInstance().rental().getActiveRentals();
        var activeCard = buildActiveList(activeRentals, searchFld);

        // Search action
        Runnable doSearch = () -> {
            var id = searchFld.getText().trim();
            if (id.isEmpty()) { toast.show("Enter a rental ID.", ToastService.Type.WARNING); return; }
            var match = activeRentals.stream().filter(r -> r.getId().equalsIgnoreCase(id)).findFirst();
            if (match.isEmpty()) {
                toast.show("No active rental found with ID: " + id, ToastService.Type.ERROR);
                infoPanel.setVisible(false); infoPanel.setManaged(false);
                confirmPanel.setVisible(false); confirmPanel.setManaged(false);
                return;
            }
            var r = match.get();
            buildInfoPanel(infoPanel, r);
            buildConfirmPanel(confirmPanel, r, toast);
            infoPanel.setVisible(true); infoPanel.setManaged(true);
            confirmPanel.setVisible(true); confirmPanel.setManaged(true);
        };
        searchBtn.setOnAction(e -> doSearch.run());
        searchFld.setOnAction(e -> doSearch.run());

        var page = UI.pageRoot();
        page.getChildren().addAll(
            new VBox(4, UI.h1("Return Vehicle"), UI.muted("Process a vehicle return and calculate final cost")),
            searchCard, infoPanel, confirmPanel, activeCard);
        return UI.pageScroll(page);
    }

    private void buildInfoPanel(VBox panel, Rental r) {
        panel.getChildren().clear();
        var today   = LocalDate.now();
        var late    = r.getLateDays(today);
        var daysUsed= java.time.temporal.ChronoUnit.DAYS.between(r.getStartDate(), today);
        BigDecimal est;
        try { est = AppContext.getInstance().rental().calculateRentalCost(r.getId(), today); }
        catch (Exception e) { est = BigDecimal.ZERO; }

        var rows = new GridPane();
        rows.setHgap(40); rows.setVgap(12);
        addInfoRow(rows, 0, "Rental ID",     r.getId());
        addInfoRow(rows, 1, "Customer",      r.getCustomer().getFullName());
        addInfoRow(rows, 2, "Email",         r.getCustomer().getEmail());
        addInfoRow(rows, 3, "Vehicle",       r.getVehicle().getBrand() + " " + r.getVehicle().getModel() +
                                              " (" + r.getVehicle().getId() + ")");
        addInfoRow(rows, 4, "Daily Rate",    "$" + r.getVehicle().getDailyRate().toPlainString() + "/day");
        addInfoRow(rows, 5, "Start Date",    r.getStartDate().format(FMT));
        addInfoRow(rows, 6, "Planned End",   r.getEndDate().format(FMT));
        addInfoRow(rows, 7, "Return Today",  today.format(FMT));
        addInfoRow(rows, 8, "Days Used",     daysUsed + " days");

        String lateText = late > 0 ? late + " days OVERDUE" : "On time";
        String lateColor= late > 0 ? AppTheme.RED : AppTheme.GREEN;
        var lateLbl = new Label(lateText);
        lateLbl.setStyle("-fx-text-fill:" + lateColor + ";-fx-font-weight:bold;-fx-font-size:13px;");
        rows.add(UI.caption("OVERDUE STATUS"), 0, 9); rows.add(lateLbl, 1, 9);

        var costBig = new Label("$" + est.toPlainString());
        costBig.setStyle("-fx-font-size:34px;-fx-font-weight:bold;-fx-text-fill:" + AppTheme.ACCENT + ";");
        var costBox = new VBox(6, UI.caption("TOTAL DUE"), costBig,
            UI.muted("Includes " + (late > 0 ? "late fees" : "standard rate")));
        costBox.setPadding(new Insets(14, 18, 14, 18));
        costBox.setStyle("-fx-background-color:rgba(129,140,248,0.06);" +
            "-fx-background-radius:10;-fx-border-color:rgba(129,140,248,0.18);" +
            "-fx-border-radius:10;-fx-border-width:1;");

        panel.getChildren().setAll(
            UI.card(UI.h3("Rental Details"), new Separator(), rows, costBox));
    }

    private void buildConfirmPanel(VBox panel, Rental r, ToastService toast) {
        panel.getChildren().clear();
        var confirmBtn = new Button("\u2714  Confirm Return Now");
        confirmBtn.setStyle(AppTheme.btnPrimary() + "-fx-font-size:14px;-fx-padding:13 32;");
        confirmBtn.setOnAction(e -> {
            try {
                var total = AppContext.getInstance().rental().returnVehicle(r.getId());
                toast.show("Vehicle returned! Total charged: $" + total.toPlainString(),
                    ToastService.Type.SUCCESS);
                panel.setVisible(false); panel.setManaged(false);
                confirmBtn.setDisable(true);
            } catch (Exception ex) {
                toast.show("Return failed: " + ex.getMessage(), ToastService.Type.ERROR);
            }
        });
        var cancelBtn = UI.btnGhost("Cancel");
        cancelBtn.setOnAction(e -> {
            panel.setVisible(false); panel.setManaged(false);
        });
        var row = new HBox(12, cancelBtn, UI.hSpacer(), confirmBtn);
        row.setAlignment(Pos.CENTER_LEFT);
        panel.getChildren().add(row);
    }

    private VBox buildActiveList(java.util.List<Rental> rentals, TextField searchFld) {
        var title = UI.h3("All Active Rentals");
        var hint  = UI.muted("Click a row to auto-fill the ID above.");

        TableView<Rental> table = new TableView<>();
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        table.setPrefHeight(280);
        table.setPlaceholder(new Label("No active rentals."));

        var cId   = sCol("RENTAL ID",  80,  r -> r.getId());
        var cCust = sCol("CUSTOMER",   130, r -> r.getCustomer().getFullName());
        var cVeh  = sCol("VEHICLE",    150, r -> r.getVehicle().getBrand() + " " + r.getVehicle().getModel());
        var cEnd  = sCol("DUE DATE",   110, r -> r.getEndDate().format(FMT));
        var cDays = sCol("DAYS LEFT",  90,  r -> {
            long d = java.time.temporal.ChronoUnit.DAYS.between(LocalDate.now(), r.getEndDate());
            return d < 0 ? (Math.abs(d) + "d overdue") : (d + "d left");
        });
        table.getColumns().addAll(cId, cCust, cVeh, cEnd, cDays);
        table.getItems().addAll(rentals);
        table.setOnMouseClicked(e -> {
            var sel = table.getSelectionModel().getSelectedItem();
            if (sel != null) searchFld.setText(sel.getId());
        });

        var card = new VBox(10, new HBox(12, title, hint), new Separator(), table);
        card.setStyle(AppTheme.card());
        card.setPadding(new Insets(20, 22, 20, 22));
        return card;
    }

    private void addInfoRow(GridPane g, int row, String key, String val) {
        g.add(UI.caption(key), 0, row);
        var v = new Label(val);
        v.setStyle("-fx-text-fill:" + AppTheme.TEXT + ";-fx-font-size:13px;");
        g.add(v, 1, row);
    }

    private TableColumn<Rental, String> sCol(String title, int min,
            java.util.function.Function<Rental, String> fn) {
        var c = new TableColumn<Rental, String>(title);
        c.setCellValueFactory(cd -> new javafx.beans.property.SimpleStringProperty(fn.apply(cd.getValue())));
        c.setMinWidth(min); return c;
    }
}
