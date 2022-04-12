package front.controllers;

import app.Main;
import back.user.Account;
import back.user.Wallet;
import com.mashape.unirest.http.exceptions.UnirestException;
import front.animation.FadeInTransition;
import front.animation.threads.FadeOutThread;
import front.navigation.Flow;
import front.navigation.navigators.BackButtonNavigator;
import front.scenes.SceneLoader;
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
import java.util.Calendar;
import java.util.NoSuchElementException;

import static app.Main.appLocale;

public class ProductDetailsSceneController extends Controller implements BackButtonNavigator {
    @FXML
    Button backButton, historyButton, fetchAccountsButton, transferButton, toggleButton, archiveAccountButton, archivedAccountsButton;
    @FXML
    TableView<Account> accountsTableView;
    @FXML
    TableColumn<Account, String> bankNameColumn, IBANColumn, accountTypeColumn, transferPermissionsColumn, subAccountsColumn, activatedColumn;
    @FXML
    Label lastUpdateTimeLabel;
    @FXML
    Label loadingAccountsLabel;
    @FXML
    Label togglingProductLabel;
    @FXML
    Label toggledOnProductLabel;
    @FXML
    Label toggledOffProductLabel;
    @FXML
    Label accountInactiveLabel;

    ObservableList<Account> data = FXCollections.observableArrayList();

    public void initialize() {
        bankNameColumn.setCellValueFactory(a -> new SimpleStringProperty(a.getValue().getBank().getName()));
        IBANColumn.setCellValueFactory(new PropertyValueFactory<>("IBAN"));
        accountTypeColumn.setCellValueFactory(new PropertyValueFactory<>("accountType"));
        transferPermissionsColumn.setCellValueFactory(a -> new SimpleStringProperty(a.getValue().canPay() ? "Yes" : "No"));
        subAccountsColumn.setCellValueFactory(a -> new SimpleStringProperty(String.valueOf(a.getValue().getSubAccountList().size())));
        activatedColumn.setCellValueFactory(a -> new SimpleStringProperty(a.getValue().isActivated() ? "Yes" : "No"));
        accountsTableView.setPlaceholder(new Label("No account available."));
        accountsTableView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        fetchAccounts();
        updateAccounts();
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
    void handleBackButtonClicked(MouseEvent event) {
        handleBackButtonNavigation(event);
        accountInactiveLabel.setVisible(false);
    }

    @FXML
    void handleHistoryButtonClicked(MouseEvent event) {
        if (accountsTableView.getSelectionModel().getSelectedItems().size() == 1) {
            accountInactiveLabel.setVisible(false);
            Main.setCurrentAccount(accountsTableView.getSelectionModel().getSelectedItems().get(0));
            Scenes.TransactionsHistoryScene = SceneLoader.load("TransactionsHistoryScene.fxml", appLocale);
            Main.setScene(Flow.forward(Scenes.TransactionsHistoryScene));
        }
    }

    @FXML
    void handleFetchAccountsButtonClicked(MouseEvent event) {
        fetchAccounts();
        updateAccounts();
    }

    @FXML
    void handleTransferButtonClicked(MouseEvent event) {
        if (accountsTableView.getSelectionModel().getSelectedItems().size() == 1) {
            if (accountsTableView.getSelectionModel().getSelectedItem().isActivated()) {
                Main.setScene(Flow.forward(Scenes.TransferScene));
                accountInactiveLabel.setVisible(false);
                Main.setCurrentAccount(accountsTableView.getSelectionModel().getSelectedItems().get(0));
            } else {
                accountInactiveLabel.setVisible(true);
            }
        }
    }

    @FXML
    void handleToggleButtonClicked(MouseEvent event) {
        // If the user selected one wallet
        if (accountsTableView.getSelectionModel().getSelectedItems().size() > 0) {
            for (Account account : accountsTableView.getSelectionModel().getSelectedItems()) {
                toggleProduct(account);
            }
            accountInactiveLabel.setVisible(false);
        }
    }

    /**
     * Fetches the account
     */
    public void fetchAccounts() {
        if (loadingAccountsLabel.getOpacity() == 0.0) {
            int fadeInDuration = 1000;
            int fadeOutDuration = fadeInDuration;
            int sleepDuration = 1000;
            FadeOutThread sleepAndFadeOutLoadingAccountsLabelFadeThread;
            // Fade the label "updating accounts..." in to 1.0 opacity
            FadeInTransition.playFromStartOn(loadingAccountsLabel, Duration.millis(fadeInDuration));
            // We use a new Thread, so we can sleep the method for a few hundreds of milliseconds so that the label
            // doesn't instantly go away when the notifications are retrieved.
            sleepAndFadeOutLoadingAccountsLabelFadeThread = new FadeOutThread();
            // Save actual time and date
            Calendar c = Calendar.getInstance();
            // Update lastUpdateLabel with the new time and date
            lastUpdateTimeLabel.setText("Last update : " + formatCurrentTime(c));
            // Fetch accounts
            Wallet currentWallet = Main.getCurrentWallet();
            ArrayList<Account> accountsList = new ArrayList<>();
            for (Account account : currentWallet.getAccountList()) {
                if (!account.isArchived()) accountsList.add(account);
            }
            data = FXCollections.observableArrayList(accountsList);
            accountsTableView.setItems(data);
            // Fade the label "updating accounts..." out to 0.0 opacity
            sleepAndFadeOutLoadingAccountsLabelFadeThread.start(fadeOutDuration, sleepDuration + fadeInDuration, loadingAccountsLabel);
        }
    }

    /**
     * Updates the accounts
     */
    public void updateAccounts() {
        // Execute this only if the label is not visible (that is, only if we are not already retrieving data etc)
        if (loadingAccountsLabel.getOpacity() == 0.0) {
            int fadeInDuration = 1000;
            int fadeOutDuration = fadeInDuration;
            int sleepDuration = 1000;
            FadeOutThread sleepAndFadeOutLoadingAccountsLabelFadeThread;
            // Fade the label "updating accounts..." in to 1.0 opacity
            FadeInTransition.playFromStartOn(loadingAccountsLabel, Duration.millis(fadeInDuration));
            // We use a new Thread, so we can sleep the method for a few hundreds of milliseconds so that the label
            // doesn't instantly go away when the notifications are retrieved.
            sleepAndFadeOutLoadingAccountsLabelFadeThread = new FadeOutThread();
            // Save actual time and date
            Calendar c = Calendar.getInstance();
            // Update lastUpdateLabel with the new time and date
            lastUpdateTimeLabel.setText("Last update : " + formatCurrentTime(c));
            // Update the portfolio and then fetch the accounts
            Wallet currentWallet = Main.getCurrentWallet();
            ArrayList<Account> accountsList = new ArrayList<>();
            for (Account account : currentWallet.getAccountList()) {
                if (!account.isArchived()) accountsList.add(account);
            }
            data = FXCollections.observableArrayList(accountsList);
            accountsTableView.setItems(data);
            // Fade the label "updating accounts..." out to 0.0 opacity
            sleepAndFadeOutLoadingAccountsLabelFadeThread.start(fadeOutDuration, sleepDuration + fadeInDuration, loadingAccountsLabel);
        }
    }

    public void toggleProduct(Account account) {
        // Execute this only if the label is not visible (that is, only if we are not already toggling it)
        if (togglingProductLabel.getOpacity() == 0.0) {
            int sleepDuration = 1000;

            // Toggle on or off the product
            if (account.isActivated()) {
                fadeInAndOutNode(sleepDuration, toggledOffProductLabel);

                try {
                    account.toggleOff();
                } catch (UnirestException e) {
                    Main.ErrorManager(408);
                }

            } else {
                fadeInAndOutNode(sleepDuration, toggledOnProductLabel);

                try {
                    account.toggleOn();
                } catch (UnirestException e) {
                    Main.ErrorManager(408);
                }
            }
        }
    }

    @FXML
    void handleComponentKeyReleased(KeyEvent event) {
        if (event.getCode() == KeyCode.ESCAPE) {
            emulateBackButtonClicked();
            event.consume();
        }
    }

    @FXML
    void handleArchivedAccountsButtonClicked(MouseEvent mouseEvent) {
        Scenes.ArchivedAccountsScene = SceneLoader.load("ArchivedAccountsScenes.fxml", appLocale);
        Main.setScene(Flow.forward(Scenes.ArchivedAccountsScene));
    }

    @FXML
    void handleArchiveAccountButtonClicked(MouseEvent mouseEvent) {
        if (accountsTableView.getSelectionModel().getSelectedItems().size() > 0) {
            ObservableList<Account> selection = accountsTableView.getSelectionModel().getSelectedItems();
            try {
                for (Account account : selection) {
                    archiveAccount(account);
                    data.remove(account);
                }
            } catch (NoSuchElementException e) {
                // This exception occurs when the user archives the last visible account remaining
                // The reason is that JavaFX instantly updates the selection when an item is removed,
                // so the selection becomes empty, throwing a NoSuchElementException
            }
            accountsTableView.setItems(data);
        }
    }

    private void archiveAccount(Account account) {
        account.setArchived(true);
        // TODO : set account as archived in database
    }
}
