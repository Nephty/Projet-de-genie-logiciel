package front.animation.threads;

import front.animation.FadeOutTransition;
import javafx.scene.control.Label;
import javafx.util.Duration;

/**
 * This class is used to make use of a fade out transition. It creates a new <code>Thread</code> and uses it
 * to properly fade out the given <code>Node</code>, while the main thread can keep going on its own, independently of
 * the fade out transition.
 */
public class FadeOutThread extends Thread {
    int sleepDuration, fadeInDuration, fadeOutDuration;
    Label label;

    /**
     * Sets the sleep duration, fade
     * @param fadeInDuration
     * @param fadeOutDuration
     * @param sleepDuration
     * @param label
     */
    public void customStart(int fadeInDuration, int fadeOutDuration, int sleepDuration, Label label) {
        this.fadeInDuration = fadeInDuration;
        this.fadeOutDuration = fadeOutDuration;
        this.sleepDuration = sleepDuration;
        this.label = label;
        super.start();
    }

    @Override
    public void run() {
        try {
            Thread.sleep(sleepDuration + fadeInDuration);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        FadeOutTransition.playFromStartOn(label, Duration.millis(1000));
    }
}
