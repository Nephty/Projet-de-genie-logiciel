package front.controllers;

import app.Main;
import back.user.Notification;
import back.user.Portfolio;
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

public class NotificationsSceneController extends Controller implements BackButtonNavigator {
    @FXML
    Button backButton, dismissButton, flagButton, fetchNotificationsButton;
    @FXML
    Label loadingNotificationsLabel;
    @FXML
    Label lastUpdateTimeLabel;
    @FXML
    TableView<Notification> notificationsTableView;
    @FXML
    TableColumn<Notification, String> dateColumn, senderColumn, contentColumn;
    @FXML
    TableColumn<Notification, Long> IDColumn;

    public void initialize() {
        IDColumn.setCellValueFactory(new PropertyValueFactory<>("ID"));
        dateColumn.setCellValueFactory(new PropertyValueFactory<>("date"));
        senderColumn.setCellValueFactory(new PropertyValueFactory<>("senderName"));
        contentColumn.setCellValueFactory(new PropertyValueFactory<>("content"));
        notificationsTableView.setPlaceholder(new Label("No notifications."));
        notificationsTableView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        fetchNotifications();
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
    void handleDismissButtonClicked(MouseEvent event) {
        if (notificationsTableView.getSelectionModel().getSelectedItems().size() > 0) {
            notificationsTableView.getSelectionModel().getSelectedItems().get(0).dismiss();
        }
    }

    @FXML
    void handleFlagButtonClicked(MouseEvent event) {
        if (notificationsTableView.getSelectionModel().getSelectedItems().size() > 0) {
            notificationsTableView.getSelectionModel().getSelectedItems().get(0).changeFlag();
            fetchNotifications();
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
            ArrayList<String> notificationList = Portfolio.JSONArrayParser(toParse);
            ArrayList<Notification> notifList = new ArrayList<Notification>();
            if(!notificationList.get(0).equals("")){
                for(int i = 0; i<notificationList.size(); i++){
                    JSONObject obj = new JSONObject(notificationList.get(i));
                    if(obj.getInt("notificationType") == 4){
                        notifList.add(new Notification(obj.getString("senderName"), obj.getString("comments"),obj.getString("date"), obj.getLong("notificationId"), obj.getBoolean("isFlagged")));
                    }
                }
            }
            notificationsTableView.setItems(FXCollections.observableArrayList(notifList));

            // Fade the label "updating notifications..." out to 0.0 opacity
            sleepAndFadeOutLoadingNotificationsLabelFadeThread.start(fadeOutDuration, sleepDuration + fadeInDuration, loadingNotificationsLabel);
        }
    }

    @FXML
    void handleFetchNotificationsButtonClicked(MouseEvent event) {
        fetchNotifications();
    }

    @FXML
    void handleComponentKeyReleased(KeyEvent event) {
        if (event.getCode() == KeyCode.ESCAPE) {
            emulateBackButtonClicked();
            event.consume();
        }
    }
}
