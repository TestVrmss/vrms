package soft.eng.presentation.fx;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

import javafx.geometry.*;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import soft.eng.domain.model.Customer;

public final class RentPage {

    private final ToastService toast;
    private static final DateTimeFormatter FMT = DateTimeFormatter.ofPattern("dd MMM yyyy");

    public RentPage(ToastService toast) { this.toast = toast; }

    public Node build() {
        var ctx = AppContext.getInstance();
        var availVehicles = ctx.catalog().getAvailableVehicles();

        // ─ Customer info
        var fldName   = UI.field("Full name");
        var fldEmail  = UI.field("customer@email.com");
        var fldAge    = UI.field("Age (18–120)");
        var chkSpecial = UI.checkBox("Has special/heavy vehicle license");

        fldName.setMaxWidth(Double.MAX_VALUE);
        fldEmail.setMaxWidth(Double.MAX_VALUE);
        fldAge.setMaxWidth(120);

        var customerCard = UI.card(
            UI.h3("Customer Information"),
            new Separator(),
            UI.formRow("FULL NAME", fldName),
            new HBox(16,
                setGrow(UI.formRow("EMAIL ADDRESS", fldEmail)),
                UI.formRow("AGE", fldAge)),
            chkSpecial);

        // ─ Vehicle selection
        var vehicleItems = availVehicles.stream()
            .map(v -> v.getId() + "  —  " + v.getBrand() + " " + v.getModel()
                    + "  ($" + v.getDailyRate().toPlainString() + "/day)")
            .toArray(String[]::new);
        var vehicleCombo = vehicleItems.length == 0
            ? new ComboBox<String>() : UI.combo(vehicleItems);
        if (vehicleItems.length == 0) vehicleCombo.setPromptText("No vehicles available");
        vehicleCombo.setMaxWidth(Double.MAX_VALUE);

        var vehicleCard = UI.card(UI.h3("Vehicle"), new Separator(),
            UI.formRow("SELECT VEHICLE", vehicleCombo));

        // ─ Rental period
        var startPicker = UI.datePicker();
        var endPicker   = UI.datePicker();
        startPicker.setValue(LocalDate.now());
        endPicker.setValue(LocalDate.now().plusDays(3));

        // Cost preview
        var costLbl = new Label("Estimated cost will appear here");
        costLbl.setStyle("-fx-font-size:28px;-fx-font-weight:bold;-fx-text-fill:" + AppTheme.ACCENT + ";");
        var costCard = new VBox(6,
            UI.caption("ESTIMATED COST"), costLbl,
            UI.muted("Based on daily rate \u00D7 number of days"));
        costCard.setPadding(new Insets(16, 20, 16, 20));
        costCard.setStyle("-fx-background-color:rgba(129,140,248,0.06);" +
            "-fx-background-radius:10;-fx-border-color:rgba(129,140,248,0.18);" +
            "-fx-border-radius:10;-fx-border-width:1;");

        // Recalculate cost on selection/date changes
        Runnable updateCost = () -> {
            try {
                int sel = vehicleCombo.getSelectionModel().getSelectedIndex();
                if (sel < 0 || startPicker.getValue() == null || endPicker.getValue() == null) return;
                var v = availVehicles.get(sel);
                long days = java.time.temporal.ChronoUnit.DAYS.between(
                    startPicker.getValue(), endPicker.getValue());
                if (days <= 0) { costLbl.setText("End date must be after start date"); return; }
                var cost = v.getDailyRate().multiply(BigDecimal.valueOf(days));
                costLbl.setText("$" + cost.toPlainString() + "  (" + days + " days)");
            } catch (Exception ignored) {}
        };
        vehicleCombo.setOnAction(e -> updateCost.run());
        startPicker.setOnAction(e -> updateCost.run());
        endPicker.setOnAction(e   -> updateCost.run());

        var periodCard = UI.card(
            UI.h3("Rental Period"), new Separator(),
            new HBox(16,
                setGrow(UI.formRow("START DATE", startPicker)),
                setGrow(UI.formRow("END DATE",   endPicker))),
            costCard);

        // ─ Error label
        var errLbl = new Label("");
        errLbl.setStyle("-fx-text-fill:" + AppTheme.RED + ";-fx-font-size:12px;");
        errLbl.setWrapText(true);

        // ─ Submit button
        var submitBtn = new Button("\uD83D\uDD11  Confirm Rental");
        submitBtn.setStyle(AppTheme.btnPrimary() + "-fx-font-size:14px;-fx-padding:13 32;");
        submitBtn.setOnAction(e -> {
            errLbl.setText("");
            // Validate
            if (fldName.getText().isBlank()  || fldEmail.getText().isBlank() || fldAge.getText().isBlank()) {
                errLbl.setText("Please fill all customer fields."); return;
            }
            int sel = vehicleCombo.getSelectionModel().getSelectedIndex();
            if (sel < 0) { errLbl.setText("Please select a vehicle."); return; }
            if (startPicker.getValue() == null || endPicker.getValue() == null) {
                errLbl.setText("Please set rental dates."); return;
            }
            if (!endPicker.getValue().isAfter(startPicker.getValue())) {
                errLbl.setText("End date must be after start date."); return;
            }
            int age;
            try { age = Integer.parseInt(fldAge.getText().trim()); }
            catch (NumberFormatException ex) { errLbl.setText("Age must be a number."); return; }

            try {
                var customer = new Customer(
                    UUID.randomUUID().toString().substring(0, 8),
                    fldName.getText().trim(),
                    fldEmail.getText().trim(),
                    age,
                    chkSpecial.isSelected());
                var vehicle = availVehicles.get(sel);
                var rental = ctx.rental().rentVehicle(customer, vehicle.getId(),
                    startPicker.getValue(), endPicker.getValue());
                long days = java.time.temporal.ChronoUnit.DAYS.between(
                    startPicker.getValue(), endPicker.getValue());
                toast.show("Rental created! ID: " + rental.getId() +
                    " | " + days + " days | Vehicle: " + vehicle.getBrand() + " " + vehicle.getModel(),
                    ToastService.Type.SUCCESS);
                // Clear form
                fldName.clear(); fldEmail.clear(); fldAge.clear();
                vehicleCombo.getSelectionModel().clearSelection();
                costLbl.setText("Estimated cost will appear here");
            } catch (Exception ex) {
                errLbl.setText("Error: " + ex.getMessage());
                toast.show(ex.getMessage(), ToastService.Type.ERROR);
            }
        });

        var submitRow = new HBox(16, errLbl, UI.hSpacer(), submitBtn);
        submitRow.setAlignment(Pos.CENTER_LEFT);

        var page = UI.pageRoot();
        page.getChildren().addAll(
            new VBox(4, UI.h1("Rent a Vehicle"), UI.muted("Fill in customer info and select a vehicle")),
            customerCard, vehicleCard, periodCard, submitRow);
        return UI.pageScroll(page);
    }

    private VBox setGrow(VBox v) { HBox.setHgrow(v, Priority.ALWAYS); return v; }
}
