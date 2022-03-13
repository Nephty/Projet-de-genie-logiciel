package front.controllers;

import app.Main;
import back.user.Request;
import front.animation.FadeInTransition;
import front.animation.threads.FadeOutThread;
import front.navigation.Flow;
import front.navigation.navigators.BackButtonNavigator;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.util.Duration;

import java.util.Calendar;

public class ManageTransferPermissionRequestsSceneController extends Controller implements BackButtonNavigator {
    @FXML
    public Button backButton, denyButton, approveButton, fetchRequestsButton;
    @FXML
    public ListView<Request> requestsListView;
    @FXML
    public Label lastUpdateTimeLabel, loadingNotificationsLabel;

    public void initialize() {
        fetchRequests();
    }

    @Override
    public void handleBackButtonNavigation(MouseEvent event) {
        Main.setScene(Flow.back());
    }

    @Override
    public void emulateBackButtonClicked() {
        handleBackButtonClicked(null);
    }

    @FXML
    public void handleComponentKeyReleased(KeyEvent keyEvent) {
        if (keyEvent.getCode() == KeyCode.ESCAPE) {
            emulateBackButtonClicked();
        }
    }

    @FXML
    public void handleBackButtonClicked(MouseEvent event) {
        handleBackButtonNavigation(event);
    }

    @FXML
    public void handleDenyButtonClicked(MouseEvent event) {
        if (requestsListView.getSelectionModel().getSelectedItems().size() > 0) {
            // TODO : back-end : implement "denied" attribute of request, change it accordingly for all selected requests and commit changes to database
        }
    }

    @FXML
    public void handleApproveButtonClicked(MouseEvent event) {
        if (requestsListView.getSelectionModel().getSelectedItems().size() > 0) {
            // TODO : back-end : implement "approved" attribute of request, change it accordingly for all selected requests and commit changes to database
        }
    }

    @FXML
    public void handleFetchRequestsButtonClicked(MouseEvent event) {
        fetchRequests();
    }

    public void fetchRequests() {
        if (loadingNotificationsLabel.getOpacity() == 0.0) {
            int fadeInDuration = 1000;
            int fadeOutDuration = fadeInDuration;
            int sleepDuration = 1000;
            FadeOutThread sleepAndFadeOutLoadingRequestsLabelFadeThread;
            FadeInTransition.playFromStartOn(loadingNotificationsLabel, Duration.millis(fadeInDuration));
            sleepAndFadeOutLoadingRequestsLabelFadeThread = new FadeOutThread();
            Calendar c = Calendar.getInstance();
            lastUpdateTimeLabel.setText("Last update : " + formatCurrentTime(c));
            // TODO : back-end : fetch transfer permission requests from the database and put them in the listview
            sleepAndFadeOutLoadingRequestsLabelFadeThread.start(fadeOutDuration, sleepDuration + fadeInDuration, loadingNotificationsLabel);
        }
    }
}
