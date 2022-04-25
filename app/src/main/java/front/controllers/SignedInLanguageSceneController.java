package front.controllers;

import app.Main;
import back.user.ErrorHandler;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import front.navigation.Flow;
import front.navigation.navigators.BackButtonNavigator;
import front.scenes.SceneLoader;
import front.scenes.Scenes;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;

import java.util.Locale;

/**
 * @author Arnaud MOREAU
 */
public class SignedInLanguageSceneController extends Controller implements BackButtonNavigator {
    @FXML
    TableView<Locale> languagesTableView;
    @FXML
    TableColumn<Locale, String> displayNameColumn, languageColumn, countryColumn;
    @FXML
    Button backButton, setButton;
    @FXML
    Label chooseLanguageLabel;

    public void initialize() {
        countryColumn.setCellValueFactory(new PropertyValueFactory<>("displayCountry"));
        languageColumn.setCellValueFactory(new PropertyValueFactory<>("displayLanguage"));
        displayNameColumn.setCellValueFactory(new PropertyValueFactory<>("displayName"));
        languagesTableView.setPlaceholder(new Label("No language available. Make sure your installation contains all the required language files. If you see this message, contact administrators ASAP."));
        languagesTableView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        languagesTableView.setItems(FXCollections.observableArrayList(Main.FR_BE_Locale, Main.EN_US_Locale, Main.NL_NL_Locale, Main.PT_PT_Locale, Main.LT_LT_Locale, Main.RU_RU_Locale, Main.DE_DE_Locale, Main.PL_PL_Locale));
    }

    @FXML
    void handleBackButtonClicked(MouseEvent mouseEvent) {
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
    void handleSetButtonMouseClicked(MouseEvent event) {
        if (languagesTableView.getSelectionModel().getSelectedItems().size() == 1) {
            String language = languagesTableView.getSelectionModel().getSelectedItems().get(0).getDisplayName();
            Unirest.setTimeouts(0, 0);
            HttpResponse<String> response = ErrorHandler.handlePossibleError(() -> {
                HttpResponse<String> rep = null;
                try {
                    rep = Unirest.put("https://flns-spring-test.herokuapp.com/api/user")
                            .header("Authorization", "Bearer "+Main.getToken())
                            .header("Content-Type", "application/json")
                            .body("{\r\n        \"language\": \""+language+"\"\r\n}")
                            .asString();
                } catch (UnirestException e) {
                    throw new RuntimeException(e);
                }
                return rep;
            });
        }
    }

    @FXML
    void handleComponentKeyReleased(KeyEvent event) {
        if (event.getCode() == KeyCode.ESCAPE) {
            emulateBackButtonClicked();
            event.consume();
        }
    }
}
