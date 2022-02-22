package front.controllers;

import front.Main;
import front.navigation.Flow;
import front.navigation.navigators.BackButtonNavigator;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.input.MouseEvent;

public class NotificationsSceneController implements BackButtonNavigator {
    @FXML
    public Button backButton;
    // TODO : back-end : implement notifications to make this a ListView<Notification>
    @FXML
    public ListView notificationsListView;

    public void handleBackButtonClicked(MouseEvent event) {
        handleBackButtonNavigation(event);
    }

    @Override
    public void handleBackButtonNavigation(MouseEvent event) {
        Main.setScene(Flow.back());
    }
}
