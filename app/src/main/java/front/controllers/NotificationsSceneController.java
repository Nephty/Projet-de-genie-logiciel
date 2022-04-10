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
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.geometry.Point3D;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.*;
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

    private ArrayList<Notification> content = new ArrayList<>();
    private ArrayList<String> flaggedNotificationsContent = new ArrayList<>();

    private MouseEvent fakeMouseEventFlagButtonCLicked = new MouseEvent(MouseEvent.MOUSE_CLICKED, 19.0, 18.0, 1075.0+19.0, 200.0+18.0, MouseButton.PRIMARY, 1, false, false, false, false, true,  false, false, false, false, false, new PickResult(flagButton, new Point3D(46.0, 11.0, 2.2737367544323206E-13), 1877.2215562071458));

    private boolean alreadyUpdatedBackground = false;

    public void initialize() {
        IDColumn.setCellValueFactory(new PropertyValueFactory<>("ID"));
        dateColumn.setCellValueFactory(new PropertyValueFactory<>("date"));
        senderColumn.setCellValueFactory(new PropertyValueFactory<>("senderName"));
        contentColumn.setCellValueFactory(new PropertyValueFactory<>("content"));
        notificationsTableView.setPlaceholder(new Label("No notifications."));
        notificationsTableView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);

        for (Notification n : content) {
            if (n.isFlagged()) flaggedNotificationsContent.add(n.getContent());
        }

        // TODO : 10-04-2022 22:12:47 i gave up - update all notifications background
        fetchNotifications();
    }

    private void emulateFlagButtonClicked() {
        handleFlagButtonClicked(null);
    }

    @FXML
    void handleBackButtonClicked(MouseEvent event) {
        handleBackButtonNavigation(event);
        alreadyUpdatedBackground = false;
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
            for (Notification n : notificationsTableView.getSelectionModel().getSelectedItems())
                n.dismiss();
        }
    }

    @FXML
    void handleFlagButtonClicked(MouseEvent event) {
        System.out.println("event = " + event.getPickResult());
        if (notificationsTableView.getSelectionModel().getSelectedItems().size() > 0) {
            ObservableList<Notification> selection = notificationsTableView.getSelectionModel().getSelectedItems();
            for (Notification n : selection) {
                n.changeFlag();
                if (n.isFlagged()) flaggedNotificationsContent.add(n.getContent());
                else flaggedNotificationsContent.remove(n.getContent());
                updateBackgroundColorsAccordingToFlagging(contentColumn);
            }
        }
    }

    private void updateBackgroundColorsAccordingToFlagging(TableColumn<Notification, String> tableColumn) {
        tableColumn.setCellFactory(tv -> new TableCell<>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setGraphic(null);
                } else {
                    setText(item);
                }
                TableRow<Notification> currentRow = getTableRow();
                if (!isEmpty()) {
                    if (flaggedNotificationsContent.contains(item)) {
                        currentRow.setStyle("-fx-background-color:lightcoral");
                    } else {
                        currentRow.setStyle("");
                    }
                }
            }
        });
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
            String toParse = body.substring(1, body.length() - 1);
            ArrayList<String> notificationList = Portfolio.JSONArrayParser(toParse);
            if (!notificationList.get(0).equals("")) {
                for (int i = 0; i < notificationList.size(); i++) {
                    JSONObject obj = new JSONObject(notificationList.get(i));
                    if (obj.getInt("notificationType") == 4) {
                        content.add(new Notification(obj.getString("senderName"), obj.getString("comments"), obj.getString("date"), obj.getLong("notificationId"), obj.getBoolean("isFlagged")));
                    }
                }
            }
            notificationsTableView.setItems(FXCollections.observableArrayList(content));

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
