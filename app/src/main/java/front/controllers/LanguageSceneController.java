package front.controllers;

import app.Main;
import front.navigation.Flow;
import front.navigation.navigators.BackButtonNavigator;
import front.scenes.SceneLoader;
import front.scenes.Scenes;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;

import java.util.Locale;

public class LanguageSceneController extends Controller implements BackButtonNavigator {
    @FXML
    public ListView<Locale> languagesListView;
    @FXML
    Button backButton, addButton, setButton;
    @FXML
    Label chooseLanguageLabel;

    public void initialize() {
        languagesListView.setItems(FXCollections.observableArrayList(Main.FR_BE_Locale, Main.EN_US_Locale, Main.NL_NL_Locale, Main.PT_PT_Locale, Main.LT_LT_Locale));
    }

    @FXML
    public void handleBackButtonClicked(MouseEvent mouseEvent) {
        handleBackButtonNavigation(mouseEvent);
    }

    @Override
    public void handleBackButtonNavigation(MouseEvent event) {
        System.out.println(Flow.tail());
        Main.setScene(Flow.back());
        System.out.println(Flow.getContentAsString());
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
        if (languagesListView.getSelectionModel().getSelectedItems().size() == 1) {
            Main.appLocale = languagesListView.getSelectionModel().getSelectedItems().get(0);
            // Reload scenes
            Scenes.AuthScene = SceneLoader.load("AuthScene.fxml", Main.appLocale);
            Scenes.SignInScene = SceneLoader.load("SignInScene.fxml", Main.appLocale);
            Scenes.LanguageScene = SceneLoader.load("LanguageScene.fxml", Main.appLocale);
            Scenes.SignUpScene = SceneLoader.load("SignUpScene.fxml", Main.appLocale);
            Scenes.MainScreenScene = SceneLoader.load("MainScreenScene.fxml", Main.appLocale);
            Scenes.ChangePasswordScene = SceneLoader.load("ChangePasswordScene.fxml", Main.appLocale);
            // Replace the before last scene in the flow
            Flow.replaceBeforeLastElement(Scenes.AuthScene);
        }
    }

    @FXML
    public void handleButtonKeyReleased(KeyEvent event) {
        if (event.getCode() == KeyCode.ESCAPE) {
            emulateBackButtonClicked();
            event.consume();
        }
    }
}
