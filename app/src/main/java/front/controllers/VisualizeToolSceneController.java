package front.controllers;

import app.Main;
import back.Timespan;
import back.user.Account;
import back.user.Wallet;
import front.navigation.Flow;
import front.navigation.navigators.BackButtonNavigator;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.chart.PieChart;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;

import java.util.ArrayList;
import java.util.Arrays;

public class VisualizeToolSceneController extends Controller implements BackButtonNavigator {
    @FXML
    public Button backButton, setTableMode, setGraphMode, setPieChartMode, addAccountButton, removeAccountButton;
    @FXML
    public ComboBox<Timespan> timeSpanComboBox;
    @FXML
    public ListView ChartsArea;  // TODO : this is just a reserved area for visualisation purposes
    @FXML
    public ListView<Account> availableAccountsListView, addedAccountsListView;
    @FXML
    public Label availableAccountsLabel, addedAccountsLabel;
    @FXML
    public AnchorPane chartsPane;

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
        pieChartData.add(new PieChart.Data("test0", 12));
        pieChartData.add(new PieChart.Data("test1", 20));
        pieChart = new PieChart(pieChartData);
        // TODO : center the chart
        // TODO can we not just put it in the fxml ?
        pieChart.setTitle("Data");
        chartsPane.getChildren().add(pieChart);
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
        // pieChart.setVisible(true);
    }

    @FXML
    public void handleAddAccountButtonClicked(MouseEvent event) {
        if (availableAccountsListView.getSelectionModel().getSelectedItems().size() > 0) {
            addedAccountsListView.getItems().addAll(availableAccountsListView.getSelectionModel().getSelectedItems());
            availableAccountsListView.getItems().removeAll(addedAccountsListView.getItems());
            // TODO : update graphs ?
        }
    }

    @FXML
    public void handleRemoveAccountModeButtonClicked(MouseEvent event) {
        if (addedAccountsListView.getSelectionModel().getSelectedItems().size() > 0) {
            availableAccountsListView.getItems().addAll(addedAccountsListView.getSelectionModel().getSelectedItems());
            addedAccountsListView.getItems().removeAll(availableAccountsListView.getItems());
            // TODO : update graphs ?
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
}
