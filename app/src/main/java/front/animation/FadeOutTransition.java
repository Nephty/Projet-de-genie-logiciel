package front.animation;

import javafx.animation.FadeTransition;
import javafx.scene.Node;
import javafx.util.Duration;

/**
 * This class is used to create a fade in transition. This class must be used along with the <code>FadeOutThread</code>
 * class, in order to use multi-threading. How to use :
 * - Create a new FadeOutThread
 * - Use the start method of the thread
 * Note : never use this class on its own. Follow the previous how-to-use steps.
 * @author Arnaud MOREAU
 */
public class FadeOutTransition {

    /**
     * Starts a fade out transition on the given <code>Node</code> and for the given <code>Duration</code>, during which
     * the node slowly reaches an opacity level of 0, at constant pace.
     *
     * @param node     the node on which the transition should be applied
     * @param duration the duration of the transition
     */
    public static void playFromStartOn(Node node, Duration duration) {
        FadeTransition FT = new FadeTransition(duration, node);
        FT.setToValue(0);
        FT.setCycleCount(1);
        FT.setAutoReverse(false);
        FT.playFromStart();
    }
}
