package front.controllers;

import app.Main;
import back.user.CommunicationType;
import back.user.ErrorHandler;
import back.user.Portfolio;
import back.user.Request;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import front.animation.FadeInTransition;
import front.animation.threads.FadeOutThread;
import front.navigation.Flow;
import front.navigation.navigators.BackButtonNavigator;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.util.Duration;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;

/**
 * @author Arnaud MOREAU
 */
public class RequestsStatusSceneController extends Controller implements BackButtonNavigator {
    @FXML
    Button backButton, fetchRequestsButton;
    @FXML
    TableView<Request> requestsTableView;
    @FXML
    TableColumn<Request, String> dateColumn, typeColumn, contentColumn, statusColumn;
    @FXML
    Label lastUpdateTimeLabel, loadingRequestsLabel;

    public void initialize() {
        dateColumn.setCellValueFactory(new PropertyValueFactory<>("date"));
        typeColumn.setCellValueFactory(new PropertyValueFactory<>("communicationType"));
        contentColumn.setCellValueFactory(new PropertyValueFactory<>("content"));
        statusColumn.setCellValueFactory(a -> new SimpleStringProperty("Waiting..."));
        requestsTableView.setPlaceholder(new Label("No request."));
        requestsTableView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        fetchRequests();
    }

    @FXML
    void handleBackButtonClicked(MouseEvent event) {
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
    void handleFetchRequestsButtonClicked(MouseEvent event) {
        fetchRequests();
    }

    /**
     * Fetches requests from the database to display them
     */
    public void fetchRequests() {
        // Execute this only if the label is not visible (that is, only if we are not already retrieving data etc.)
        if (loadingRequestsLabel.getOpacity() == 0.0) {
            int fadeInDuration = 1000;
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

            // Fetch the requests
            ArrayList<Request> reqList = Request.fetchRequests();

            requestsTableView.setItems(FXCollections.observableArrayList(reqList));

            // Fade the label "updating requests..." out to 0.0 opacity
            sleepAndFadeOutLoadingRequestsLabelFadeThread.start(fadeInDuration, sleepDuration + fadeInDuration, loadingRequestsLabel);
        }
    }

    @FXML
    void handleComponentKeyReleased(KeyEvent event) {
        if (event.getCode() == KeyCode.ESCAPE) {
            emulateBackButtonClicked();
            event.consume();
        }
    }
}
