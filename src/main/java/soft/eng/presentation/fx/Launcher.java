package soft.eng.presentation.fx;

/**
 * Entry point for Eclipse / plain java execution.
 * JavaFX requires the main class NOT to extend Application when run from
 * a class-path-only environment. Launcher delegates to App.main.
 */
public final class Launcher {
    public static void main(String[] args) {
        App.main(args);
    }
}
