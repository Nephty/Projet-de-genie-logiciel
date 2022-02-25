package front.controllers;

import BenkyngApp.Main;
import back.user.Account;
import back.user.Notification;
import front.animation.FadeInTransition;
import front.animation.threads.FadeOutThread;
import front.navigation.Flow;
import front.navigation.navigators.BackButtonNavigator;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.input.MouseEvent;
import javafx.util.Duration;

import java.util.Calendar;

public class DetailsSceneController extends Controller implements BackButtonNavigator {
    @FXML
    public Button backButton, historyButton, fetchAccountButton, transferButton;
    @FXML
    public ListView<Account> accountsListView;
    // TODO : back-end : implement account
    @FXML
    public Label lastUpdateTimeLabel, loadingAccountsLabel;

    public void initialize() {
        fetchAccounts();
        accountsListView.setItems(FXCollections.observableArrayList(new Account("account A"), new Account("account B")));
    }

    @Override
    public void handleBackButtonNavigation(MouseEvent event) {
        Main.setScene(Flow.back());
    }

    @FXML
    public void handleBackButtonClicked(MouseEvent event) {
        handleBackButtonNavigation(event);
    }

    @FXML
    public void handleHistoryButtonClicked(MouseEvent event) {
        // TODO : navigate to history scene
    }

    @FXML
    public void handleFetchAccountsButtonClicked(MouseEvent event) {
        fetchAccounts();
    }

    @FXML
    public void handleTransferButtonClicked(MouseEvent event) {
        // TODO : navigate to transfer scene
    }

    public void fetchAccounts() {
        // Execute this only if the label is not visible (that is, only if we are not already retrieving data etc)
        if (loadingAccountsLabel.getOpacity() == 0.0) {
            int fadeInDuration = 1000;
            int fadeOutDuration = fadeInDuration;
            int sleepDuration = 1000;
            FadeOutThread sleepAndFadeOutLoadingNotificationsLabelFadeThread;
            // Fade the label "updating notifications..." in to 1.0 opacity
            FadeInTransition.playFromStartOn(loadingAccountsLabel, Duration.millis(fadeInDuration));
            // We use a new Thread, so we can sleep the method for a few hundreds of milliseconds so that the label
            // doesn't instantly go away when the notifications are retrieved.
            sleepAndFadeOutLoadingNotificationsLabelFadeThread = new FadeOutThread();
            // Save actual time and date
            Calendar c = Calendar.getInstance();
            // Update lastUpdateLabel with the new time and date
            lastUpdateTimeLabel.setText("Last update : " + formatCurrentTime(c));
            // Fetch notifications and put them in the listview
            // TODO : back-end : fetch notifications from the database and put them in the listview only if they are not dismissed
            // Fade the label "updating notifications..." out to 0.0 opacity
            sleepAndFadeOutLoadingNotificationsLabelFadeThread.start(fadeOutDuration, sleepDuration + fadeInDuration, loadingAccountsLabel);
        }
    }
}
