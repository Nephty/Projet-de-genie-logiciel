package front.animation;

import javafx.animation.FadeTransition;
import javafx.scene.Node;
import javafx.util.Duration;

public class FadeOutTransition {
    private static FadeTransition FT;

    public static void playFromStartOn(Node node, Duration duration) {
        node.setVisible(true);
        FT = new FadeTransition(duration);
        FT.setFromValue(1f);
        FT.setToValue(0f);
        FT.setCycleCount(1);
        FT.setAutoReverse(false);
        FT.setNode(node);
        FT.playFromStart();
        node.setVisible(false);
    }
}
