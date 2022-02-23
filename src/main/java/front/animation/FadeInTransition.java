package front.animation;

import javafx.animation.FadeTransition;
import javafx.scene.Node;
import javafx.util.Duration;

public class FadeInTransition {
    private static FadeTransition FT;

    public static void playFromStartOn(Node node, Duration duration) {
        FT = new FadeTransition(duration, node);
        FT.setToValue(1);
        FT.setCycleCount(1);
        FT.setAutoReverse(false);
        FT.playFromStart();
    }
}
