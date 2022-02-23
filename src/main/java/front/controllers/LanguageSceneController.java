package front.controllers;

import BenkyngApp.Main;
import front.navigation.Flow;
import front.navigation.navigators.BackButtonNavigator;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;

public class LanguageSceneController extends Controller implements BackButtonNavigator {

    @FXML
    Button backButton, addButton, setButton;
    @FXML
    Label chooseLanguageLabel;

    @FXML
    public void handleBackButtonClicked(MouseEvent mouseEvent) {
        handleBackButtonNavigation(mouseEvent);
    }

    @Override
    public void handleBackButtonNavigation(MouseEvent event) {
        Main.setScene(Flow.back());
    }

    @FXML
    public void handleAddButtonClicked(MouseEvent mouseEvent) {
    }
}
