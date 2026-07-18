package soft.eng.presentation.fx;

import javafx.animation.*;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import javafx.util.Duration;

/** Displays transient toast notifications in the top-right corner. */
public final class ToastService {

    public enum Type { SUCCESS, ERROR, WARNING, INFO }

    private final StackPane overlay;
    private final VBox stack;

    public ToastService(StackPane overlay) {
        this.overlay = overlay;
        this.stack = new VBox(8);
        stack.setAlignment(Pos.TOP_RIGHT);
        stack.setPickOnBounds(false);
        stack.setMaxSize(Region.USE_PREF_SIZE, Region.USE_PREF_SIZE);
        StackPane.setAlignment(stack, Pos.TOP_RIGHT);
        StackPane.setMargin(stack, new Insets(16, 16, 0, 0));
        overlay.getChildren().add(stack);
    }

    public void show(String message, Type type) {
        Platform.runLater(() -> {
            var toast = buildToast(message, type);
            stack.getChildren().add(0, toast);

            var fadeIn = new FadeTransition(Duration.millis(220), toast);
            fadeIn.setFromValue(0); fadeIn.setToValue(1); fadeIn.play();

            var pause = new PauseTransition(Duration.seconds(type == Type.ERROR ? 5 : 3.5));
            pause.setOnFinished(e -> {
                var fadeOut = new FadeTransition(Duration.millis(300), toast);
                fadeOut.setFromValue(1); fadeOut.setToValue(0);
                fadeOut.setOnFinished(fe -> stack.getChildren().remove(toast));
                fadeOut.play();
            });
            pause.play();
        });
    }

    private static HBox buildToast(String msg, Type type) {
        String icon, bg, fg;
        switch (type) {
            case SUCCESS -> { icon = "\u2714"; bg = "rgba(16,185,129,0.92)"; fg = "white"; }
            case ERROR   -> { icon = "\u2718"; bg = "rgba(239,68,68,0.92)";  fg = "white"; }
            case WARNING -> { icon = "\u26A0"; bg = "rgba(245,158,11,0.92)"; fg = "white"; }
            default      -> { icon = "\u2139"; bg = "rgba(129,140,248,0.15)"; fg = "#818CF8"; }
        }
        var iconL = new Label(icon);
        iconL.setStyle("-fx-text-fill:" + fg + ";-fx-font-size:14px;-fx-font-weight:bold;");
        var msgL  = new Label(msg);
        msgL.setStyle("-fx-text-fill:" + fg + ";-fx-font-size:13px;-fx-font-weight:bold;");
        msgL.setWrapText(true); msgL.setMaxWidth(320);

        var row = new HBox(10, iconL, msgL);
        row.setAlignment(Pos.CENTER_LEFT);
        row.setPadding(new Insets(12, 16, 12, 16));
        row.setStyle("-fx-background-color:" + bg + ";-fx-background-radius:10;" +
                     "-fx-effect:dropshadow(gaussian,rgba(0,0,0,0.5),20,0.3,0,4);");
        row.setMaxWidth(380);
        row.setCursor(javafx.scene.Cursor.HAND);
        return row;
    }
}
