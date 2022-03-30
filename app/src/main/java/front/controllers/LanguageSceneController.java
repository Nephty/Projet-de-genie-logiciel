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
        /*
        String path = System.getProperty("user.dir") + "/src/main/resources/lang";
        File dir = new File(path);
        File[] files = dir.listFiles();
        assert files != null;  // this asserts that files is not null because we know there will be data in the lang directory
        ArrayList<Language> lst = new ArrayList<Language>();
        HashMap<String, Language> stringToLanguageMap = new HashMap<String, Language>();

        for (File item : files) {
            String str = item.getName();
            // remove the .json at the end of the string, replace the _ with a space and put the result to upper case
            // en_us.json --> EN US                     fr_be.json --> FR BE
            str = str.substring(0, str.length() - 4).replace('_', ' ').toUpperCase();
            lst.add(new Language(str));
        }
        languagesListView.setItems(FXCollections.observableArrayList(lst));
         */
        languagesListView.setItems(FXCollections.observableArrayList(Main.FR_BE_Locale, Main.EN_US_Locale, Main.NL_NL_Locale, Main.PT_PT_Locale, Main.LT_LT_Locale, Main.RU_RU_Locale, Main.DE_DE_Locale, Main.PL_PL_Locale));
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
    public void handleComponentKeyReleased(KeyEvent event) {
        if (event.getCode() == KeyCode.ESCAPE) {
            emulateBackButtonClicked();
            event.consume();
        }
    }
}
