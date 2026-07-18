package soft.eng.presentation.fx;

import javafx.animation.*;
import javafx.application.Application;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.util.Duration;

public final class App extends Application {

    private static Stage primaryStage;
    private static ToastService toastService;
    private static StackOverlayRoot overlayRoot;

    @Override
    public void start(Stage stage) {
        primaryStage = stage;

        overlayRoot = new StackOverlayRoot();
        toastService = new ToastService(overlayRoot.getOverlay());

        var loginScreen = new LoginScreen(toastService);
        overlayRoot.setContent(loginScreen.build());

        var scene = new Scene(overlayRoot, 1280, 780);
        scene.getStylesheets().add(AppTheme.dataUri());

        stage.setTitle("VRMS \u2014 Vehicle Rental Management System");
        stage.setScene(scene);
        stage.setMinWidth(1050);
        stage.setMinHeight(680);
        stage.centerOnScreen();
        stage.show();
    }

    public static void navigateTo(Parent page) {
        var current = overlayRoot.getContent();
        var ft = new FadeTransition(Duration.millis(120), current);
        ft.setFromValue(1); ft.setToValue(0);
        ft.setOnFinished(e -> {
            overlayRoot.setContent(page);
            page.setOpacity(0);
            var fi = new FadeTransition(Duration.millis(200), page);
            fi.setFromValue(0); fi.setToValue(1);
            fi.play();
        });
        ft.play();
    }

    public static ToastService toast() { return toastService; }
    public static Stage stage()        { return primaryStage; }

    public static void main(String[] args) { launch(args); }
}
