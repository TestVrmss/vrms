package soft.eng.presentation.fx;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.text.TextAlignment;

/**
 * Factory for reusable, pre-styled UI components.
 * ALL styling is inline — no external CSS file references.
 */
public final class UI {

    private UI() {}

    // ── Labels ────────────────────────────────────────────────────────────

    public static Label h1(String text) {
        var l = new Label(text);
        l.setStyle("-fx-font-size:26px;-fx-font-weight:bold;-fx-text-fill:" + AppTheme.TEXT + ";");
        return l;
    }
    public static Label h2(String text) {
        var l = new Label(text);
        l.setStyle("-fx-font-size:18px;-fx-font-weight:bold;-fx-text-fill:" + AppTheme.TEXT + ";");
        return l;
    }
    public static Label h3(String text) {
        var l = new Label(text);
        l.setStyle("-fx-font-size:14px;-fx-font-weight:bold;-fx-text-fill:" + AppTheme.TEXT + ";");
        return l;
    }
    public static Label caption(String text) {
        var l = new Label(text);
        l.setStyle("-fx-font-size:11px;-fx-font-weight:bold;-fx-text-fill:" + AppTheme.TEXT_DIM +
                   ";-fx-letter-spacing:1;");
        return l;
    }
    public static Label muted(String text) {
        var l = new Label(text);
        l.setStyle("-fx-font-size:12px;-fx-text-fill:" + AppTheme.TEXT_DIM + ";");
        return l;
    }
    public static Label colored(String text, String color, int size) {
        var l = new Label(text);
        l.setStyle("-fx-text-fill:" + color + ";-fx-font-size:" + size + "px;");
        return l;
    }
    public static Label coloredBold(String text, String color, int size) {
        var l = new Label(text);
        l.setStyle("-fx-text-fill:" + color + ";-fx-font-size:" + size + "px;-fx-font-weight:bold;");
        return l;
    }

    // ── Buttons ───────────────────────────────────────────────────────────

    public static Button btnPrimary(String text) {
        var b = new Button(text);
        b.setStyle(AppTheme.btnPrimary());
        b.setMaxWidth(Double.MAX_VALUE);
        hover(b, AppTheme.btnPrimary(),
            "-fx-background-color:#6366F1;-fx-text-fill:white;-fx-font-weight:bold;" +
            "-fx-background-radius:8;-fx-padding:11 24;-fx-cursor:hand;" +
            "-fx-effect:dropshadow(gaussian,rgba(99,102,241,0.55),18,0.4,0,4);");
        return b;
    }
    public static Button btnSecondary(String text) {
        var b = new Button(text);
        b.setStyle(AppTheme.btnSecondary());
        return b;
    }
    public static Button btnDanger(String text) {
        var b = new Button(text);
        b.setStyle(AppTheme.btnDanger());
        return b;
    }
    public static Button btnGhost(String text) {
        var b = new Button(text);
        b.setStyle("-fx-background-color:transparent;-fx-text-fill:" + AppTheme.TEXT_DIM +
                   ";-fx-font-size:12px;-fx-background-radius:8;-fx-padding:9 16;-fx-cursor:hand;" +
                   "-fx-border-color:rgba(148,163,184,0.15);-fx-border-radius:8;-fx-border-width:1;");
        return b;
    }

    // ── Form fields ───────────────────────────────────────────────────────

    public static TextField field(String prompt) {
        var f = new TextField();
        f.setPromptText(prompt);
        return f;
    }
    public static PasswordField passField(String prompt) {
        var f = new PasswordField();
        f.setPromptText(prompt);
        return f;
    }
    public static TextArea textArea(String prompt, int rows) {
        var a = new TextArea();
        a.setPromptText(prompt);
        a.setPrefRowCount(rows);
        a.setWrapText(true);
        return a;
    }
    public static DatePicker datePicker() {
        var dp = new DatePicker();
        dp.setMaxWidth(Double.MAX_VALUE);
        return dp;
    }
    public static ComboBox<String> combo(String... items) {
        var c = new ComboBox<String>();
        c.getItems().addAll(items);
        c.setMaxWidth(Double.MAX_VALUE);
        return c;
    }
    public static CheckBox checkBox(String text) {
        return new CheckBox(text);
    }

    // ── Layout helpers ────────────────────────────────────────────────────

    /** Labelled form row: caption + field stacked vertically */
    public static VBox formRow(String labelText, Node field) {
        var box = new VBox(6, caption(labelText), field);
        return box;
    }
    public static VBox formRow(String labelText, Node field, String hint) {
        var h = muted(hint);
        h.setStyle(h.getStyle() + "-fx-font-size:11px;-fx-text-fill:" + AppTheme.ACCENT + ";");
        h.setWrapText(true);
        return new VBox(6, caption(labelText), field, h);
    }

    /** Page scroll wrapper */
    public static ScrollPane pageScroll(Node content) {
        var sp = new ScrollPane(content);
        sp.setFitToWidth(true);
        sp.setStyle("-fx-background-color:transparent;-fx-border-color:transparent;");
        sp.getStyleClass().add("edge-to-edge");
        return sp;
    }

    /** A card panel with padding */
    public static VBox card(Node... children) {
        var v = new VBox(14);
        v.getChildren().addAll(children);
        v.setStyle(AppTheme.card());
        v.setPadding(new Insets(20, 22, 20, 22));
        return v;
    }

    /** Page root with standard padding */
    public static VBox pageRoot() {
        var v = new VBox(22);
        v.setStyle("-fx-background-color:" + AppTheme.BG + ";");
        v.setPadding(new Insets(24, 28, 24, 28));
        return v;
    }

    /** Section divider with title */
    public static HBox sectionTitle(String text) {
        var l = caption(text);
        var line = new Separator();
        HBox.setHgrow(line, Priority.ALWAYS);
        var h = new HBox(12, l, line);
        h.setAlignment(Pos.CENTER_LEFT);
        return h;
    }

    /** A badge label */
    public static Label badge(String text, String bg, String fg) {
        var l = new Label(text);
        l.setStyle("-fx-background-color:" + bg + ";-fx-text-fill:" + fg +
                   ";-fx-font-size:10px;-fx-font-weight:bold;" +
                   "-fx-background-radius:4;-fx-padding:3 8;");
        return l;
    }

    /** A horizontal spacer */
    public static Region hSpacer() {
        var r = new Region();
        HBox.setHgrow(r, Priority.ALWAYS);
        return r;
    }
    /** A vertical spacer */
    public static Region vSpacer() {
        var r = new Region();
        VBox.setVgrow(r, Priority.ALWAYS);
        return r;
    }

    // ── KPI Card ─────────────────────────────────────────────────────────

    public static VBox kpiCard(String labelText, String value, String sub, String accentColor) {
        var labelL = caption(labelText);
        var valueL = new Label(value);
        valueL.setStyle("-fx-font-size:38px;-fx-font-weight:bold;-fx-text-fill:" + accentColor + ";");
        var subL = muted(sub);

        // small accent bar at top
        var bar = new Region();
        bar.setPrefHeight(3);
        bar.setMaxWidth(40);
        bar.setStyle("-fx-background-color:" + accentColor + ";-fx-background-radius:2;");

        var card = new VBox(10, bar, labelL, valueL, subL);
        card.setStyle(AppTheme.card() +
            "-fx-border-color:" + accentColor.replace(")", ",0.25)"
                .replace("#", "rgba(")) + ";");
        // fallback: just use the card style
        card.setStyle(AppTheme.card());
        card.setPadding(new Insets(18, 20, 18, 20));
        HBox.setHgrow(card, Priority.ALWAYS);
        return card;
    }

    // ── Utility ───────────────────────────────────────────────────────────

    private static void hover(Button b, String normal, String hovered) {
        b.setOnMouseEntered(e -> b.setStyle(hovered));
        b.setOnMouseExited(e -> b.setStyle(normal));
    }
}
