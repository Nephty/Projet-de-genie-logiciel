package front.controllers;

import app.Main;
import back.Timespan;
import back.user.Account;
import back.user.Transaction;
import back.user.Wallet;
import front.navigation.Flow;
import front.navigation.navigators.BackButtonNavigator;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.chart.PieChart;
import javafx.scene.chart.PieChart.Data;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.StackPane;

import javax.naming.SizeLimitExceededException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;

public class VisualizeToolSceneController extends Controller implements BackButtonNavigator {
    @FXML
    public Button backButton, setTableMode, setGraphMode, setPieChartMode, addAccountButton, removeAccountButton;
    @FXML
    public ComboBox<Timespan> timeSpanComboBox;
    @FXML
    public ListView<Account> availableAccountsListView, addedAccountsListView;
    @FXML
    public Label availableAccountsLabel, addedAccountsLabel;
    @FXML
    public StackPane chartsPane;
    @FXML
    public PieChart pieChartInPane;

    private PieChart pieChart;
    private ObservableList<PieChart.Data> pieChartData = FXCollections.observableArrayList();


    public void initialize() {
        ObservableList<Timespan> timespanValues = FXCollections.observableArrayList(Arrays.asList(Timespan.DAILY, Timespan.WEEKLY, Timespan.MONTHLY, Timespan.YEARLY));
        timeSpanComboBox.setItems(timespanValues);
        timeSpanComboBox.setValue(Timespan.DAILY);

        ArrayList<Account> accounts = new ArrayList<>();
        for (Wallet wallet : Main.getPortfolio().getWalletList()) {
            accounts.addAll(wallet.getAccountList());
        }
        availableAccountsListView.setItems(FXCollections.observableArrayList(accounts));

        // TODO : load all graphs and only change their visibility when switching mode
    }

    @Override
    public void handleBackButtonNavigation(MouseEvent event) {
        Main.setScene(Flow.back());
        if (addAccountButton.isDisabled()) addAccountButton.setDisable(false);
        if (removeAccountButton.isDisabled()) removeAccountButton.setDisable(false);
    }

    @Override
    public void emulateBackButtonClicked() {
        handleBackButtonNavigation(null);
    }

    @FXML
    public void handleBackButtonClicked(MouseEvent event) {
        handleBackButtonNavigation(event);
    }

    @FXML
    public void handleSetTableModeButtonClicked(MouseEvent event) {
        // TODO : change to table mode
    }

    @FXML
    public void handleSetGraphModeButtonClicked(MouseEvent event) {
        // TODO : change to graph mode
    }

    @FXML
    public void handleSetPieChartModeButtonClicked(MouseEvent event) {
    }

    @FXML
    public void handleAddAccountButtonClicked(MouseEvent event) {
        if (availableAccountsListView.getSelectionModel().getSelectedItems().size() > 0) {
            ObservableList<Account> selection = availableAccountsListView.getSelectionModel().getSelectedItems();
            addedAccountsListView.getItems().addAll(selection);
            // Update pie chart
            for (Account account : selection) {
                double amount = 0;
                try {
                    amount = account.getAmount();
                    // account.getAmountDaysAgo(0);
                } catch (SizeLimitExceededException e) {
                    e.printStackTrace();
                }
                if (amount == 0) amount = 0.01; // very small amount so that it still appears on the chart
                pieChartData.add(new Data(account.getIBAN(), amount));
            }
            pieChartInPane.setData(pieChartData);
            // TODO : update table
            // TODO : update graph
            availableAccountsListView.getItems().removeAll(selection);
        }
    }

    @FXML
    public void handleRemoveAccountModeButtonClicked(MouseEvent event) {
        if (addedAccountsListView.getSelectionModel().getSelectedItems().size() > 0) {
            ObservableList<Account> selection = addedAccountsListView.getSelectionModel().getSelectedItems();
            availableAccountsListView.getItems().addAll(selection);
            // Update pie chart
            for (Account account : selection) {
                for (Data data : pieChartData) {
                    if (account.getIBAN().equals(data.getName())) {
                        pieChartData.remove(data);
                        break;
                    }
                }
            }
            pieChartInPane.setData(pieChartData);
            // TODO : update table
            // TODO : update graph
            addedAccountsListView.getItems().removeAll(selection);
        }
    }

    @FXML
    public void handleComponentKeyReleased(KeyEvent event) {
        if (event.getCode() == KeyCode.ESCAPE) {
            emulateBackButtonClicked();
            event.consume();
        }
    }

    @FXML
    public void handleAvailableAccountsListViewMouseClicked(MouseEvent mouseEvent) {
        removeAccountButton.setDisable(true);
        addAccountButton.setDisable(false);
    }

    @FXML
    public void handleAddedAccountsListViewMouseClicked(MouseEvent mouseEvent) {
        removeAccountButton.setDisable(false);
        addAccountButton.setDisable(true);
    }

    /**
     * Using a list of transactions and a timespan, returns all transactions taken from the history that correspond to
     * the defined timespan. If the time span is DAILY, selects transactions that occurred within 30 days, if it is
     * WEEKLY, selects transactions that occurred within 6 weeks, if it is MONTHLY, selects transactions that occurred
     * within 6 months, and if it is YEARLY, selects transactions that occurred within 4 years.
     *
     * @param history  The transaction history that will be parsed
     * @param timeSpan The timespan that will define how far back in time we will go to grab transactions
     * @return A list of transactions that occurred within the given timespan
     */
    public ArrayList<Transaction> getTransactionsHistoryFromTimeSpan(ArrayList<Transaction> history, Timespan timeSpan) {
        // today's date as milliseconds since epoch
        long today = Instant.now().toEpochMilli();
        ArrayList<Transaction> toApply = new ArrayList<>();
        // 1 day in milliseconds : 86.400.000ms
        long _1_DAY_IN_MILLISECONDS = 86400000L;
        long _30_DAYS_IN_MILLISECONDS = 30 * _1_DAY_IN_MILLISECONDS;
        // 8 weeks = 56 days
        long _8_WEEKS_IN_MILLISECONDS = 56 * _1_DAY_IN_MILLISECONDS;
        // 6 months = 182 days
        long _6_MONTHS_IN_MILLISECONDS = 182 * _1_DAY_IN_MILLISECONDS;
        // 4 years = 1461 days
        long _4_YEARS_IN_MILLISECONDS = 1461 * _1_DAY_IN_MILLISECONDS;

        // TODO : if the history list is sorted, we can break out of the loop once we reach a transactions that is too
        //  far back in time
        for (Transaction t : history) {
            // for each transaction, if they are in the date range, keep it in a list to use them to compute the amount
            // of money the account had at a particular time stamp
            long sendingDate = t.getSendingDateAsDateObject();
            switch (timeSpan) {
                case DAILY:
                    // last 30 days
                    if (today - sendingDate < _30_DAYS_IN_MILLISECONDS) toApply.add(t);
                    break;
                case WEEKLY:
                    // last 8 weeks
                    if (today - sendingDate < _8_WEEKS_IN_MILLISECONDS) toApply.add(t);
                    break;
                case MONTHLY:
                    // last 6 months
                    if (today - sendingDate < _6_MONTHS_IN_MILLISECONDS) toApply.add(t);
                    break;
                case YEARLY:
                    // last 4 years
                    if (today - sendingDate < _4_YEARS_IN_MILLISECONDS) toApply.add(t);
                    break;
            }
        }

        return toApply;
    }

    /**
     * Using an initial value, a list of transactions and a timespan, returns a list of values that correspond to the
     * amount of money that was held by an account periodically (according to the timespan). If we give an initial value
     * of 50, a list of transactions and a timespan of DAILY, returns a list of values that correspond to the amounts of
     * money held by an account that has 50 of any currency as of today, evaluated every day (that is, one day will
     * separate every value). If the timespan is WEEKLY, MONTHLY or YEARLY, every value will have a one week, month or
     * year interval respectively.
     *
     * @param initialValue The initial amount of money held by an account
     * @param history      The transaction history for the account (WARNING : needs to be complete, or the computations will
     *                     be inaccurate)
     * @param timeSpan     The timespan that has to separate every value (one day, one week, one month or one year)
     * @return A list of values that represent the amounts of money held by an account that has the given initial value
     * of money as of today, evaluated every day/week/month/year (according to the given timespan)
     */
    public ArrayList<Double> computeValuesHistory(int initialValue, ArrayList<Transaction> history, Timespan timeSpan) {
        ArrayList<Double> valuesHistory = new ArrayList<>();
        // TODO : compute value of account at given time stamps now that we have all required transactions
        //  Idea : iterate over every transaction and if it is a new day/week/... then make a new value for the list
        switch (timeSpan) {
            case DAILY:
                break;
            case WEEKLY:

                break;
            case MONTHLY:

                break;
            case YEARLY:

                break;
        }
        return valuesHistory;
    }
}
