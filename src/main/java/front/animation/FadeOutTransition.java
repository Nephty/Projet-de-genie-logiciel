package front.animation;

import javafx.animation.FadeTransition;
import javafx.scene.Node;
import javafx.util.Duration;

public class FadeOutTransition {
    private static FadeTransition FT;

    public static void playFromStartOn(Node node, Duration duration) {
        FT = new FadeTransition(duration, node);
        FT.setToValue(0);
        FT.setCycleCount(1);
        FT.setAutoReverse(false);
        FT.playFromStart();
    }
}
