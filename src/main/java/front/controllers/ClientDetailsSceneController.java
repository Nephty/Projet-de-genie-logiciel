package front.controllers;

import app.Main;
import back.user.Account;
import back.user.Profile;
import front.animation.FadeInTransition;
import front.animation.threads.FadeOutThread;
import front.navigation.Flow;
import front.navigation.navigators.BackButtonNavigator;
import front.scenes.Scenes;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.util.Duration;

import java.util.ArrayList;
import java.util.Calendar;

public class ClientDetailsSceneController extends Controller implements BackButtonNavigator {
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

    private static Profile currentProfile;
    private boolean searched = false;

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
        if (loadingClientDetailsLabel.getOpacity() == 0.0) {
            int fadeInDuration = 1000;
            int fadeOutDuration = fadeInDuration;
            int sleepDuration = 1000;
            FadeOutThread sleepAndFadeOutLoadingClientDetailsLabelFadeThread;
            FadeInTransition.playFromStartOn(loadingClientDetailsLabel, Duration.millis(fadeInDuration));
            sleepAndFadeOutLoadingClientDetailsLabelFadeThread = new FadeOutThread();
            Calendar c = Calendar.getInstance();
            lastUpdateTimeLabel.setText("Last update : " + formatCurrentTime(c));
            // TODO : back-end : fetch clients from the database and put them in the listview
            sleepAndFadeOutLoadingClientDetailsLabelFadeThread.start(fadeOutDuration, sleepDuration + fadeInDuration, loadingClientDetailsLabel);
        }
    }

    @FXML
    public void handleExportDataButtonClicked(MouseEvent event) {
        if (clientDetailsListView.getItems().size() > 0) {
            Main.setScene(Scenes.ExportDataScene);
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
        // TODO : back-end : delete the account
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

    public static void setCurrentProfile(Profile profile) {
        currentProfile = profile;
    }
}
