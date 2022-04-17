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

public class ManagePortfolioRequestsSceneController extends Controller implements BackButtonNavigator {
    @FXML
    Button backButton, denyButton, approveButton, fetchRequestsButton;
    @FXML
    TableView<Request> requestsTableView;
    @FXML
    Label lastUpdateTimeLabel, loadingRequestsLabel, noRequestSelectedLabel;
    @FXML
    TableColumn<Request, String> IDColumn, senderIDColumn, dateColumn;

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
            for(int i = 0; i<requestsTableView.getSelectionModel().getSelectedItems().size(); i++){
                requestsTableView.getSelectionModel().getSelectedItems().get(i).deny();
            }
        } else if (!noRequestSelectedLabel.isVisible()) noRequestSelectedLabel.setVisible(true);
    }

    @FXML
    void handleApproveButtonClicked(MouseEvent event) {
        if (requestsTableView.getSelectionModel().getSelectedItems().size() > 0) {
            if (noRequestSelectedLabel.isVisible()) noRequestSelectedLabel.setVisible(false);
            for(int i = 0; i<requestsTableView.getSelectionModel().getSelectedItems().size(); i++){
                requestsTableView.getSelectionModel().getSelectedItems().get(i).approve();
            }
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
            Unirest.setTimeouts(0, 0);
            HttpResponse<String> response = ErrorHandler.handlePossibleError(() -> {
                HttpResponse<String> rep = null;
                try {
                    rep = Unirest.get("https://flns-spring-test.herokuapp.com/api/notification")
                            .header("Authorization", "Bearer " + Main.getToken())
                            .asString();
                } catch (UnirestException e) {
                    throw new RuntimeException(e);
                }
                return rep;
            });

            String body = response.getBody();
            String toParse = body.substring(1, body.length() - 1);
            ArrayList<String> requestList = Bank.JSONArrayParser(toParse);
            ArrayList<Request> reqList = new ArrayList<Request>();
            if (!requestList.get(0).equals("")) {
                for (int i = 0; i < requestList.size(); i++) {
                    JSONObject obj = new JSONObject(requestList.get(i));
                    if (obj.getInt("notificationType") == 0) {
                        CommunicationType comType = CommunicationType.CUSTOM;
                        int notifType = obj.getInt("notificationType");
                        switch (notifType) {
                            case (0):
                                comType = CommunicationType.CREATE_ACCOUNT;
                                break;
                            case (1):
                                comType = CommunicationType.CREATE_SUB_ACCOUNT;
                                break;
                            case (2):
                                comType = CommunicationType.TRANSFER_PERMISSION;
                                break;
                            case (3):
                                comType = CommunicationType.NEW_WALLET;
                                break;
                        }
                        reqList.add(new Request(obj.getString("senderName"), comType, obj.getString("date"), "", obj.getString("senderId"), obj.getLong("notificationId")));
                    }
                }
            }
            requestsTableView.setItems(FXCollections.observableArrayList(reqList));

            sleepAndFadeOutLoadingRequestsLabelFadeThread.start(fadeOutDuration, sleepDuration + fadeInDuration, loadingRequestsLabel);
        }
    }
}
