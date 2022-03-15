package front.controllers;

import app.Main;
import back.user.Account;
import back.user.Wallet;
import front.animation.FadeInTransition;
import front.animation.threads.FadeOutThread;
import front.navigation.Flow;
import front.navigation.navigators.BackButtonNavigator;
import front.scenes.Scenes;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.util.Duration;

import java.util.ArrayList;
import java.util.Calendar;

public class ProductDetailsSceneController extends Controller implements BackButtonNavigator {

    @FXML
    public Button backButton, historyButton, fetchAccountButton, transferButton, toggleButton;
    @FXML
    public ListView<Account> accountsListView;

    @FXML
    public Label lastUpdateTimeLabel;
    @FXML
    public Label loadingAccountsLabel;
    @FXML
    public Label togglingProductLabel;
    @FXML
    public Label toggledOnProductLabel;
    @FXML
    public Label toggledOffProductLabel;
    @FXML
    public Label accountInactiveLabel;

    public void initialize() {
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
    public void handleBackButtonClicked(MouseEvent event) {
        handleBackButtonNavigation(event);
        accountInactiveLabel.setVisible(false);
    }

    @FXML
    public void handleHistoryButtonClicked(MouseEvent event) {
        if (accountsListView.getSelectionModel().getSelectedItems().size() == 1) {
            Main.setScene(Flow.forward(Scenes.TransactionsHistoryScene));
            accountInactiveLabel.setVisible(false);
            Main.setCurrentAccount(accountsListView.getSelectionModel().getSelectedItems().get(0));
        }
    }

    @FXML
    public void handleFetchAccountsButtonClicked(MouseEvent event) {
        updateAccounts();
    }

    @FXML
    public void handleTransferButtonClicked(MouseEvent event) {
        if (accountsListView.getSelectionModel().getSelectedItems().size() == 1) {
            if (accountsListView.getSelectionModel().getSelectedItem().isActivated()) {
                Main.setScene(Flow.forward(Scenes.TransferScene));
                accountInactiveLabel.setVisible(false);
                Main.setCurrentAccount(accountsListView.getSelectionModel().getSelectedItems().get(0));
            } else {
                accountInactiveLabel.setVisible(true);
                // TODO : show label "account inactive"
            }
        }
    }

    @FXML
    public void handleToggleButtonClicked(MouseEvent event) {
        // If the user selected one wallet
        if (accountsListView.getSelectionModel().getSelectedItems().size() == 1) {
            toggleProduct(accountsListView.getSelectionModel().getSelectedItems().get(0));
            accountInactiveLabel.setVisible(false);
        }
    }

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
            // Fetch accounts and put them in the listview
            // TODO : back-end : fetch accounts from the database and put them in the listview
            ArrayList<Wallet> walletList = Main.getPortfolio().getWalletList();
            Wallet currentWallet = Main.getCurrentWallet();
            if (currentWallet == null) {
                currentWallet = walletList.get(0);
            }

            // Fade the label "updating accounts..." out to 0.0 opacity
            sleepAndFadeOutLoadingAccountsLabelFadeThread.start(fadeOutDuration, sleepDuration + fadeInDuration, loadingAccountsLabel);
            accountsListView.setItems(FXCollections.observableArrayList(currentWallet.getAccountList()));
        }
    }

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
            // Fetch accounts and put them in the listview
            // TODO : back-end : fetch accounts from the database and put them in the listview
            Main.updatePortfolio();
            ArrayList<Wallet> walletList = Main.getPortfolio().getWalletList();
            String currentWallet = Main.getCurrentWallet().getBank().getSwiftCode();
            ArrayList<Account> accountList = null;
            for (int i = 0; i < walletList.size(); i++) {
                if (walletList.get(i).getBank().getSwiftCode().equals(currentWallet)) {
                    accountList = walletList.get(i).getAccountList();
                }
            }

            // Fade the label "updating accounts..." out to 0.0 opacity
            sleepAndFadeOutLoadingAccountsLabelFadeThread.start(fadeOutDuration, sleepDuration + fadeInDuration, loadingAccountsLabel);
            accountsListView.setItems(FXCollections.observableArrayList(accountList));
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
                FadeOutThread sleepAndFadeOutProductToggledOffLabelFadeThread;
                FadeInTransition.playFromStartOn(toggledOffProductLabel, Duration.millis(fadeInDuration));
                sleepAndFadeOutProductToggledOffLabelFadeThread = new FadeOutThread();
                sleepAndFadeOutProductToggledOffLabelFadeThread.start(fadeOutDuration, sleepDuration + fadeInDuration, toggledOffProductLabel);

                account.toggleOff();
            } else {
                FadeOutThread sleepAndFadeOutProductToggledOnLabelFadeThread;
                FadeInTransition.playFromStartOn(toggledOnProductLabel, Duration.millis(fadeInDuration));
                sleepAndFadeOutProductToggledOnLabelFadeThread = new FadeOutThread();
                sleepAndFadeOutProductToggledOnLabelFadeThread.start(fadeOutDuration, sleepDuration + fadeInDuration, toggledOnProductLabel);

                account.toggleOn();
            }
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