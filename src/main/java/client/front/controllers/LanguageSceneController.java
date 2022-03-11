package client.front.controllers;

import client.BenkyngApp.Main;
import client.back.user.Language;
import client.front.navigation.Flow;
import client.front.navigation.navigators.BackButtonNavigator;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;

public class LanguageSceneController extends Controller implements BackButtonNavigator {
    @FXML
    public ListView<Language> languagesListView;
    @FXML
    Button backButton, addButton, setButton;
    @FXML
    Label chooseLanguageLabel;

    public void initialize() {
        languagesListView.setItems(FXCollections.observableArrayList(new Language("language A"), new Language("language B")));
        // TODO : client.back-end : fetch languages
    }

    @FXML
    public void handleBackButtonClicked(MouseEvent mouseEvent) {
        handleBackButtonNavigation(mouseEvent);
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
    public void handleAddButtonMouseClicked(MouseEvent event) {
    }

    @FXML
    public void handleSetButtonMouseClicked(MouseEvent event) {

    }

    @FXML
    public void handleButtonKeyReleased(KeyEvent event) {
        if (event.getCode() == KeyCode.ESCAPE) {
            emulateBackButtonClicked();
            event.consume();
        }
    }
}
