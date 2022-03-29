package front.controllers;

import app.Main;
import back.Timespan;
import back.user.Account;
import back.user.SubAccount;
import back.user.Transaction;
import back.user.Wallet;
import front.navigation.Flow;
import front.navigation.navigators.BackButtonNavigator;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.chart.*;
import javafx.scene.chart.PieChart.Data;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.StackPane;

import java.time.Instant;
import java.time.LocalDate;
import java.time.Period;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class VisualizeToolSceneController extends Controller implements BackButtonNavigator {
    @FXML
    public Button backButton, setTableMode, setGraphMode, setPieChartMode, addAccountButton, removeAccountButton;
    @FXML
    public ComboBox<Timespan> timeSpanComboBox;
    @FXML
    public ListView<SubAccount> availableAccountsListView, addedAccountsListView;
    @FXML
    public Label availableAccountsLabel, addedAccountsLabel;
    @FXML
    public StackPane chartsPane;
    @FXML
    public PieChart pieChart;
    @FXML
    public StackedAreaChart<String, Double> stackedAreaChart;
    private ObservableList<PieChart.Data> pieChartData = FXCollections.observableArrayList();
    CategoryAxis xAxis;
    NumberAxis yAxis;
    ArrayList<Double> valuesHistory;


    public void initialize() {
        ObservableList<Timespan> timespanValues = FXCollections.observableArrayList(Arrays.asList(Timespan.DAILY, Timespan.WEEKLY, Timespan.MONTHLY, Timespan.YEARLY));
        timeSpanComboBox.setItems(timespanValues);
        timeSpanComboBox.setValue(Timespan.DAILY);

        ArrayList<SubAccount> subAccounts = new ArrayList<>();
        for (Wallet wallet : Main.getPortfolio().getWalletList()) {
            for (Account account : wallet.getAccountList()) {
                subAccounts.addAll(account.getSubAccountList());
            }
        }
        availableAccountsListView.setItems(FXCollections.observableArrayList(subAccounts));

        xAxis = new CategoryAxis();
        xAxis.setLabel("Time");
        yAxis = new NumberAxis(0, 1000, 100);
        yAxis.setLabel("Money");
        stackedAreaChart = new StackedAreaChart(xAxis, yAxis);

        stackedAreaChart.setVisible(false);
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
        pieChart.setVisible(false);
        stackedAreaChart.setVisible(false);
    }

    @FXML
    public void handleSetGraphModeButtonClicked(MouseEvent event) {
        ArrayList<String> dates = new ArrayList<>();
        XYChart.Series<String, Double> data = new XYChart.Series<>();
        SubAccount subAccountToVisualize = addedAccountsListView.getItems().get(0);
        valuesHistory = computeValuesHistoryProcess(subAccountToVisualize.getAmount(), subAccountToVisualize.getTransactionHistory(), timeSpanComboBox.getValue(), subAccountToVisualize);
        switch (timeSpanComboBox.getValue()) {
            case DAILY:
                // For every day, add a new data that follow the pattern (String, Double) <=> (Date, Amount of money)
                // and add every date to the dates list (we use this list for the x-axis
                for (int i = 0; i < 30; i++) {
                    LocalDate datePoint = LocalDate.now().minus(Period.ofDays(30-i));
                    dates.add(datePoint.toString());
                    XYChart.Data<String, Double> dataPoint = new XYChart.Data<>(datePoint.toString(), valuesHistory.get(i));
                    data.getData().add(dataPoint);
                }
                xAxis.setCategories(FXCollections.observableArrayList(dates));
                for (int i = 0; i < stackedAreaChart.getData().size(); i++) {
                    stackedAreaChart.getData().set(i, null);
                }
                stackedAreaChart.getData().add(data);
                break;
            case WEEKLY:
                for (int i = 0; i < 8; i++) {
                    dates.add(LocalDate.now().minus(Period.ofWeeks(8-i)).toString());
                }
                xAxis.setCategories(FXCollections.observableArrayList(dates));
                break;
            case MONTHLY:
                for (int i = 0; i < 6; i++) {
                    dates.add(LocalDate.now().minus(Period.ofMonths(6-i)).toString());
                }
                xAxis.setCategories(FXCollections.observableArrayList(dates));
                break;
            case YEARLY:
                for (int i = 0; i < 4; i++) {
                    dates.add(LocalDate.now().minus(Period.ofYears(4-i)).toString());
                }
                xAxis.setCategories(FXCollections.observableArrayList(dates));
                break;
        }
        pieChart.setVisible(false);
        stackedAreaChart.setVisible(true);
        // TODO : set last chart to invisible
    }

    @FXML
    public void handleSetPieChartModeButtonClicked(MouseEvent event) {
        stackedAreaChart.setVisible(false);
        pieChart.setVisible(true);
        // TODO : set last chart to invisible
    }

    @FXML
    public void handleAddAccountButtonClicked(MouseEvent event) {
        if (availableAccountsListView.getSelectionModel().getSelectedItems().size() > 0) {
            ObservableList<SubAccount> selection = availableAccountsListView.getSelectionModel().getSelectedItems();
            addedAccountsListView.getItems().addAll(selection);
            // Update pie chart
            for (SubAccount account : selection) {
                double amount = 0;
                amount = account.getAmount();
                // account.getAmountDaysAgo(0);
                if (amount == 0) amount = 0.01; // very small amount so that it still appears on the chart
                pieChartData.add(new Data(account.getIBAN(), amount));
            }
            pieChart.setData(pieChartData);
            // TODO : update table
            // TODO : update graph
            availableAccountsListView.getItems().removeAll(selection);
        }
    }

    @FXML
    public void handleRemoveAccountModeButtonClicked(MouseEvent event) {
        if (addedAccountsListView.getSelectionModel().getSelectedItems().size() > 0) {
            ObservableList<SubAccount> selection = addedAccountsListView.getSelectionModel().getSelectedItems();
            SubAccount selectedSubAccount = selection.get(0);
            availableAccountsListView.getItems().addAll(selection);
            // Update pie chart
            for (SubAccount account : selection) {
                for (Data data : pieChartData) {
                    if (account.getIBAN().equals(data.getName())) {
                        pieChartData.remove(data);
                        break;
                    }
                }
            }
            pieChart.setData(pieChartData);
            // TODO : update table
            // TODO : update graph
            addedAccountsListView.getItems().removeAll(selection);
            valuesHistory = computeValuesHistoryProcess(selectedSubAccount.getAmount(), selectedSubAccount.getTransactionHistory(), timeSpanComboBox.getValue(), selectedSubAccount);
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
     * Separates all transactions given in the history into a <code>HashMap</code> that uses String as keys. These
     * strings represent all the different time stamps. If the given timespan is DAILY, every key is a day of the year.
     * If it is WEEKLY, every key is a week of the year. If it is MONTHLY, every key is a month. If it is YEARLY, every
     * key is a year. Using these keys, we can retrieve a list of all transactions that happened on that
     * day/week/month/year in an ArrayList of Transaction.
     *
     * @param history  The transactions history
     * @param timeSpan The timespan to follow
     * @return A HashMap that has every time stamps as keys that lead to ArrayLists of Transactions that happened during
     * that time range (day/week/month/year).
     */
    public HashMap<String, ArrayList<Transaction>> separateTransactionsIntoHashMap(ArrayList<Transaction> history, Timespan timeSpan) {
        HashMap<String, ArrayList<Transaction>> dateTransactionSeparator = new HashMap<>();
        switch (timeSpan) {
            case DAILY:
                for (Transaction t : history) {
                    String sendingDate = t.getSendingDate();
                    String sendingDateDayOfYearAsString = String.valueOf(StringDateParser.getDayOfYearFromString(sendingDate));
                    if (dateTransactionSeparator.containsKey(sendingDateDayOfYearAsString)) {
                        ArrayList<Transaction> oldContent = dateTransactionSeparator.get(sendingDateDayOfYearAsString);
                        oldContent.add(t);
                        dateTransactionSeparator.put(sendingDateDayOfYearAsString, oldContent);
                    } else {
                        dateTransactionSeparator.put(sendingDateDayOfYearAsString, new ArrayList<>(List.of(t)));
                    }
                }
                break;
            case WEEKLY:
                for (Transaction t : history) {
                    String sendingDate = t.getSendingDate();
                    String sendingDateWeekOfYearAsString = String.valueOf(StringDateParser.getYearWeekFromString(sendingDate));
                    if (dateTransactionSeparator.containsKey(sendingDateWeekOfYearAsString)) {
                        dateTransactionSeparator.get(sendingDateWeekOfYearAsString).add(t);
                    } else {
                        dateTransactionSeparator.put(sendingDateWeekOfYearAsString, new ArrayList<>(List.of(t)));
                    }
                }
                break;
            case MONTHLY:
                for (Transaction t : history) {
                    String sendingDate = t.getSendingDate();
                    String sendingDateMonth = String.valueOf(StringDateParser.getMonthFromString(sendingDate));
                    if (dateTransactionSeparator.containsKey(sendingDateMonth)) {
                        dateTransactionSeparator.get(sendingDateMonth).add(t);
                    } else {
                        dateTransactionSeparator.put(sendingDateMonth, new ArrayList<>(List.of(t)));
                    }
                }
                break;
            case YEARLY:
                for (Transaction t : history) {
                    String sendingDate = t.getSendingDate();
                    String sendingDateYear = String.valueOf(StringDateParser.getYearFromString(sendingDate));
                    if (dateTransactionSeparator.containsKey(sendingDateYear)) {
                        dateTransactionSeparator.get(sendingDateYear).add(t);
                    } else {
                        dateTransactionSeparator.put(sendingDateYear, new ArrayList<>(List.of(t)));
                    }
                }
                break;
        }
        return dateTransactionSeparator;
    }

    /**
     * Prepares a comprehensive <code>HashMap</code> that contains every key required for time analysis according to the
     * given timespan. If the timespan is DAILY, the hashmap will have a key for every day of the last 30 days. If it is
     * WEEKLY, the hashmap will have a key for every week of the last 8 weeks. If it is MONTHLY, the hashmap will have a
     * key for every month of the last 6 months. If it is YEARLY, the hashmap will have a key for every year of the last
     * 4 years. Every key leads to an empty ArrayList of Transaction. This method prepares the hashmap for analysis :
     * if no transaction occurred on a specific day, we still need to acknowledge that. If we don't, we may encounter
     * issues when tracing the value history of an account.
     *
     * @param hashMap  Data we already have for every day : use the method <code>SeparateTransactionsIntoHashMap</code>
     *                 to format an ArrayList of transaction into an HashMap that this method can use
     * @param timeSpan The timespan to follow
     * @return A comprehensive HashMap containing an ArrayList for every time stamp. Value computation can now begin.
     */
    public HashMap<String, ArrayList<Transaction>> prepareComprehensiveTransactionsHashMap
    (HashMap<String, ArrayList<Transaction>> hashMap, Timespan timeSpan) {
        HashMap<String, ArrayList<Transaction>> comprehensiveHashMap = new HashMap<>();

        switch (timeSpan) {
            case DAILY:
                int daysSpan = 30;
                LocalDate date30DaysAgo = LocalDate.now().minus(Period.ofDays(daysSpan));
                int day30DaysAgoAfterIteration;
                for (int i = 0; i < daysSpan; i++) {
                    day30DaysAgoAfterIteration = (date30DaysAgo.getDayOfYear() + i) % 365;
                    if (hashMap.containsKey(String.valueOf(day30DaysAgoAfterIteration))) {
                        // There is data for that particular day
                        comprehensiveHashMap.put(String.valueOf(day30DaysAgoAfterIteration), hashMap.get(String.valueOf(day30DaysAgoAfterIteration)));
                    } else {
                        // There is no data for that day : add an empty list
                        comprehensiveHashMap.put(String.valueOf(day30DaysAgoAfterIteration), new ArrayList<>());
                    }
                }
                break;
            case WEEKLY:
                int weeksSpan = 8;
                LocalDate date8WeeksAgo = LocalDate.now().minus(Period.ofDays(weeksSpan * 7));
                int week8WeeksAgoAfterIteration;
                for (int i = 0; i < weeksSpan; i++) {
                    week8WeeksAgoAfterIteration = ((date8WeeksAgo.getDayOfYear() / 7) + i) % 52;
                    if (hashMap.containsKey(String.valueOf(week8WeeksAgoAfterIteration))) {
                        // There is data for that particular week
                        comprehensiveHashMap.put(String.valueOf(week8WeeksAgoAfterIteration), hashMap.get(String.valueOf(week8WeeksAgoAfterIteration)));
                    } else {
                        // There is no data for that week : add an empty list
                        comprehensiveHashMap.put(String.valueOf(week8WeeksAgoAfterIteration), new ArrayList<>());
                    }
                }
                break;
            case MONTHLY:
                int monthsSpan = 6;
                LocalDate date6MonthsAgo = LocalDate.now().minus(Period.ofDays(182));
                int month6MonthsAgoAfterIteration;
                for (int i = 0; i < monthsSpan; i++) {
                    month6MonthsAgoAfterIteration = (date6MonthsAgo.getMonth().getValue() + i) % 12;
                    if (hashMap.containsKey(String.valueOf(month6MonthsAgoAfterIteration))) {
                        // There is data for that particular month
                        comprehensiveHashMap.put(String.valueOf(month6MonthsAgoAfterIteration), hashMap.get(String.valueOf(month6MonthsAgoAfterIteration)));
                    } else {
                        // There is no data for that month : add an empty list
                        comprehensiveHashMap.put(String.valueOf(month6MonthsAgoAfterIteration), new ArrayList<>());
                    }
                }
                break;
            case YEARLY:
                int yearsSpan = 4;
                LocalDate date4YearsAgo = LocalDate.now().minus(Period.ofDays(yearsSpan));
                int year4YearsAgoAfterIteration;
                for (int i = 0; i < yearsSpan; i++) {
                    year4YearsAgoAfterIteration = date4YearsAgo.getYear() + i;
                    if (hashMap.containsKey(String.valueOf(year4YearsAgoAfterIteration))) {
                        // There is data for that particular year
                        comprehensiveHashMap.put(String.valueOf(year4YearsAgoAfterIteration), hashMap.get(String.valueOf(year4YearsAgoAfterIteration)));
                    } else {
                        // There is no data for that year : add an empty list
                        comprehensiveHashMap.put(String.valueOf(year4YearsAgoAfterIteration), new ArrayList<>());
                    }
                }
                break;
        }
        return comprehensiveHashMap;
    }

    /**
     * Computes all values of accounts holdings at regular intervals using the given transactions and puts all the values
     * in an array list that corresponds to how much money was on an account at regular intervals. These intervals are given
     * by the HashMap that matches strings to lists of transactions. These lists of transactions must reflect the intervals
     * of time. For example, the lists of transactions are the transactions that happened every day for 30 days. If so,
     * the returned arraylist will contain the holding history of an account recorded every day for 30 days.
     *
     * @param initialValue The initial amount of money on the account
     * @param transactions A hashmap that maps dates to a list of transactions (every entry in the hashmap is regularly
     *                     spaced one from another
     * @param account      The account from which we track the history
     * @return An arraylist containing the holdings history at every regular timestamp
     */
    public ArrayList<Double> computeValuesHistory(double initialValue, HashMap<String, ArrayList<Transaction>> transactions, SubAccount account) {
        double value = initialValue;
        ArrayList<Double> values = new ArrayList<>();
        for (String key : transactions.keySet()) {
            for (Transaction t : transactions.get(key)) {
                if (t.getSenderIBAN().equals(account.getIBAN())) { // TODO : reset this to Main.getCurrentAccount().getIBAN()
                    value -= t.getAmount();
                } else {
                    value += t.getAmount();
                }
            }
            values.add(value);
        }
        return values;
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
     * @param account      The account we track the history from
     * @return A list of values that represent the amounts of money held by an account that has the given initial value
     * of money as of today, evaluated every day/week/month/year (according to the given timespan)
     */
    public ArrayList<Double> computeValuesHistoryProcess(double initialValue, ArrayList<Transaction> history, Timespan timeSpan, SubAccount account) {
        ArrayList<Double> valuesHistory;
        HashMap<String, ArrayList<Transaction>> transactionsSeparated = separateTransactionsIntoHashMap(history, timeSpan);
        HashMap<String, ArrayList<Transaction>> comprehensiveData = prepareComprehensiveTransactionsHashMap(transactionsSeparated, timeSpan);
        valuesHistory = computeValuesHistory(initialValue, comprehensiveData, account);
        return valuesHistory;
    }

    public static class StringDateParser {
        public static int getDayFromString(String date) {
            try {
                LocalDate asDateObject = LocalDate.parse(date);
                return asDateObject.getDayOfMonth();
            } catch (DateTimeParseException e) {
                if (date.matches("^\\d{2}-\\d{2}-\\d{4}")) {
                    return getDayFromString(date.substring(6) + "-" + date.substring(3, 5) + "-" + date.substring(0, 2));
                }
                return -1;
            }
        }

        public static int getMonthFromString(String date) {
            try {
                LocalDate asDateObject = LocalDate.parse(date);
                return asDateObject.getMonth().getValue();
            } catch (DateTimeParseException e) {
                if (date.matches("^\\d{2}-\\d{2}-\\d{4}")) {
                    return getMonthFromString(date.substring(6) + "-" + date.substring(3, 5) + "-" + date.substring(0, 2));
                }
                return -1;
            }
        }

        public static int getYearFromString(String date) {
            try {
                LocalDate asDateObject = LocalDate.parse(date);
                return asDateObject.getYear();
            } catch (DateTimeParseException e) {
                if (date.matches("^\\d{2}-\\d{2}-\\d{4}")) {
                    return getYearFromString(date.substring(6) + "-" + date.substring(3, 5) + "-" + date.substring(0, 2));
                }
                return -1;
            }
        }

        public static int getYearWeekFromString(String date) {
            try {
                LocalDate asDateObject = LocalDate.parse(date);
                return asDateObject.getDayOfYear() / 7;
            } catch (DateTimeParseException e) {
                if (date.matches("^\\d{2}-\\d{2}-\\d{4}")) {
                    return getYearWeekFromString(date.substring(6) + "-" + date.substring(3, 5) + "-" + date.substring(0, 2));
                }
                return -1;
            }
        }

        public static int getDayOfYearFromString(String date) {
            try {
                LocalDate asDateObject = LocalDate.parse(date);
                return asDateObject.getDayOfYear();
            } catch (DateTimeParseException e) {
                if (date.matches("^\\d{2}-\\d{2}-\\d{4}")) {
                    return getDayOfYearFromString(date.substring(6) + "-" + date.substring(3, 5) + "-" + date.substring(0, 2));
                }
                return -1;
            }
        }
    }
}
