package front.controllers;

import BenkyngApp.Main;
import back.user.Request;
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

public class RequestsStatusSceneController extends Controller implements BackButtonNavigator {
    @FXML
    public Button backButton, fetchRequestsButton;
    @FXML
    public ListView<Request> requestsListView;
    @FXML
    public Label lastUpdateTimeLabel, loadingRequestsLabel;

    public void initialize() {
        fetchRequests();
//        requestsListView.setItems(FXCollections.observableArrayList(new Request("request A"), new Request("request B")));
    }

    @FXML
    public void handleBackButtonClicked(MouseEvent event) {
        handleBackButtonNavigation(event);
    }

    @Override
    public void handleBackButtonNavigation(MouseEvent event) {
        Main.setScene(Flow.back());
    }

    @FXML
    public void handleFetchRequestsButtonClicked(MouseEvent event) {
        fetchRequests();
    }

    /**
     * Fetches requests from the database to display them
     */
    public void fetchRequests() {
        // Execute this only if the label is not visible (that is, only if we are not already retrieving data etc)
        if (loadingRequestsLabel.getOpacity() == 0.0) {
            int fadeInDuration = 1000;
            int fadeOutDuration = fadeInDuration;
            int sleepDuration = 1000;
            FadeOutThread sleepAndFadeOutLoadingRequestsLabelFadeThread;
            // Fade the label "updating requests..." in to 1.0 opacity
            FadeInTransition.playFromStartOn(loadingRequestsLabel, Duration.millis(fadeInDuration));
            // We use a new Thread, so we can sleep the method for a few hundreds of milliseconds so that the label
            // doesn't instantly go away when the requests are retrieved.
            sleepAndFadeOutLoadingRequestsLabelFadeThread = new FadeOutThread();
            // Save actual time and date
            Calendar c = Calendar.getInstance();
            // Update lastUpdateLabel with the new time and date
            lastUpdateTimeLabel.setText("Last update : " + formatCurrentTime(c));
            // Fetch requests and put them in the listview
            // TODO : back-end : fetch requests from the database and put them in the listview
            // Fade the label "updating requests..." out to 0.0 opacity
            sleepAndFadeOutLoadingRequestsLabelFadeThread.start(fadeOutDuration, sleepDuration + fadeInDuration, loadingRequestsLabel);
        }
    }
}
