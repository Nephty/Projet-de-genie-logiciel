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
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.util.Duration;

import java.util.ArrayList;
import java.util.Calendar;

import static app.Main.appLocale;

public class ProductDetailsSceneController extends Controller implements BackButtonNavigator {

    @FXML
    Button backButton, historyButton, fetchAccountsButton, transferButton, toggleButton;
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
        if (accountsTableView.getSelectionModel().getSelectedItems().size() == 1) {
            toggleProduct(accountsTableView.getSelectionModel().getSelectedItems().get(0));
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
            ArrayList<Wallet> walletList = Main.getPortfolio().getWalletList();
            Wallet currentWallet = Main.getCurrentWallet();
            if (currentWallet == null) {
                currentWallet = walletList.get(0);
            }
            // Put the accounts in the listview
            accountsTableView.setItems(FXCollections.observableArrayList(currentWallet.getAccountList()));
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
            Main.updatePortfolio();
            ArrayList<Wallet> walletList = Main.getPortfolio().getWalletList();
            String currentWallet = Main.getCurrentWallet().getBank().getSwiftCode();
            ArrayList<Account> accountList = null;
            for (Wallet wallet : walletList) {
                if (wallet.getBank().getSwiftCode().equals(currentWallet)) {
                    accountList = wallet.getAccountList();
                }
            }
            // Put the accounts in the listview
            accountsTableView.setItems(FXCollections.observableArrayList(accountList));
            // Fade the label "updating accounts..." out to 0.0 opacity
            sleepAndFadeOutLoadingAccountsLabelFadeThread.start(fadeOutDuration, sleepDuration + fadeInDuration, loadingAccountsLabel);
        }
    }

    public void toggleProduct(Account account) {
        // Execute this only if the label is not visible (that is, only if we are not already toggling it)
        if (togglingProductLabel.getOpacity() == 0.0) {
            int fadeInDuration = 1000;
            int fadeOutDuration = fadeInDuration;
            int sleepDuration = 1000;

            // Toggle on or off the product
            if (account.isActivated()) {
                fadeInAndOutNode(1000, toggledOffProductLabel);

                try {
                    account.toggleOff();
                } catch (UnirestException e) {
                    Main.ErrorManager(408);
                }

            } else {
                fadeInAndOutNode(1000, toggledOnProductLabel);

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
}
