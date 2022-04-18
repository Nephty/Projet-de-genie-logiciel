package front.controllers;

import app.Main;
import back.Timespan;
import back.user.Account;
import back.user.SubAccount;
import back.user.Transaction;
import back.user.Wallet;
import front.navigation.Flow;
import front.navigation.navigators.BackButtonNavigator;
import front.scenes.Scenes;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.chart.*;
import javafx.scene.chart.PieChart.Data;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.StackPane;

import java.sql.Date;
import java.time.Instant;
import java.time.LocalDate;
import java.time.Period;
import java.time.format.DateTimeParseException;
import java.util.*;

/**
 * @author Arnaud MOREAU
 */
public class VisualizeToolSceneController extends Controller implements BackButtonNavigator {
    @FXML
    Button backButton, setGraphMode, setPieChartMode, addAccountButton, removeAccountButton, exportHistoryButton;
    @FXML
    ComboBox<Timespan> timeSpanComboBox;
    @FXML
    TableView<SubAccount> availableAccountsTableView, addedAccountsTableView;
    @FXML
    Label availableAccountsLabel, addedAccountsLabel;
    @FXML
    StackPane chartsPane;
    @FXML
    PieChart pieChart;
    @FXML
    StackedAreaChart stackedAreaChart;
    @FXML
    CategoryAxis bottomAxis;
    @FXML
    NumberAxis leftAxis;
    @FXML
    TableColumn<SubAccount, String> availableIBANColumn, availableAmountColumn, addedIBANColumn, addedAmountColumn;

    private ObservableList<PieChart.Data> pieChartData = FXCollections.observableArrayList();
    private ObservableList<XYChart.Series<String, Double>> stackedAreaChartData = FXCollections.observableArrayList();
    ArrayList<Double> valuesHistory;

    public void initialize() {
        availableAccountsTableView.setPlaceholder(new Label("No account available."));
        addedAccountsTableView.setPlaceholder(new Label("No account added."));
        availableAccountsTableView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        addedAccountsTableView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        availableIBANColumn.setCellValueFactory(sa -> new SimpleStringProperty(sa.getValue().getIBAN()));
        availableAmountColumn.setCellValueFactory(sa -> new SimpleStringProperty(String.valueOf(sa.getValue().getAmount())));
        addedIBANColumn.setCellValueFactory(sa -> new SimpleStringProperty(sa.getValue().getIBAN()));
        addedAmountColumn.setCellValueFactory(sa -> new SimpleStringProperty(String.valueOf(sa.getValue().getAmount())));

        ObservableList<Timespan> timespanValues = FXCollections.observableArrayList(Arrays.asList(Timespan.DAILY, Timespan.WEEKLY, Timespan.MONTHLY, Timespan.YEARLY));
        timeSpanComboBox.setItems(timespanValues);
        timeSpanComboBox.setValue(Timespan.DAILY);

        ArrayList<SubAccount> subAccounts = new ArrayList<>();
        for (Wallet wallet : Main.getPortfolio().getWalletList()) {
            for (Account account : wallet.getAccountList()) {
                subAccounts.addAll(account.getSubAccountList());
            }
        }
        availableAccountsTableView.setItems(FXCollections.observableArrayList(subAccounts));

        // Add a listener that checks for value change in the combo box to reset the stacked area chart
        timeSpanComboBox.valueProperty().addListener((observable, oldValue, newValue) -> {
            stackedAreaChartData = FXCollections.observableArrayList();
            stackedAreaChart.setData(stackedAreaChartData);
            for (SubAccount subAccount : addedAccountsTableView.getItems()) {
                addAccountToStackedAreaChartData(subAccount, timeSpanComboBox.getValue());
            }
        });
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
    void handleBackButtonClicked(MouseEvent event) {
        handleBackButtonNavigation(event);
    }

    @FXML
    void handleSetGraphModeButtonClicked(MouseEvent event) {
        pieChart.setVisible(false);
        stackedAreaChart.setVisible(true);
    }

    @FXML
    void handleSetPieChartModeButtonClicked(MouseEvent event) {
        stackedAreaChart.setVisible(false);
        pieChart.setVisible(true);
    }

    @FXML
    void handleAddAccountButtonClicked(MouseEvent event) {
        if (availableAccountsTableView.getSelectionModel().getSelectedItems().size() > 0) {
            // Prepare selected data for utilization
            ObservableList<SubAccount> selection = availableAccountsTableView.getSelectionModel().getSelectedItems();

            // Copy accounts to the added accounts listview
            addedAccountsTableView.getItems().addAll(selection);

            // Update pie chart
            for (SubAccount subAccount : selection) {
                double amount = subAccount.getAmount();
                if (amount == 0) amount = 0.01; // very small amount so that it still appears on the chart
                pieChartData.add(new Data(subAccount.getIBAN(), amount));
            }
            pieChart.setData(pieChartData);

            // Update stacked area chart
            for (SubAccount subAccount : selection) {
                addAccountToStackedAreaChartData(subAccount, timeSpanComboBox.getValue());
            }
            stackedAreaChart.setData(stackedAreaChartData);

            // Remove accounts from the available accounts listview
            availableAccountsTableView.getItems().removeAll(selection);
        }
    }

    @FXML
    void handleRemoveAccountModeButtonClicked(MouseEvent event) {
        if (addedAccountsTableView.getSelectionModel().getSelectedItems().size() > 0) {
            ObservableList<SubAccount> selection = addedAccountsTableView.getSelectionModel().getSelectedItems();
            availableAccountsTableView.getItems().addAll(selection);

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

            // Update stacked area chart
            ArrayList<XYChart.Series> toRemove = new ArrayList<>();
            for (SubAccount subAccount : selection) {
                for (XYChart.Series series : stackedAreaChartData) {
                    if (series.getName().equals(subAccount.getIBAN())) {
                        toRemove.add(series);
                    }
                }
            }
            stackedAreaChartData.removeAll(toRemove);
            stackedAreaChart.setData(stackedAreaChartData);

            addedAccountsTableView.getItems().removeAll(selection);
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
    void handleAvailableAccountsListViewMouseClicked(MouseEvent mouseEvent) {
        removeAccountButton.setDisable(true);
        addAccountButton.setDisable(false);
    }

    @FXML
    void handleAddedAccountsListViewMouseClicked(MouseEvent mouseEvent) {
        removeAccountButton.setDisable(false);
        addAccountButton.setDisable(true);
    }


    /**
     * Using a timespan, adds the given subAccount to the stacked area chart data for visualization purposes. Prepares
     * a date's arraylist of strings that will be used as a new x-axis and creates a new series of data for the chart.
     * Adds the newly created series to the data. The chart is not actively being updated : we must do
     * [chart].setData([data])
     *
     * @param subAccount The account to add to the data
     * @param timeSpan   The timespan to follow
     */
    public void addAccountToStackedAreaChartData(SubAccount subAccount, Timespan timeSpan) {
        ArrayList<String> dates = new ArrayList<>();
        XYChart.Series<String, Double> data = new XYChart.Series<>();
        data.setName(subAccount.getIBAN());
        valuesHistory = computeValuesHistoryProcess(subAccount, timeSpanComboBox.getValue());
        switch (timeSpan) {
            case DAILY:
                for (int i = 0; i < 30; i++) {
                    LocalDate datePoint = LocalDate.now().minus(Period.ofDays(29 - i));
                    dates.add(datePoint.toString());
                    XYChart.Data<String, Double> dataPoint = new XYChart.Data<>(datePoint.toString(), valuesHistory.get(29 - i));
                    data.getData().add(dataPoint);
                }
                break;
            case WEEKLY:
                for (int i = 0; i < 8; i++) {
                    LocalDate datePoint = LocalDate.now().minus(Period.ofWeeks(7 - i));
                    dates.add(datePoint.toString());
                    XYChart.Data<String, Double> dataPoint = new XYChart.Data<>(datePoint.toString(), valuesHistory.get(7 - i));
                    data.getData().add(dataPoint);
                }
                break;
            case MONTHLY:
                for (int i = 0; i < 6; i++) {
                    LocalDate datePoint = LocalDate.now().minus(Period.ofMonths(5 - i));
                    dates.add(datePoint.toString());
                    XYChart.Data<String, Double> dataPoint = new XYChart.Data<>(datePoint.toString(), valuesHistory.get(5 - i));
                    data.getData().add(dataPoint);
                }
                break;
            case YEARLY:
                for (int i = 0; i < 4; i++) {
                    LocalDate datePoint = LocalDate.now().minus(Period.ofYears(3 - i));
                    dates.add(datePoint.toString());
                    XYChart.Data<String, Double> dataPoint = new XYChart.Data<>(datePoint.toString(), valuesHistory.get(3 - i));
                    data.getData().add(dataPoint);
                }
                break;
        }
        bottomAxis.setCategories(FXCollections.observableArrayList(dates));
        stackedAreaChartData.add(data);
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

        boolean canBreak = false;
        for (Transaction t : history) {
            // for each transaction, if they are in the date range, keep it in a list to use them to compute the amount
            // of money the account had at a particular time stamp
            long sendingDateToMillis = t.getSendingDateAsDateObject().toInstant().toEpochMilli();
            switch (timeSpan) {
                case DAILY:
                    // last 30 days
                    if (today - sendingDateToMillis < _30_DAYS_IN_MILLISECONDS) toApply.add(t);
                    else canBreak = true;  // We are too far back in time, no need to check for the next transactions
                    break;
                case WEEKLY:
                    // last 8 weeks
                    if (today - sendingDateToMillis < _8_WEEKS_IN_MILLISECONDS) toApply.add(t);
                    else canBreak = true;  // We are too far back in time, no need to check for the next transactions
                    break;
                case MONTHLY:
                    // last 6 months
                    if (today - sendingDateToMillis < _6_MONTHS_IN_MILLISECONDS) toApply.add(t);
                    else canBreak = true;  // We are too far back in time, no need to check for the next transactions
                    break;
                case YEARLY:
                    // last 4 years
                    if (today - sendingDateToMillis < _4_YEARS_IN_MILLISECONDS) toApply.add(t);
                    else canBreak = true;  // We are too far back in time, no need to check for the next transactions
                    break;
            }
            if (canBreak) break;
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
                LocalDate date4YearsAgo = LocalDate.now().minus(Period.ofYears(yearsSpan - 1));
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
     * by the HashMap that matches strings to list of transactions. These lists of transactions must reflect the intervals
     * of time. For example, the lists of transactions are the transactions that happened every day for 30 days. If so,
     * the returned arraylist will contain the holding history of an account recorded every day for 30 days.
     *
     * @param initialValue The initial amount of money on the account
     * @param transactions A hashmap that maps dates to a list of transactions (every entry in the hashmap is regularly
     *                     spaced one from another
     * @param account      The account from which we track the history
     * @return An arraylist containing the holding's history at every regular timestamp
     */
    public ArrayList<Double> computeValuesHistory(double initialValue, HashMap<String, ArrayList<Transaction>> transactions, SubAccount account) {
        double value = initialValue;
        ArrayList<Double> values = new ArrayList<>();
        Set<String> keySet = transactions.keySet();
        ArrayList<String> sortedKeySet = reverseSortStringNaturalsKeySet(keySet);
        values.add(value);
        for (int i = 0; i < sortedKeySet.size() - 1; i++) {
            for (Transaction t : transactions.get(sortedKeySet.get(i))) {
                if (t.getSenderIBAN().equals(account.getIBAN())) value += t.getAmount();
                else value -= t.getAmount();
            }
            values.add(value);
        }
        return values;
    }

    /**
     * Sorts a set of string keys in cyclic order. Entries must be integer numbers contained in strings that represent
     * days, weeks or months. Entries must follow a sequence ("3", "4", "5", "6"...) but they can be cyclic, that is
     * entries could be "1", "2", "3", "4", "49", "50", "51", "52" and cycle back to 1 after 52 is reached.
     * After sorting and before returning, the list is reversed.
     *
     * @param keySet The set to order
     * @return An inverted ordered copy of the original set
     */
    private ArrayList<String> reverseSortStringNaturalsKeySet(Set<String> keySet) {
        ArrayList<String> newKeySet = new ArrayList<>(keySet);
        newKeySet.sort(Comparator.comparingInt(Integer::parseInt));

        // If the set is in the monthly timespan and the first element is 0, move it to the front of the set
        // We must always do that because December must always be the last element and the integer comparator will
        // always put it first in the list.
        if (newKeySet.size() == 6 && newKeySet.get(0).equals("0")) {
            newKeySet.remove(0);
            newKeySet.add("0");
        }
        // If the size of the set is 6 and the last is 0, then there is a gap in the monthly timespan.
        // In all cases where a gap occur in the monthly timespan, 0 is the last entry.
        // Case where we need to put some months behind
        // To know how many elements we need to move, we use a central symmetry, it is easy to see why.
        // Taking the case of the weekly timespan :
        // Low bound = 5 --> Elements to move = 1
        // Low bound = 4 --> Elements to move = 2
        // Low bound = 3 --> Elements to move = 3
        // Low bound = 2 --> Elements to move = 4
        // Low bound = 1 --> Elements to move = 5
        // This is a central symmetry where the center is 3
        // Thus, the mathematical function that takes the low bound in input and returns the number of elements to move
        // is : f(x) = x - 2 ( x - center ) <=> f(x) = - x + 2 * center <=> f(x) = - x + size_of_the_list
        String gapLowBound = identifyGapLowBound(newKeySet, newKeySet.size() - 1);
        int numberOfEntriesToMove;
        if (!gapLowBound.equals("")) {
            numberOfEntriesToMove = newKeySet.size() - Integer.parseInt(gapLowBound);
            // For the moving operation, we convert the arraylist to a linked list since moving the last element of an
            // arraylist to the beginning of this arraylist is a very expensive operation.
            // Using linked lists, this operation is much cheaper
            LinkedList<String> newKeySetAsLinkedList = new LinkedList<>(newKeySet);
            for (int i = 0; i < numberOfEntriesToMove; i++) {
                String elementToMove = newKeySetAsLinkedList.get(newKeySetAsLinkedList.size() - 1);
                newKeySetAsLinkedList.addFirst(elementToMove);
                newKeySetAsLinkedList.removeLast();
            }
            Collections.reverse(newKeySetAsLinkedList);
            return new ArrayList<>(newKeySetAsLinkedList);
        }
        Collections.reverse(newKeySet);
        return newKeySet;
    }

    /**
     * Looks at the last element of the given arraylist and checks if this value is below a certain threshold.
     * If so, returns the said element, if not, removes it from a copy of the list and calls the method recursively
     * on the new shrunk list.
     * For example, in the monthly timespan, if the last element is <= 4, then this is the low bound of the gap, because
     * all possible gaped sets with their gap highlighted and their low bound are :
     * [1, 2, 3, 4, *5, 0*]     --> 5
     * [1, 2, 3, *4, 11*, 0]    --> 4
     * [1, 2, *3, 10*, 11, 0]   --> 3
     * [1, *2, 9*, 10, 11, 0]   --> 2
     * [*1, 8*, 9, 10, 11, 0]   --> 1
     * To get the maximum value of the low bound, we only need to know the size of the list since the worst case is
     * the case where the last element is the only one "gaped" from the others. This means that subtracting 1 from
     * the size of the list gives us the maximum value of the low bound.
     * WARNING : we cannot use this method on yearly timespan. This timespan isn't cyclic, whereas daily, weekly & monthly
     * timespan are cyclic.
     *
     * @param newKeySet The list of elements which we need to find the low gap value of. This set MUST come from the
     *                  method <code>sortStringNaturalsKeySet</code> and be sorted using the comparingInt method
     *                  from Comparator.
     * @return The value of the low bound of the gap. See method explanation to know what this value is.
     */
    private String identifyGapLowBound(ArrayList<String> newKeySet, int maxValueForLowBound) {
        ArrayList<String> copy = (ArrayList<String>) newKeySet.clone();
        if (newKeySet.size() == 0) return "";
        String lastElementOfCopy = copy.get(copy.size() - 1);
        if (Integer.parseInt(lastElementOfCopy) <= maxValueForLowBound && !lastElementOfCopy.equals("0")) return lastElementOfCopy;
        else {
            copy.remove(copy.size() - 1);
            return identifyGapLowBound(copy, maxValueForLowBound);
        }
    }

    /**
     * Using an initial value, a list of transactions and a timespan, returns a list of values that correspond to the
     * amount of money that was held by an account periodically (according to the timespan). If we give an initial value
     * of 50, a list of transactions and a timespan of DAILY, returns a list of values that correspond to the amounts of
     * money held by an account that has 50 of any currency as of today, evaluated every day (that is, one day will
     * separate every value). If the timespan is WEEKLY, MONTHLY or YEARLY, every value will have a one week, month or
     * year interval respectively.
     *
     * @param timeSpan The timespan that has to separate every value (one day, one week, one month or one year)
     * @param account  The account we track the history from
     * @return A list of values that represent the amounts of money held by an account that has the given initial value
     * of money as of today, evaluated every day/week/month/year (according to the given timespan)
     */
    public ArrayList<Double> computeValuesHistoryProcess(SubAccount account, Timespan timeSpan) {
        double initialValue = account.getAmount();
        ArrayList<Transaction> history = account.getTransactionHistory();
        history.sort(Comparator.comparing(t -> Date.valueOf(t.getSendingDate())));
        HashMap<String, ArrayList<Transaction>> transactionsSeparated = separateTransactionsIntoHashMap(history, timeSpan);
        HashMap<String, ArrayList<Transaction>> comprehensiveData = prepareComprehensiveTransactionsHashMap(transactionsSeparated, timeSpan);
        valuesHistory = computeValuesHistory(initialValue, comprehensiveData, account);
        return valuesHistory;
    }

    public void handleExportHistoryButtonClicked(MouseEvent event) {
        if (addedAccountsTableView.getItems().size() > 0) {
            ExportHistorySceneController.exportData = new ArrayList<>();
            // today's date as milliseconds since epoch
            long today = Instant.now().toEpochMilli();
            // 1 day in milliseconds : 86.400.000ms
            long _1_DAY_IN_MILLISECONDS = 86400000L;
            long _30_DAYS_IN_MILLISECONDS = 30 * _1_DAY_IN_MILLISECONDS;
            // 8 weeks = 56 days
            long _8_WEEKS_IN_MILLISECONDS = 56 * _1_DAY_IN_MILLISECONDS;
            // 6 months = 182 days
            long _6_MONTHS_IN_MILLISECONDS = 182 * _1_DAY_IN_MILLISECONDS;
            // 4 years = 1461 days
            long _4_YEARS_IN_MILLISECONDS = 1461 * _1_DAY_IN_MILLISECONDS;
            for (SubAccount subAccount : addedAccountsTableView.getItems()) {
                for (Transaction t : subAccount.getTransactionHistory()) {
                    long sendingDateToMillis = t.getSendingDateAsDateObject().toInstant().toEpochMilli();
                    switch (timeSpanComboBox.getValue()) {
                        case DAILY:
                            // last 30 days
                            if (today - sendingDateToMillis < _30_DAYS_IN_MILLISECONDS) ExportHistorySceneController.exportData.add(t);
                            break;
                        case WEEKLY:
                            // last 8 weeks
                            if (today - sendingDateToMillis < _8_WEEKS_IN_MILLISECONDS) ExportHistorySceneController.exportData.add(t);
                            break;
                        case MONTHLY:
                            // last 6 months
                            if (today - sendingDateToMillis < _6_MONTHS_IN_MILLISECONDS) ExportHistorySceneController.exportData.add(t);
                            break;
                        case YEARLY:
                            // last 4 years
                            if (today - sendingDateToMillis < _4_YEARS_IN_MILLISECONDS) ExportHistorySceneController.exportData.add(t);
                            break;
                    }
                }
            }
            Main.setScene(Flow.forward(Scenes.ExportHistoryScene));
        }
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
