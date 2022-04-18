package front.controllers;

import front.animation.FadeInTransition;
import front.animation.threads.FadeOutThread;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.input.MouseEvent;
import javafx.util.Duration;

import java.util.Calendar;

/**
 * This mother Controller class gives access to a few useful methods to all children classes.
 * Having access to these methods provide shortcuts. For example :
 * The method formatCurrentTime() uses a <code>Calendar</code> object to return the current time in a defined format.
 * This allows every controller to have access to a method to format time, rather than defining it in all FXML controllers.
 * It also makes the code more readable.
 * @author Arnaud MOREAU
 */
public class Controller {
    /**
     * Returns the visibility of a <code>Node</code> using its opacity. If the node's opacity is 0, then it is
     * considered invisible. If the node's opacity is not 0, it is considered visible. This method provides an
     * alternative to the isVisible() method which returns whether the attribute "visible" is true or false.
     * This is mainly used with transitions, since these use the opacity of the node, and not it's core visibility.
     * It is not recommended using this method if you don't alter the node's opacity is such a way that it
     * purposely reaches 0 or 1 (for example, using fade in/fade out transitions). If you alter the node's visibility
     * to make it, let's say, 0.4, using the <code>visible</code> attribute of the node is probably a better way to go,
     * because you'd probably want to know if it is visible at its core, not if the opacity is other than 0.
     *
     * @param node - <code>Node</code> - The node to check
     * @return Whether the node's opacity is not 0 or not : that is, true if the node's opacity is not 0,
     * false otherwise.
     */
    public static boolean isVisibleUsingOpacity(Node node) {
        return node.getOpacity() != 0;
    }

    /**
     * Returns a <code>String</code> containing the current time and date of the given <code>Calendar</code>,
     * but formatted for display.
     *
     * @param calendar The calendar providing the desired time and date
     * @return A string containing the current time and date of the given <code>Calendar</code>, but formatted for
     * display.
     */
    public static String formatCurrentTime(Calendar calendar) {
        String res = "";
        if (calendar.get(Calendar.DAY_OF_MONTH) < 10) res += "0";
        res += calendar.get(Calendar.DAY_OF_MONTH);
        res += "-";
        if (calendar.get(Calendar.MONTH) + 1 < 10) res += "0";
        res += calendar.get(Calendar.MONTH) + 1;
        res += "-";
        res += calendar.get(Calendar.YEAR);
        res += " ~ ";
        if (calendar.get(Calendar.HOUR_OF_DAY) < 10) res += "0";
        res += calendar.get(Calendar.HOUR_OF_DAY);
        res += ":";
        if (calendar.get(Calendar.MINUTE) < 10) res += "0";
        res += calendar.get(Calendar.MINUTE);
        res += ":";
        if (calendar.get(Calendar.SECOND) < 10) res += "0";
        res += calendar.get(Calendar.SECOND);
        return res;
    }

    /**
     * Returns a new style line with the darker background color to use when the mouse enters a button.
     *
     * @param button The entered button
     * @return The CSS line
     */
    public static String formatNewCSSLineMouseEntered(Button button) {
        String CSSLine = "";
        if (button.getPrefWidth() == 350) {
            // Big buttons
            CSSLine = "-fx-background-color: rgb(190, 185, 180); -fx-border-color: rgb(10, 10, 20); -fx-border-width: 2.5; -fx-border-insets: -2.5; -fx-border-radius: 10;";
        } else if (button.getPrefWidth() == 120) {
            // Language & Back buttons
            CSSLine = "-fx-background-color: rgb(190, 185, 180); -fx-border-color: rgb(10, 10, 20); -fx-border-width: 1.5; -fx-border-insets: -1.5; -fx-border-radius: 5;";
        } else if (button.getPrefWidth() == 296) {
            // Change password button
            CSSLine = "-fx-background-color: rgb(190, 185, 180); -fx-border-color: rgb(10, 10, 20); -fx-border-width: 2.5; -fx-border-insets: -2.5; -fx-border-radius: 10;";
        } else if (button.getPrefWidth() == 50) {
            // Square buttons for PIN
            CSSLine = "-fx-background-color: rgb(190, 185, 180); -fx-border-color: rgb(10, 10, 20); -fx-border-width: 2.5; -fx-border-insets: -1.5; -fx-border-radius: 4;";
        } else if (button.getPrefWidth() == 200) {
            if (button.getText().toLowerCase().contains("path")) {
                CSSLine = "-fx-background-color: rgb(190, 185, 180); -fx-border-color: rgb(10, 10, 20); -fx-border-width: 1.5; -fx-border-insets: -1.5; -fx-border-radius: 5;";
            } else {
                // Confirm button for PIN
                CSSLine = "-fx-background-color: rgb(190, 185, 180); -fx-border-color: rgb(10, 10, 20); -fx-border-width: 2.5; -fx-border-insets: -2.5; -fx-border-radius: 10;";
            }
        } else if (button.getPrefWidth() == 250) {
            // Export buttons
            CSSLine = "-fx-background-color: rgb(190, 185, 180); -fx-border-color: rgb(10, 10, 20); -fx-border-width: 1.5; -fx-border-insets: -1.5; -fx-border-radius: 5;";
        } else if (button.getPrefWidth() == 170) {
            // Right side buttons
            CSSLine = "-fx-background-color: rgb(190, 185, 180); -fx-border-color: rgb(10, 10, 20); -fx-border-width: 1.5; -fx-border-insets: -1.5; -fx-border-radius: 5;";
        } else if (button.getPrefWidth() == 75) {
            // Add button for language
            CSSLine = "-fx-background-color: rgb(190, 185, 180); -fx-border-color: rgb(10, 10, 20); -fx-border-width: 1.5; -fx-border-insets: -1.5; -fx-border-radius: 5;";
        } else if (button.getPrefWidth() == 135) {
            // Add/remove account for visualisation
            CSSLine = "-fx-background-color: rgb(190, 185, 180); -fx-border-color: rgb(10, 10, 20); -fx-border-width: 1.5; -fx-border-insets: -1.5; -fx-border-radius: 5;";
        } else if (button.getPrefWidth() == 100) {
            // Search button
            CSSLine = "-fx-background-color: rgb(190, 185, 180); -fx-border-color: rgb(10, 10, 20); -fx-border-width: 1.5; -fx-border-insets: -1.5; -fx-border-radius: 5;";
        }
        return CSSLine;
    }

    /**
     * Returns a new style line with the original background color to use when the mouse exits a button.
     *
     * @param button The exited button
     * @return The CSS line
     */
    public static String formatNewCSSLineMouseExited(Button button) {
        String CSSLine = "";
        if (button.getPrefWidth() == 350) {
            // Big buttons
            CSSLine = "-fx-background-color: rgb(210, 205, 200); -fx-border-color: rgb(10, 10, 20); -fx-border-width: 2.5; -fx-border-insets: -2.5; -fx-border-radius: 10;";
        } else if (button.getPrefWidth() == 120) {
            // Language & Back buttons
            CSSLine = "-fx-background-color: rgb(210, 205, 200); -fx-border-color: rgb(10, 10, 20); -fx-border-width: 1.5; -fx-border-insets: -1.5; -fx-border-radius: 5;";
        } else if (button.getPrefWidth() == 296) {
            // Change password button
            CSSLine = "-fx-background-color: rgb(210, 205, 200); -fx-border-color: rgb(10, 10, 20); -fx-border-width: 2.5; -fx-border-insets: -2.5; -fx-border-radius: 10;";
        } else if (button.getPrefWidth() == 50) {
            // Square buttons for PIN
            CSSLine = "-fx-background-color: rgb(210, 205, 200); -fx-border-color: rgb(10, 10, 20); -fx-border-width: 2.5; -fx-border-insets: -1.5; -fx-border-radius: 4;";
        } else if (button.getPrefWidth() == 200) {
            if (button.getText().toLowerCase().contains("path")) {
                CSSLine = "-fx-background-color: rgb(210, 205, 200); -fx-border-color: rgb(10, 10, 20); -fx-border-width: 1.5; -fx-border-insets: -1.5; -fx-border-radius: 5;";
            } else {
                // Confirm button for PIN
                CSSLine = "-fx-background-color: rgb(210, 205, 200); -fx-border-color: rgb(10, 10, 20); -fx-border-width: 2.5; -fx-border-insets: -2.5; -fx-border-radius: 10;";
            }
        } else if (button.getPrefWidth() == 250) {
            // Export buttons
            CSSLine = "-fx-background-color: rgb(210, 205, 200); -fx-border-color: rgb(10, 10, 20); -fx-border-width: 1.5; -fx-border-insets: -1.5; -fx-border-radius: 5;";
        } else if (button.getPrefWidth() == 170) {
            // Right side buttons
            CSSLine = "-fx-background-color: rgb(210, 205, 200); -fx-border-color: rgb(10, 10, 20); -fx-border-width: 1.5; -fx-border-insets: -1.5; -fx-border-radius: 5;";
        } else if (button.getPrefWidth() == 75) {
            // Add button for language
            CSSLine = "-fx-background-color: rgb(210, 205, 200); -fx-border-color: rgb(10, 10, 20); -fx-border-width: 1.5; -fx-border-insets: -1.5; -fx-border-radius: 5;";
        } else if (button.getPrefWidth() == 135) {
            // Add/remove account for visualisation
            CSSLine = "-fx-background-color: rgb(210, 205, 200); -fx-border-color: rgb(10, 10, 20); -fx-border-width: 1.5; -fx-border-insets: -1.5; -fx-border-radius: 5;";
        } else if (button.getPrefWidth() == 100) {
            // Search button
            CSSLine = "-fx-background-color: rgb(210, 205, 200); -fx-border-color: rgb(10, 10, 20); -fx-border-width: 1.5; -fx-border-insets: -1.5; -fx-border-radius: 5;";
        }
        return CSSLine;
    }

    /**
     * Checks if the given passwords match and are not empty.
     *
     * @param password             - <code>String</code> - the password
     * @param passwordConfirmation - <code>String</code> - the password confirmation
     * @return <code>boolean</code> - whether the two passwords match or not
     */
    public boolean passwordMatchesAndIsNotEmpty(String password, String passwordConfirmation) {
        return password.equals(passwordConfirmation) && !password.equals("");
    }

    @FXML
    void handleButtonMouseEntered(MouseEvent event) {
        Button buttonSource = (Button) event.getSource();
        buttonSource.setStyle(formatNewCSSLineMouseEntered(buttonSource));
    }

    @FXML
    void handleButtonMouseExited(MouseEvent event) {
        Button buttonSource = (Button) event.getSource();
        buttonSource.setStyle(formatNewCSSLineMouseExited(buttonSource));
    }

    /**
     * Fades in and out the given node using its opacity values for a pre-determined durations in the following order :
     * - fade in (opacity rises to a level of 1 over a period of 1 second)
     * - sleep (opacity remains at a level of 1)
     * - fade out (opacity decreases to a level of 0 over a period of 1 second)
     * Note that this method forces the fade in and fade out transitions to last for exactly 1 second.
     *
     * @param sleepDuration the time that should pass by after the fade in transition and before the fade out transition
     * @param node          the node which should fade in and out
     */
    public void fadeInAndOutNode(int sleepDuration, Node node) {
        int fadeInDuration = 1000, fadeOutDuration = 1000;
        FadeOutThread sleepAndFadeOutFadeThread;
        FadeInTransition.playFromStartOn(node, Duration.millis(fadeInDuration));
        sleepAndFadeOutFadeThread = new FadeOutThread();
        sleepAndFadeOutFadeThread.start(fadeOutDuration, sleepDuration + fadeInDuration, node);
    }
}
