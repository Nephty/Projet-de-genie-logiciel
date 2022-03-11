package client.front.controllers;

import client.BenkyngApp.Main;
import client.back.user.Notification;
import client.front.animation.FadeInTransition;
import client.front.animation.threads.FadeOutThread;
import client.front.navigation.Flow;
import client.front.navigation.navigators.BackButtonNavigator;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
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
    // TODO : client.back-end : implement notifications to make this a ListView<Notification>
    @FXML
    public ListView<Notification> notificationsListView;

    private Notification selectedNotification;

    public void initialize() {
        fetchNotifications();
//        notificationsListView.setItems(FXCollections.observableArrayList(new Notification("notification A"), new Notification("notification B")));
    }

    @FXML
    public void handleBackButtonClicked(MouseEvent event) {
        handleBackButtonNavigation(event);
    }

    @Override
    public void handleBackButtonNavigation(MouseEvent event) {
        Main.setScene(Flow.back());
    }

    @Override
    public void emulateBackButtonClicked() {
        handleBackButtonNavigation(null);
    }

    @FXML
    public void handleDismissButtonClicked(MouseEvent event) {
        if (notificationsListView.getSelectionModel().getSelectedItems().size() > 0) {
            // TODO : client.back-end : implement "dismissed" attribute of notification, change it accordingly for all selected notifications and commit changes to database
        }
    }

    @FXML
    public void handleFlagButtonClicked(MouseEvent event) {
        if (notificationsListView.getSelectionModel().getSelectedItems().size() > 0) {
            // TODO : client.back-end : implement "flagged" attribute of notification, change it accordingly for all selected notifications and commit changes to database
        }
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
            FadeOutThread sleepAndFadeOutLoadingNotificationsLabelFadeThread;
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
            // TODO : client.back-end : fetch notifications from the database and put them in the listview only if they are not dismissed
            // Fade the label "updating notifications..." out to 0.0 opacity
            sleepAndFadeOutLoadingNotificationsLabelFadeThread.start(fadeOutDuration, sleepDuration + fadeInDuration, loadingNotificationsLabel);
        }
    }

    @FXML
    public void handleFetchNotificationsButtonClicked(MouseEvent event) {
        fetchNotifications();
    }

    @FXML
    public void handleButtonKeyReleased(KeyEvent event) {
        if (event.getCode() == KeyCode.ESCAPE) {
            emulateBackButtonClicked();
            event.consume();
        }
    }
}