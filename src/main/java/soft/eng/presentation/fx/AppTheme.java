package soft.eng.presentation.fx;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

/**
 * "Nebula" ultra-dark premium theme.
 * CSS is embedded as a constant and loaded via Base64 data-URI
 * so NO external file path is needed.
 */
public final class AppTheme {

    private AppTheme() {}

    /** Returns a data-URI that can be passed to scene.getStylesheets().add() */
    public static String dataUri() {
        return "data:text/css;base64," +
            Base64.getEncoder().encodeToString(CSS.getBytes(StandardCharsets.UTF_8));
    }

    // ------------------------------------------------------------------ //
    //  PALETTE                                                             //
    // ------------------------------------------------------------------ //
    public static final String BG         = "#080C14";
    public static final String SURFACE    = "#0F1623";
    public static final String SURFACE2   = "#162032";
    public static final String SURFACE3   = "#1D2A40";
    public static final String ACCENT     = "#818CF8";
    public static final String ACCENT_DIM = "#4F46E5";
    public static final String GREEN      = "#10B981";
    public static final String RED        = "#EF4444";
    public static final String AMBER      = "#F59E0B";
    public static final String CYAN       = "#22D3EE";
    public static final String TEXT       = "#E2E8F0";
    public static final String TEXT_DIM   = "#94A3B8";
    public static final String TEXT_FAINT = "#334155";
    public static final String BORDER     = "rgba(129,140,248,0.15)";

    // ------------------------------------------------------------------ //
    //  INLINE STYLE HELPERS                                                //
    // ------------------------------------------------------------------ //
    public static String card() {
        return "-fx-background-color:" + SURFACE + ";" +
               "-fx-background-radius:12;" +
               "-fx-border-color:" + BORDER + ";" +
               "-fx-border-radius:12;" +
               "-fx-border-width:1;" +
               "-fx-effect:dropshadow(gaussian,rgba(0,0,0,0.5),20,0.2,0,6);";
    }
    public static String inputField() {
        return "-fx-background-color:" + SURFACE2 + ";" +
               "-fx-text-fill:" + TEXT + ";" +
               "-fx-prompt-text-fill:" + TEXT_FAINT + ";" +
               "-fx-background-radius:8;" +
               "-fx-border-color:rgba(129,140,248,0.20);" +
               "-fx-border-radius:8;" +
               "-fx-border-width:1;" +
               "-fx-padding:10 14;" +
               "-fx-font-size:13px;";
    }
    public static String btnPrimary() {
        return "-fx-background-color:" + ACCENT_DIM + ";" +
               "-fx-text-fill:white;" +
               "-fx-font-weight:bold;" +
               "-fx-font-size:13px;" +
               "-fx-background-radius:8;" +
               "-fx-padding:11 24;" +
               "-fx-cursor:hand;" +
               "-fx-effect:dropshadow(gaussian,rgba(99,102,241,0.35),14,0.3,0,3);";
    }
    public static String btnSecondary() {
        return "-fx-background-color:rgba(129,140,248,0.10);" +
               "-fx-text-fill:" + ACCENT + ";" +
               "-fx-font-weight:bold;" +
               "-fx-font-size:12px;" +
               "-fx-background-radius:8;" +
               "-fx-padding:9 18;" +
               "-fx-cursor:hand;" +
               "-fx-border-color:rgba(129,140,248,0.25);" +
               "-fx-border-radius:8;" +
               "-fx-border-width:1;";
    }
    public static String btnDanger() {
        return "-fx-background-color:rgba(239,68,68,0.12);" +
               "-fx-text-fill:" + RED + ";" +
               "-fx-font-weight:bold;" +
               "-fx-font-size:12px;" +
               "-fx-background-radius:8;" +
               "-fx-padding:9 18;" +
               "-fx-cursor:hand;" +
               "-fx-border-color:rgba(239,68,68,0.25);" +
               "-fx-border-radius:8;" +
               "-fx-border-width:1;";
    }
    public static String label(String color, int size) {
        return "-fx-text-fill:" + color + ";-fx-font-size:" + size + "px;";
    }
    public static String labelBold(String color, int size) {
        return label(color, size) + "-fx-font-weight:bold;";
    }

    // ------------------------------------------------------------------ //
    //  FULL CSS STYLESHEET                                                 //
    // ------------------------------------------------------------------ //
    public static final String CSS = """
/* ================================================================
   VRMS NEBULA THEME  — ultra-dark premium dashboard
   ================================================================ */

.root {
    -fx-font-family: 'Segoe UI', 'Inter', 'Helvetica Neue', sans-serif;
    -fx-background-color: #080C14;
}

/* ScrollPane */
.scroll-pane { -fx-background-color: transparent; -fx-border-color: transparent; }
.scroll-pane > .viewport { -fx-background-color: transparent; }
.scroll-bar:vertical .thumb, .scroll-bar:horizontal .thumb {
    -fx-background-color: #1D2A40; -fx-background-radius: 4;
}
.scroll-bar .track { -fx-background-color: transparent; }
.scroll-bar .increment-button, .scroll-bar .decrement-button {
    -fx-background-color: transparent; -fx-padding: 0;
}

/* TableView */
.table-view { -fx-background-color: transparent; -fx-border-color: transparent;
              -fx-table-cell-border-color: transparent; }
.table-view .column-header-background {
    -fx-background-color: #162032; -fx-background-radius: 8 8 0 0;
}
.table-view .column-header { -fx-background-color: transparent; -fx-padding: 10 12; }
.table-view .column-header .label {
    -fx-text-fill: #94A3B8; -fx-font-size: 11px; -fx-font-weight: bold; -fx-alignment: CENTER_LEFT;
}
.table-view .table-row-cell {
    -fx-background-color: transparent;
    -fx-border-color: transparent transparent rgba(129,140,248,0.08) transparent;
    -fx-border-width: 0 0 1 0;
    -fx-table-cell-border-color: transparent;
}
.table-view .table-row-cell:odd  { -fx-background-color: rgba(255,255,255,0.012); }
.table-view .table-row-cell:hover { -fx-background-color: rgba(129,140,248,0.06); }
.table-view .table-row-cell:selected { -fx-background-color: rgba(129,140,248,0.12); }
.table-view .table-cell {
    -fx-text-fill: #E2E8F0; -fx-font-size: 13px; -fx-padding: 10 12;
    -fx-border-color: transparent; -fx-alignment: CENTER_LEFT;
}
.table-view .placeholder .label { -fx-text-fill: #334155; -fx-font-size: 13px; }

/* TextField / PasswordField / TextArea / DatePicker */
.text-field, .password-field {
    -fx-background-color: #162032;
    -fx-text-fill: #E2E8F0;
    -fx-prompt-text-fill: #334155;
    -fx-background-radius: 8;
    -fx-border-color: rgba(129,140,248,0.20);
    -fx-border-radius: 8;
    -fx-border-width: 1;
    -fx-padding: 10 14;
    -fx-font-size: 13px;
    -fx-highlight-fill: rgba(129,140,248,0.3);
}
.text-field:focused, .password-field:focused {
    -fx-border-color: #818CF8;
    -fx-effect: dropshadow(gaussian, rgba(129,140,248,0.25), 12, 0.3, 0, 0);
}
.text-area {
    -fx-background-color: #162032;
    -fx-text-fill: #E2E8F0;
    -fx-prompt-text-fill: #334155;
    -fx-border-color: rgba(129,140,248,0.20);
    -fx-border-width: 1;
    -fx-border-radius: 8;
    -fx-background-radius: 8;
    -fx-font-size: 13px;
}
.text-area .content {
    -fx-background-color: #162032;
    -fx-background-radius: 8;
    -fx-padding: 10 14;
}
.text-area:focused { -fx-border-color: #818CF8; }

.date-picker { -fx-background-radius: 8; }
.date-picker .text-field {
    -fx-background-color: #162032;
    -fx-text-fill: #E2E8F0;
    -fx-border-radius: 8 0 0 8;
}
.date-picker .arrow-button {
    -fx-background-color: #162032;
    -fx-border-color: rgba(129,140,248,0.20);
    -fx-border-width: 1;
    -fx-border-radius: 0 8 8 0;
    -fx-padding: 0 10;
}
.date-picker .arrow-button .arrow { -fx-background-color: #94A3B8; }
.date-picker:focused .text-field { -fx-border-color: #818CF8; }

/* ComboBox */
.combo-box {
    -fx-background-color: #162032;
    -fx-border-color: rgba(129,140,248,0.20);
    -fx-border-width: 1;
    -fx-border-radius: 8;
    -fx-background-radius: 8;
    -fx-padding: 4 8;
}
.combo-box .list-cell { -fx-background-color: transparent; -fx-text-fill: #E2E8F0; -fx-font-size: 13px; }
.combo-box-popup .list-view { -fx-background-color: #162032; -fx-border-color: rgba(129,140,248,0.20); }
.combo-box-popup .list-cell { -fx-background-color: transparent; -fx-text-fill: #E2E8F0; -fx-padding: 8 12; }
.combo-box-popup .list-cell:hover { -fx-background-color: rgba(129,140,248,0.10); }
.combo-box .arrow-button { -fx-background-color: transparent; }
.combo-box .arrow { -fx-background-color: #94A3B8; }

/* CheckBox */
.check-box { -fx-text-fill: #E2E8F0; -fx-font-size: 13px; }
.check-box .box {
    -fx-background-color: #162032;
    -fx-border-color: rgba(129,140,248,0.30);
    -fx-border-radius: 4; -fx-background-radius: 4;
    -fx-padding: 3;
}
.check-box:selected .mark { -fx-background-color: #818CF8; }
.check-box:selected .box { -fx-border-color: #818CF8; }

/* Separator */
.separator .line { -fx-border-color: rgba(129,140,248,0.12); }

/* Tooltip */
.tooltip {
    -fx-background-color: #1D2A40;
    -fx-text-fill: #E2E8F0;
    -fx-border-color: rgba(129,140,248,0.20);
    -fx-border-width: 1;
    -fx-border-radius: 6;
    -fx-background-radius: 6;
    -fx-font-size: 12px;
}
""";
}
