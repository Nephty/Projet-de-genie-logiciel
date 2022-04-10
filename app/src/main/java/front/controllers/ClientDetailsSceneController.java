package front.controllers;

import app.Main;
import back.user.Account;
import back.user.Profile;
import com.mashape.unirest.http.exceptions.UnirestException;
import front.animation.FadeInTransition;
import front.animation.threads.FadeOutThread;
import front.navigation.Flow;
import front.navigation.navigators.BackButtonNavigator;
import front.scenes.Scenes;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.util.Duration;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;

public class ClientDetailsSceneController extends Controller implements BackButtonNavigator {
    private static Profile currentProfile;
    @FXML
    public Button backButton, fetchClientDetailsButton, exportDataButton, createAccountButton, closeAccountButton, searchButton, removeClientButton, cancelRemovalButton, confirmRemovalButton;
    @FXML
    public TableView<Account> clientDetailsTableView;
    @FXML
    public Label loadingClientDetailsLabel, lastUpdateTimeLabel;
    @FXML
    public TextField searchTextField;
    @FXML
    public TableColumn<Account, String> IBANColumn, accountTypeColumn, transferPermissionColumn, statusColumn;
    private boolean searched = false;

    private ObservableList<Account> allData = FXCollections.observableArrayList();
    private ObservableList<Account> currentData = FXCollections.observableArrayList();

    public static void setCurrentProfile(Profile profile) {
        currentProfile = profile;
    }

    public void initialize() {
        IBANColumn.setCellValueFactory(new PropertyValueFactory<>("IBAN"));
        accountTypeColumn.setCellValueFactory(new PropertyValueFactory<>("accountType"));
        transferPermissionColumn.setCellValueFactory(a -> new SimpleStringProperty(a.getValue().canPay() ? "Yes" : "No"));
        statusColumn.setCellValueFactory(a -> new SimpleStringProperty(a.getValue().isActivated() ? "Yes" : "No"));
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
    }

    @FXML
    public void handleFetchClientDetailsButtonClicked(MouseEvent event) {
        updateClientDetails();
    }

    @FXML
    public void handleExportDataButtonClicked(MouseEvent event) {
        Main.setScene(Flow.forward(Scenes.ExportDataScene));
        if (clientDetailsTableView.getSelectionModel().getSelectedItems().size() != 0) {
            // If items selected, only send selected items for export
            ExportDataSceneController.setExportData(new ArrayList<>(clientDetailsTableView.getSelectionModel().getSelectedItems()));
        } else {
            // Send all items for export
            ExportDataSceneController.setExportData(new ArrayList<>(clientDetailsTableView.getItems()));
        }
    }

    @FXML
    public void handleCreateAccountButtonClicked(MouseEvent event) {
        Main.setScene(Flow.forward(Scenes.CreateClientAccountScene));
        // TODO : bring user data to the scene so we can create the account for the right user
    }

    @FXML
    public void handleCloseAccountButtonClicked(MouseEvent event) {
        // TODO : Not implemented in API yet
        clientDetailsTableView.getSelectionModel().getSelectedItems().get(0).delete();
        updateClientDetails();
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
        String entry = searchTextField.getText().toLowerCase();
        if (entry.equals("")) {
            currentData = allData;
            clientDetailsTableView.setItems(currentData);
            return;
        }
        currentData = FXCollections.observableArrayList();

        for (Account account : allData) {
            ArrayList<String> accountParameters = new ArrayList<>(Arrays.asList(account.getIBAN().toLowerCase(),
                    account.getAccountType().toString().toLowerCase(), String.valueOf(account.canPay()).toLowerCase(),
                    String.valueOf(account.isActivated()).toLowerCase()));
            for (String param : accountParameters) {
                if (param.contains(entry)) {
                    currentData.add(account);
                    break;
                }
            }
        }
        clientDetailsTableView.setItems(currentData);
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

    /**
     * Fetches the client details
     */
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
            // Fetches the accountList
            ArrayList<Account> accountList = Main.getCurrentWallet().getAccountList();
            // Add it to the listView
            clientDetailsTableView.setItems(FXCollections.observableArrayList(accountList));
            allData = clientDetailsTableView.getItems();
            sleepAndFadeOutLoadingClientDetailsLabelFadeThread.start(fadeOutDuration, sleepDuration + fadeInDuration, loadingClientDetailsLabel);
        }
    }

    /**
     * Update the client details by reloading the wallet
     */
    public void updateClientDetails() {
        if (loadingClientDetailsLabel.getOpacity() == 0.0) {
            int fadeInDuration = 1000;
            int fadeOutDuration = fadeInDuration;
            int sleepDuration = 1000;
            FadeOutThread sleepAndFadeOutLoadingClientDetailsLabelFadeThread;
            FadeInTransition.playFromStartOn(loadingClientDetailsLabel, Duration.millis(fadeInDuration));
            sleepAndFadeOutLoadingClientDetailsLabelFadeThread = new FadeOutThread();
            Calendar c = Calendar.getInstance();
            lastUpdateTimeLabel.setText("Last update : " + formatCurrentTime(c));
            // Update the wallet
            try {
                Main.getCurrentWallet().update();
            } catch (UnirestException e) {
                Main.ErrorManager(408);
            }
            // Fetches the account list
            ArrayList<Account> accountList = Main.getCurrentWallet().getAccountList();
            clientDetailsTableView.setItems(FXCollections.observableArrayList(accountList));
            sleepAndFadeOutLoadingClientDetailsLabelFadeThread.start(fadeOutDuration, sleepDuration + fadeInDuration, loadingClientDetailsLabel);
        }
    }
}
