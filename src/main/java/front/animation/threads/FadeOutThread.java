package front.animation.threads;

import front.animation.FadeOutTransition;
import javafx.scene.control.Label;
import javafx.util.Duration;

public class FadeOutThread extends Thread {
    int sleepDuration, fadeInDuration, fadeOutDuration;
    Label label;

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
