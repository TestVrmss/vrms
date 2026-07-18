package soft.eng.presentation.fx;

import javafx.scene.Node;
import javafx.scene.layout.StackPane;

/**
 * Root node: content layer + toast overlay layer.
 * Avoids any FXML file loading.
 */
public final class StackOverlayRoot extends StackPane {

    private final StackPane contentLayer = new StackPane();
    private final StackPane overlayLayer = new StackPane();

    public StackOverlayRoot() {
        overlayLayer.setPickOnBounds(false);
        overlayLayer.setMouseTransparent(false);
        getChildren().addAll(contentLayer, overlayLayer);
        setStyle("-fx-background-color:" + AppTheme.BG + ";");
    }

    public void setContent(Node node) {
        contentLayer.getChildren().setAll(node);
    }

    public Node getContent() {
        return contentLayer.getChildren().isEmpty() ? this :
               contentLayer.getChildren().get(0);
    }

    public StackPane getOverlay() { return overlayLayer; }
}
