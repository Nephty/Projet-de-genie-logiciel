package front.controllers;

import app.Main;
import back.user.Bank;
import back.user.CommunicationType;
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
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.util.Duration;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;

public class ManagePortfolioRequestsSceneController extends Controller implements BackButtonNavigator {
    @FXML
    public Button backButton, denyButton, approveButton, fetchRequestsButton;
    @FXML
    public ListView<Request> requestsListView;
    @FXML
    public Label lastUpdateTimeLabel, loadingRequestsLabel, noRequestSelectedLabel;

    public void initialize() {
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
    public void handleComponentKeyReleased(KeyEvent keyEvent) {
        if (keyEvent.getCode() == KeyCode.ESCAPE) {
            emulateBackButtonClicked();
            keyEvent.consume();
        }
    }

    @FXML
    public void handleBackButtonClicked(MouseEvent event) {
        handleBackButtonNavigation(event);
    }

    @FXML
    public void handleDenyButtonClicked(MouseEvent event) {
        if (requestsListView.getSelectionModel().getSelectedItems().size() > 0) {
            if (noRequestSelectedLabel.isVisible()) noRequestSelectedLabel.setVisible(false);
            // TODO : back-end : implement "denied" attribute of request, change it accordingly for all selected requests and commit changes to database
        } else if (!noRequestSelectedLabel.isVisible()) noRequestSelectedLabel.setVisible(true);
    }

    @FXML
    public void handleApproveButtonClicked(MouseEvent event) {
        if (requestsListView.getSelectionModel().getSelectedItems().size() > 0) {
            if (noRequestSelectedLabel.isVisible()) noRequestSelectedLabel.setVisible(false);
            // TODO : back-end : implement "approved" attribute of request, change it accordingly for all selected requests and commit changes to database
        } else if (!noRequestSelectedLabel.isVisible()) noRequestSelectedLabel.setVisible(true);
    }

    @FXML
    public void handleFetchRequestsButtonClicked(MouseEvent event) {
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
            // TODO : back-end : fetch portfolio requests from the database and put them in the listview

            Unirest.setTimeouts(0, 0);
            HttpResponse<String> response = null;
            try {
                response = Unirest.get("https://flns-spring-test.herokuapp.com/api/notification")
                        .header("Authorization", "Bearer " + Main.getToken())
                        .asString();
            } catch (UnirestException e) {
                e.printStackTrace();
            }

            String body = response.getBody();
            String toParse = body.substring(1,body.length() - 1);
            ArrayList<String> requestList = Bank.JSONArrayParser(toParse);
            ArrayList<Request> reqList = new ArrayList<Request>();
            if(!requestList.get(0).equals("")){
                for(int i = 0; i<requestList.size(); i++){
                    JSONObject obj = new JSONObject(requestList.get(i));
                    if(obj.getInt("notificationType") == 0){
                        CommunicationType comType = CommunicationType.CUSTOM;
                        int notifType = obj.getInt("notificationType");
                        switch(notifType){
                            case(0): comType = CommunicationType.CREATE_ACCOUNT; break;
                            case(1): comType = CommunicationType.CREATE_SUB_ACCOUNT; break;
                            case(2): comType = CommunicationType.TRANSFER_PERMISSION; break;
                            case(3): comType = CommunicationType.NEW_WALLET; break;
                        }
                        reqList.add(new Request(obj.getString("senderName"), comType, obj.getString("date"),"",obj.getString("senderId"),obj.getLong("notificationId")));
                    }
                }
            }
            requestsListView.setItems(FXCollections.observableArrayList(reqList));

            sleepAndFadeOutLoadingRequestsLabelFadeThread.start(fadeOutDuration, sleepDuration + fadeInDuration, loadingRequestsLabel);
        }
    }
}
