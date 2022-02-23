package front.controllers;

import BenkyngApp.Main;
import front.navigation.Flow;
import front.navigation.navigators.BackButtonNavigator;
import front.scenes.Scenes;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.input.MouseEvent;

public class RequestsSceneController implements BackButtonNavigator {

    @FXML
    public Button backButton, transferPermissionButton, newPortfolioButton, requestsStatusButton;

    @Override
    public void handleBackButtonNavigation(MouseEvent event) {
        Main.setScene(Flow.back());
    }

    @FXML
    public void handleBackButtonClicked(MouseEvent event) {
        handleBackButtonNavigation(event);
    }

    @FXML
    public void handleTransferPermissionButtonClicked(MouseEvent event) {
        // TODO : navigate to the transfer permission scene
    }

    @FXML
    public void handleNewPortfolioButtonClicked(MouseEvent event) {
        // TODO : navigate to the new portfolio scene
    }

    @FXML
    public void handleRequestsStatusButtonClicked(MouseEvent event) {
        Main.setScene(Flow.forward(Scenes.RequestsStatusScene));
    }
}
