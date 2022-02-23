package front.controllers;

import BenkyngApp.Main;
import front.animation.FadeInTransition;
import front.animation.threads.FadeOutThread;
import front.navigation.Flow;
import front.navigation.navigators.BackButtonNavigator;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.input.MouseEvent;
import javafx.util.Duration;

import java.util.Calendar;

public class NotificationsSceneController extends Controller implements BackButtonNavigator {
    @FXML
    public Button backButton, dismissButton, flagButton, fetchNotificationsButton;
    @FXML
    public Label loadingNotificationsLabel;
    @FXML
    public Label lastUpdateTimeLabel;
    // TODO : back-end : implement notifications to make this a ListView<Notification>
    @FXML
    public ListView notificationsListView;

    FadeOutThread sleepAndFadeOutLoadingNotificationsLabelFadeThread;

    public void handleBackButtonClicked(MouseEvent event) {
        handleBackButtonNavigation(event);
    }

    @Override
    public void handleBackButtonNavigation(MouseEvent event) {
        Main.setScene(Flow.back());
        loadingNotificationsLabel.setVisible(false);
    }

    public void handleDismissButtonClicked(MouseEvent event) {
        // TODO : back-end : implement "dismissed" attribute of notification
    }

    public void handleFlagButtonClicked(MouseEvent event) {
        // TODO : back-end : implement "flagged" attribute of notification
    }

    /**
     * Fetches notifications from the database to display them
     */
    public void fetchNotifications() {
        // Execute this only if the label is not visible (that is, only if we are not already retrieving data etc)
        if (loadingNotificationsLabel.getOpacity() == 0.0) {
            int fadeInDuration = 1000;
            int fadeOutDuration = fadeInDuration;
            int sleepDuration = 1000;
            // Fade the label "updating notifications..." in to 1.0 opacity
            FadeInTransition.playFromStartOn(loadingNotificationsLabel, Duration.millis(fadeInDuration));
            // We use a new Thread, so we can sleep the method for a few hundreds of milliseconds so that the label
            // doesn't instantly go away when the notifications are retrieved. This is purely aesthetic, and I probably
            // shouldn't have spent my entire night trying to debug this thing that took ages to work.
            // But it looks cool.
            sleepAndFadeOutLoadingNotificationsLabelFadeThread = new FadeOutThread();
            // Save actual time and date
            Calendar c = Calendar.getInstance();
            // Update lastUpdateLabel with the new time and date
            lastUpdateTimeLabel.setText("Last update : " + formatCurrentTime(c));
            // Fetch notifications and put them in the listview
            // TODO : back-end : fetch notifications from the database and put them in the listview
            // Fade the label "updating notifications..." out to 0.0 opacity
            sleepAndFadeOutLoadingNotificationsLabelFadeThread.customStart(fadeInDuration, fadeOutDuration, sleepDuration, loadingNotificationsLabel);
        }
    }

    public static String formatCurrentTime(Calendar c) {
        String res = "";
        if (c.get(Calendar.DAY_OF_MONTH) < 10) res += "0";
        res += c.get(Calendar.DAY_OF_MONTH);
        res += "-";
        if (c.get(Calendar.MONTH) + 1 < 10) res += "0";
        res += c.get(Calendar.MONTH) + 1;
        res += "-";
        res += c.get(Calendar.YEAR);
        res += " ~ ";
        if (c.get(Calendar.HOUR_OF_DAY) < 10) res += "0";
        res += c.get(Calendar.HOUR_OF_DAY);
        res += ":";
        if (c.get(Calendar.MINUTE) < 10) res += "0";
        res += c.get(Calendar.MINUTE);
        res += ":";
        if (c.get(Calendar.SECOND) < 10) res += "0";
        res += c.get(Calendar.SECOND);
        return res;
    }

    public void handleFetchNotificationsButtonClicked(MouseEvent event) {
        fetchNotifications();
    }
}
