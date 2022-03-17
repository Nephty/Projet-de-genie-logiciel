package front.controllers;

import app.Main;
import back.user.Language;
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

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

public class LanguageSceneController extends Controller implements BackButtonNavigator {
    @FXML
    public ListView<Language> languagesListView;
    @FXML
    Button backButton, addButton, setButton;
    @FXML
    Label chooseLanguageLabel;

    public void initialize() {
        String path = System.getProperty("user.dir") + "/src/main/resources/lang";
        File dir  = new File(path);
        File[] liste = dir.listFiles();
        ArrayList<Language> lst = new ArrayList<Language>();
        HashMap<String, Language> dico = new HashMap<String, Language>();

        for(File item : liste) {
            String str = item.getName();
            str.substring(0, str.length() -4);
            str.replace('_',' ');
            str.toUpperCase();
            lst.add(new Language(str));
        }
        languagesListView.setItems(FXCollections.observableArrayList(lst));
        // TODO : back-end : fetch languages
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
