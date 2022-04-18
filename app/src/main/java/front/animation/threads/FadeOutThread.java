package front.animation.threads;

import front.animation.FadeOutTransition;
import javafx.scene.Node;
import javafx.util.Duration;

/**
 * This class is used to make use of a fade out transition. It creates a new <code>Thread</code> and uses it
 * to properly fade out the given <code>Node</code>, while the main thread can keep going on its own, independently of
 * the fade out transition.
 * @author Arnaud MOREAU
 */
public class FadeOutThread extends Thread {
    int shiftTransition, fadeInDuration, fadeOutDuration;
    Node node;

    /**
     * Sets the sleep duration, fade in and out durations, sleep duration and the <code>Node</code> on which the
     * transition should be applied.
     *
     * @param fadeOutDuration the duration of the fade out transition (in ms)
     * @param shiftTransition the shift of the transition (in ms, should include the duration of the
     *                        *                        previous fade in transition (if there were any) in order to prevent this transition
     *                        *                        from beginning to early), that is the time during which the node should be
     *                        visible and during which nothing should happen, before we start the fade out transition
     * @param node            the node on which the transition should be applied
     */
    public void start(int fadeOutDuration, int shiftTransition, Node node) {
        this.fadeOutDuration = fadeOutDuration;
        this.shiftTransition = shiftTransition;
        this.node = node;
        super.start();
    }

    /**
     * Takes care of shifting the beginning of the transition by sleeping the thread and then proceeds to fade out
     * the node.
     */
    @Override
    public void run() {
        try {
            Thread.sleep(shiftTransition + fadeInDuration);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        FadeOutTransition.playFromStartOn(node, Duration.millis(fadeOutDuration));
    }
}
