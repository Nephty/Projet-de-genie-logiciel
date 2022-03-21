package front.controllers;

import app.Main;
import back.user.Account;
import back.user.Profile;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import front.animation.FadeInTransition;
import front.animation.threads.FadeOutThread;
import front.navigation.Flow;
import front.navigation.navigators.BackButtonNavigator;
import front.scenes.Scenes;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.util.Duration;

import java.util.ArrayList;
import java.util.Calendar;

public class ClientDetailsSceneController extends Controller implements BackButtonNavigator {
    private static Profile currentProfile;
    @FXML
    public Button backButton, fetchClientDetailsButton, exportDataButton, createAccountButton, closeAccountButton, searchButton, removeClientButton, cancelRemovalButton, confirmRemovalButton;
    @FXML
    public ListView<Account> clientDetailsListView;
    @FXML
    public Label loadingClientDetailsLabel, lastUpdateTimeLabel;
    @FXML
    public ComboBox<String> sortByComboBox;
    @FXML
    public TextField searchTextField;
    private boolean searched = false;

    public static void setCurrentProfile(Profile profile) {
        currentProfile = profile;
    }

    public void initialize() {
        fetchClientDetails();
    }

    @Override
    public void handleBackButtonNavigation(MouseEvent event) {
        Main.setScene(Flow.back());
    }

    @Override
    public void emulateBackButtonClicked() {
        handleBackButtonClicked(null);
    }

    @FXML
    public void handleComponentKeyReleased(KeyEvent keyEvent) {
        if (keyEvent.getCode() == KeyCode.ESCAPE) {
            emulateBackButtonClicked();
            keyEvent.consume();
        }
    }

    @FXML
    public void handleBackButtonClicked(MouseEvent event) {
        handleBackButtonNavigation(event);
        if (searched) searchTextField.setText("");
        sortByComboBox.valueProperty().set(null);
    }

    @FXML
    public void handleFetchClientDetailsButtonClicked(MouseEvent event) {
        fetchClientDetails();
    }

    @FXML
    public void handleExportDataButtonClicked(MouseEvent event) {
        Main.setScene(Flow.forward(Scenes.ExportDataScene));
        if (clientDetailsListView.getSelectionModel().getSelectedItems().size() != 0) {
            // If items selected, only send selected items for export
            ExportDataSceneController.setExportData(new ArrayList<>(clientDetailsListView.getSelectionModel().getSelectedItems()));
        } else {
            // Send all items for export
            ExportDataSceneController.setExportData(new ArrayList<>(clientDetailsListView.getItems()));
        }
    }

    @FXML
    public void handleCreateAccountButtonClicked(MouseEvent event) {
        Main.setScene(Flow.forward(Scenes.CreateClientAccountScene));
        // TODO : bring user data to the scene so we can create the account for the right user
    }

    @FXML
    public void handleCloseAccountButtonClicked(MouseEvent event) {
        // TODO : API fonctionne pas
        if (clientDetailsListView.getSelectionModel().getSelectedItems().size() == 1) {
            Unirest.setTimeouts(0, 0);
            HttpResponse<String> response = null;
            try {
                response = Unirest.delete("https://flns-spring-test.herokuapp.com/api/account/" + clientDetailsListView.getSelectionModel().getSelectedItems().get(0).getIBAN())
                        .header("Authorization", "Bearer " + Main.getToken())
                        .asString();
            } catch (UnirestException e) {
                e.printStackTrace();
            }
        }
    }

    @FXML
    public void handleSearchTextFieldKeyPressed(KeyEvent keyEvent) {
        if (keyEvent.getCode() == KeyCode.ENTER) {
            emulateSearchButtonClicked();
        }
        searched = false;
    }

    private void emulateSearchButtonClicked() {
        handleSearchButtonClicked(null);
    }

    @FXML
    public void handleSearchButtonClicked(MouseEvent event) {
        searched = true;
    }

    @FXML
    public void handleRemoveClientButtonClicked(MouseEvent event) {
        confirmRemovalButton.setVisible(!confirmRemovalButton.isVisible());
        cancelRemovalButton.setVisible(!cancelRemovalButton.isVisible());
    }

    @FXML
    public void handleCancelRemovalButtonClicked(MouseEvent event) {
        confirmRemovalButton.setVisible(false);
        cancelRemovalButton.setVisible(false);
    }

    @FXML
    public void handleConfirmRemovalButtonClicked(MouseEvent event) {
        confirmRemovalButton.setVisible(false);
        cancelRemovalButton.setVisible(false);
        // TODO : back-end : remove client from bank
    }

    public void fetchClientDetails() {
        if (loadingClientDetailsLabel.getOpacity() == 0.0) {
            int fadeInDuration = 1000;
            int fadeOutDuration = fadeInDuration;
            int sleepDuration = 1000;
            FadeOutThread sleepAndFadeOutLoadingClientDetailsLabelFadeThread;
            FadeInTransition.playFromStartOn(loadingClientDetailsLabel, Duration.millis(fadeInDuration));
            sleepAndFadeOutLoadingClientDetailsLabelFadeThread = new FadeOutThread();
            Calendar c = Calendar.getInstance();
            lastUpdateTimeLabel.setText("Last update : " + formatCurrentTime(c));
            ArrayList<Account> accountList = Main.getCurrentWallet().getAccountList();
            clientDetailsListView.setItems(FXCollections.observableArrayList(accountList));
            sleepAndFadeOutLoadingClientDetailsLabelFadeThread.start(fadeOutDuration, sleepDuration + fadeInDuration, loadingClientDetailsLabel);
        }
    }
}
