package front.controllers;

import app.Main;
import back.Timespan;
import back.user.Account;
import front.navigation.Flow;
import front.navigation.navigators.BackButtonNavigator;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;

import java.util.Arrays;

public class VisualizeToolSceneController extends Controller implements BackButtonNavigator {
    @FXML
    public Button backButton, setTableMode, setGraphMode, setPieChartMode, addAccountButton, removeAccountButton;
    @FXML
    public ComboBox<Timespan> timeSpanComboBox;
    @FXML
    public ListView ChartsArea;  // TODO : this is just a reserved area for visualisation purposes
    @FXML
    public ListView<Account> accountsArea;
    @FXML
    public Label accountsLabel;

    public void initialize() {
        ObservableList<Timespan> timespanValues = FXCollections.observableArrayList(Arrays.asList(Timespan.DAILY, Timespan.WEEKLY, Timespan.MONTHLY, Timespan.YEARLY));
        timeSpanComboBox.setItems(timespanValues);
        timeSpanComboBox.setValue(Timespan.DAILY);
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
        // TODO : change to pie chart mode
    }

    @FXML
    public void handleAddAccountButtonClicked(MouseEvent event) {
        // TODO : add account to the visualisation
    }

    @FXML
    public void handleRemoveAccountModeButtonClicked(MouseEvent event) {
        // TODO : remove account from the visualisation
    }

    @FXML
    public void handleComponentKeyReleased(KeyEvent event) {
        if (event.getCode() == KeyCode.ESCAPE) {
            emulateBackButtonClicked();
            event.consume();
        }
    }
}
