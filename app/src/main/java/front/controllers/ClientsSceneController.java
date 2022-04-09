package front.controllers;

import app.Main;
import back.user.Profile;
import back.user.Wallet;
import com.mashape.unirest.http.exceptions.UnirestException;
import front.animation.FadeInTransition;
import front.animation.threads.FadeOutThread;
import front.navigation.Flow;
import front.navigation.navigators.BackButtonNavigator;
import front.scenes.SceneLoader;
import front.scenes.Scenes;
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

public class ClientsSceneController extends Controller implements BackButtonNavigator {
    @FXML
    public Button backButton, exportDataButton, addClientButton, detailsButton, searchButton, fetchClientsButton;
    @FXML
    public Label lastUpdateTimeLabel, loadingClientsLabel;
    @FXML
    public ComboBox<String> sortByComboBox;
    @FXML
    public TextField searchTextField;
    @FXML
    public TableView<Profile> clientsTableView;
    TableColumn<Profile, String> firstNameColumn = new TableColumn<>("First name"),
            lastNameColumn = new TableColumn<>("Last name"), NRNColumn = new TableColumn<>("National registration number");

    private ObservableList<Profile> allData = FXCollections.observableArrayList();
    private ObservableList<Profile> currentData = FXCollections.observableArrayList();

    private boolean searched = false;

    public void initialize() {
        firstNameColumn.setCellValueFactory(new PropertyValueFactory<>("firstName"));
        lastNameColumn.setCellValueFactory(new PropertyValueFactory<>("lastName"));
        NRNColumn.setCellValueFactory(new PropertyValueFactory<>("nationalRegistrationNumber"));
        clientsTableView.getColumns().setAll(firstNameColumn, lastNameColumn, NRNColumn);
        fetchClients();
        sortByComboBox.setItems(FXCollections.observableArrayList("user", "id"));
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
        sortByComboBox.valueProperty().set(null);
    }

    @FXML
    public void handleExportDataButtonClicked(MouseEvent event) {
        Main.setScene(Flow.forward(Scenes.ExportDataScene));
        if (clientsTableView.getSelectionModel().getSelectedItems().size() != 0) {
            // If items selected, only send selected items for export
            ExportDataSceneController.setExportData(new ArrayList<>(clientsTableView.getSelectionModel().getSelectedItems()));
        } else {
            // Send all items for export
            ExportDataSceneController.setExportData(new ArrayList<>(clientsTableView.getItems()));
        }
    }

    @FXML
    public void handleAddClientButtonClicked(MouseEvent event) {
        // TODO : Ajouter un client en donnat son numéro puis en switchant sur la scene créer compte pour lui créer un compte
        Main.setScene(Flow.forward(Scenes.AddClientScene));
    }

    @FXML
    public void handleDetailsButtonClicked(MouseEvent event) {
        if (clientsTableView.getSelectionModel().getSelectedItems().size() == 1) {
            try {
                Main.setCurrentWallet(new Wallet(clientsTableView.getSelectionModel().getSelectedItem()));
            } catch (UnirestException e) {
                Main.ErrorManager(408);
            }
            Scenes.ClientDetailsScene = SceneLoader.load("ClientDetailsScene.fxml", Main.appLocale);
            Main.setScene(Flow.forward(Scenes.ClientDetailsScene));
        }
    }

    @FXML
    public void handleSearchTextFieldKeyPressed(KeyEvent keyEvent) {
        if (keyEvent.getCode() == KeyCode.ENTER) {
            emulateSearchButtonClicked();
        } else {
            searched = false;
        }
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
            clientsTableView.setItems(currentData);
            return;
        }
        currentData = FXCollections.observableArrayList();

        for (Profile profile : allData) {
            ArrayList<String> profileParameters = new ArrayList<>(Arrays.asList(profile.getFirstName().toLowerCase(),
                    profile.getLastName().toLowerCase(), profile.getNationalRegistrationNumber().toLowerCase()));
            for (String param : profileParameters) {
                if (param.contains(entry)) {
                    currentData.add(profile);
                    break;
                }
            }
        }
        clientsTableView.setItems(currentData);
    }

    @FXML
    public void handleFetchClientsButtonClicked(MouseEvent event) {
        fetchClients();
    }

    private void fetchClients() {
        if (loadingClientsLabel.getOpacity() == 0.0) {
            int fadeInDuration = 1000;
            int fadeOutDuration = fadeInDuration;
            int sleepDuration = 1000;
            FadeOutThread sleepAndFadeOutLoadingClientsLabelFadeThread;
            FadeInTransition.playFromStartOn(loadingClientsLabel, Duration.millis(fadeInDuration));
            sleepAndFadeOutLoadingClientsLabelFadeThread = new FadeOutThread();
            Calendar c = Calendar.getInstance();
            lastUpdateTimeLabel.setText("Last update : " + formatCurrentTime(c));

            // Fetches the clients
            ArrayList<Profile> customerList = Profile.fetchAllCustomers(Main.getBank().getSwiftCode());
            // Put the client list in a ListView
            clientsTableView.setItems(FXCollections.observableArrayList(customerList));
            allData = clientsTableView.getItems();
            sleepAndFadeOutLoadingClientsLabelFadeThread.start(fadeOutDuration, sleepDuration + fadeInDuration, loadingClientsLabel);
        }
    }
}
