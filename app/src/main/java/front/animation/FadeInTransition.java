package front.animation;

import javafx.animation.FadeTransition;
import javafx.scene.Node;
import javafx.util.Duration;

/**
 * This class is used to create a fade in transition. This class can be used without multi-threading.
 * @author Arnaud MOREAU
 */
public class FadeInTransition {

    /**
     * Starts a fade in transition on the given <code>Node</code> and for the given <code>Duration</code>, during which
     * the node slowly reaches an opacity level of 1, at constant pace.
     *
     * @param node     the node on which the transition should be applied
     * @param duration the duration of the transition
     */
    public static void playFromStartOn(Node node, Duration duration) {
        FadeTransition FT = new FadeTransition(duration, node);
        FT.setToValue(1);
        FT.setCycleCount(1);
        FT.setAutoReverse(false);
        FT.playFromStart();
    }
}
