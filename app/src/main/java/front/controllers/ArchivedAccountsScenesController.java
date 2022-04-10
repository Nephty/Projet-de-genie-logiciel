package front.controllers;

import app.Main;
import back.user.Account;
import back.user.Wallet;
import front.animation.FadeInTransition;
import front.animation.threads.FadeOutThread;
import front.navigation.Flow;
import front.navigation.navigators.BackButtonNavigator;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.util.Duration;

import java.util.ArrayList;
import java.util.Calendar;

public class ArchivedAccountsScenesController extends Controller implements BackButtonNavigator {
    @FXML
    TableColumn<Account, String> bankNameColumn, IBANColumn, accountTypeColumn, transferPermissionsColumn, subAccountsColumn, activatedColumn;
    @FXML
    Label lastUpdateTimeLabel, loadingAccountsLabel;
    @FXML
    Button backButton, fetchAccountsButton, restoreButton;
    @FXML
    TableView<Account> archivedAccountsTableView;

    public void initialize() {
        archivedAccountsTableView.setPlaceholder(new Label("No archived account."));
        archivedAccountsTableView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        updateArchivedAccounts();
    }

    @Override
    public void handleBackButtonNavigation(MouseEvent mouseEvent) {
        Main.setScene(Flow.back());
    }

    @Override
    public void emulateBackButtonClicked() {
        handleBackButtonClicked(null);
    }

    @FXML
    void handleBackButtonClicked(MouseEvent event) {
        handleBackButtonNavigation(event);
    }

    @FXML
    void handleComponentKeyReleased(KeyEvent keyEvent) {
        if (keyEvent.getCode() == KeyCode.ESCAPE) {
            emulateBackButtonClicked();
            keyEvent.consume();
        }
    }

    public void handleRestoreButtonClicked(MouseEvent mouseEvent) {
        if (archivedAccountsTableView.getSelectionModel().getSelectedItems().size() == 1) {
            ObservableList<Account> selection = archivedAccountsTableView.getSelectionModel().getSelectedItems();
            selection.get(0).restore();
            // remove the account from the table view
            ObservableList<Account> newItems = archivedAccountsTableView.getItems();
            newItems.remove(selection.get(0));
            archivedAccountsTableView.setItems(newItems);
        }
    }

    public void handleFetchAccountsButtonClicked(MouseEvent mouseEvent) {
        updateArchivedAccounts();
    }

    /**
     * Updates the archived accounts
     */
    public void updateArchivedAccounts() {
        // TODO : this should be a "fetch" method and use a request on the API to synchronize to the DB
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
            ArrayList<Account> accountsList = new ArrayList<>();
            for (Wallet wallet : walletList) {
                for (Account account : wallet.getAccountList()) {
                    if (account.isArchived()) accountsList.add(account);
                }
            }
            // Put the accounts in the listview
            archivedAccountsTableView.setItems(FXCollections.observableArrayList(accountsList));
            // Fade the label "updating accounts..." out to 0.0 opacity
            sleepAndFadeOutLoadingAccountsLabelFadeThread.start(fadeOutDuration, sleepDuration + fadeInDuration, loadingAccountsLabel);
        }
    }
}
