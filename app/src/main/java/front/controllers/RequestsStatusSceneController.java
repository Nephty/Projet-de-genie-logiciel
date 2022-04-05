package front.controllers;

import app.Main;
import back.user.CommunicationType;
import back.user.Notification;
import back.user.Portfolio;
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

public class RequestsStatusSceneController extends Controller implements BackButtonNavigator {
    @FXML
    public Button backButton, fetchRequestsButton;
    @FXML
    public ListView<Request> requestsListView;
    @FXML
    public Label lastUpdateTimeLabel, loadingRequestsLabel;

    public void initialize() {
        fetchRequests();
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
            ArrayList<String> requestList = Portfolio.JSONArrayParser(toParse);
            ArrayList<Request> reqList = new ArrayList<Request>();
            if(!requestList.get(0).equals("")){
                for(int i = 0; i<requestList.size(); i++){
                    JSONObject obj = new JSONObject(requestList.get(i));
                    if(obj.getInt("notificationType") != 4){
                        CommunicationType comType = CommunicationType.CUSTOM;
                        int notifType = obj.getInt("notificationType");
                        switch(notifType){
                            case(0): comType = CommunicationType.CREATE_ACCOUNT; break;
                            case(1): comType = CommunicationType.CREATE_SUB_ACCOUNT; break;
                            case(2): comType = CommunicationType.TRANSFER_PERMISSION; break;
                            case(3): comType = CommunicationType.NEW_WALLET; break;
                        }
                        reqList.add(new Request(obj.getString("recipientId"), comType, obj.getString("date")));
                    }
                }
            }
            requestsListView.setItems(FXCollections.observableArrayList(reqList));

            // Fade the label "updating requests..." out to 0.0 opacity
            sleepAndFadeOutLoadingRequestsLabelFadeThread.start(fadeOutDuration, sleepDuration + fadeInDuration, loadingRequestsLabel);
        }
    }

    @FXML
    public void handleComponentKeyReleased(KeyEvent event) {
        if (event.getCode() == KeyCode.ESCAPE) {
            emulateBackButtonClicked();
            event.consume();
        }
    }
}
