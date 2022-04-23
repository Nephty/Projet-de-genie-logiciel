package front.controllers;

import app.Main;
import back.user.Notification;
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

import java.util.ArrayList;
import java.util.Calendar;

/**
 * @author Arnaud MOREAU
 */
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

    ObservableList<Notification> data = FXCollections.observableArrayList();

    private ArrayList<Notification> content = new ArrayList<>();
    private ArrayList<String> flaggedNotificationsContent = new ArrayList<>();

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
            for (Notification n : notificationsTableView.getSelectionModel().getSelectedItems()) {
                n.dismiss();
                data.remove(n);
            }
            notificationsTableView.setItems(data);
        }
    }

    @FXML
    void handleFlagButtonClicked(MouseEvent event) {
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
        // Execute this only if the label is not visible (that is, only if we are not already retrieving data etc.)
        if (loadingNotificationsLabel.getOpacity() == 0.0) {
            int fadeInDuration = 1000;
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
            ArrayList<Notification> content = Notification.fetchCustomNotification();
            data = FXCollections.observableArrayList(content);
            notificationsTableView.setItems(data);

            // Fade the label "updating notifications..." out to 0.0 opacity
            sleepAndFadeOutLoadingNotificationsLabelFadeThread.start(fadeInDuration, sleepDuration + fadeInDuration, loadingNotificationsLabel);
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
