package front.controllers;

import app.Main;
import back.user.Bank;
import back.user.CommunicationType;
import back.user.ErrorHandler;
import back.user.Request;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import front.animation.FadeInTransition;
import front.animation.threads.FadeOutThread;
import front.navigation.Flow;
import front.navigation.navigators.BackButtonNavigator;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
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

public class ManageTransferPermissionRequestsSceneController extends Controller implements BackButtonNavigator {
    @FXML
    Button backButton, denyButton, approveButton, fetchRequestsButton;
    @FXML
    TableView<Request> requestsTableView;
    @FXML
    Label lastUpdateTimeLabel, loadingRequestsLabel, noRequestSelectedLabel;
    @FXML
    TableColumn<Request, String> IDColumn, senderIDColumn, dateColumn;
    private ObservableList<Request> data = FXCollections.observableArrayList();

    public void initialize() {
        IDColumn.setCellValueFactory(new PropertyValueFactory<>("ID"));
        senderIDColumn.setCellValueFactory(new PropertyValueFactory<>("senderID"));
        dateColumn.setCellValueFactory(new PropertyValueFactory<>("date"));
        requestsTableView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        fetchRequests();
    }

    @Override
    public void handleBackButtonNavigation(MouseEvent event) {
        Main.setScene(Flow.back());
        if (noRequestSelectedLabel.isVisible()) noRequestSelectedLabel.setVisible(false);
    }

    @Override
    public void emulateBackButtonClicked() {
        handleBackButtonClicked(null);
    }

    @FXML
    void handleComponentKeyReleased(KeyEvent keyEvent) {
        if (keyEvent.getCode() == KeyCode.ESCAPE) {
            emulateBackButtonClicked();
            keyEvent.consume();
        }
    }

    @FXML
    void handleBackButtonClicked(MouseEvent event) {
        handleBackButtonNavigation(event);
    }

    @FXML
    void handleDenyButtonClicked(MouseEvent event) {
        if (requestsTableView.getSelectionModel().getSelectedItems().size() > 0) {
            if (noRequestSelectedLabel.isVisible()) noRequestSelectedLabel.setVisible(false);
            ObservableList<Request> selection = requestsTableView.getSelectionModel().getSelectedItems();
            for (Request request : selection) {
                request.deny();
                data.remove(request);
            }
            requestsTableView.setItems(data);
        } else if (!noRequestSelectedLabel.isVisible()) noRequestSelectedLabel.setVisible(true);
    }

    @FXML
    void handleApproveButtonClicked(MouseEvent event) {
        if (requestsTableView.getSelectionModel().getSelectedItems().size() > 0) {
            if (noRequestSelectedLabel.isVisible()) noRequestSelectedLabel.setVisible(false);
            ObservableList<Request> selection = requestsTableView.getSelectionModel().getSelectedItems();
            for (Request request : selection) {
                request.approve();
                data.remove(request);
            }
            requestsTableView.setItems(data);
        } else if (!noRequestSelectedLabel.isVisible()) noRequestSelectedLabel.setVisible(true);
    }

    @FXML
    void handleFetchRequestsButtonClicked(MouseEvent event) {
        fetchRequests();
    }

    public void fetchRequests() {
        if (loadingRequestsLabel.getOpacity() == 0.0) {
            int fadeInDuration = 1000;
            int fadeOutDuration = fadeInDuration;
            int sleepDuration = 1000;
            FadeOutThread sleepAndFadeOutLoadingRequestsLabelFadeThread;
            FadeInTransition.playFromStartOn(loadingRequestsLabel, Duration.millis(fadeInDuration));
            sleepAndFadeOutLoadingRequestsLabelFadeThread = new FadeOutThread();
            Calendar c = Calendar.getInstance();
            lastUpdateTimeLabel.setText("Last update : " + formatCurrentTime(c));

            // Fetch the list of transfer permission requests
            ArrayList<Request> data = Request.fetchRequests(2);

            requestsTableView.setItems(FXCollections.observableArrayList(data));
            sleepAndFadeOutLoadingRequestsLabelFadeThread.start(fadeOutDuration, sleepDuration + fadeInDuration, loadingRequestsLabel);
        }
    }
}
