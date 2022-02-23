package front.animation;

import javafx.animation.FadeTransition;
import javafx.scene.Node;
import javafx.util.Duration;

public class FadeInTransition {
    private static FadeTransition FT;

    public static void playFromStartOn(Node node, Duration duration) {
        node.setVisible(false);
        FT = new FadeTransition(duration);
        FT.setFromValue(0f);
        FT.setToValue(1f);
        FT.setCycleCount(1);
        FT.setAutoReverse(false);
        FT.setNode(node);
        FT.playFromStart();
        node.setVisible(true);
    }
}
