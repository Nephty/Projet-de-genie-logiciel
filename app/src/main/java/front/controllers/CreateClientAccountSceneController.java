package front.controllers;

import app.Main;
import back.user.AccountType;
import back.user.ErrorHandler;
import back.user.Notification;
import back.user.Request;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import front.navigation.Flow;
import front.navigation.navigators.BackButtonNavigator;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;

public class CreateClientAccountSceneController extends Controller implements BackButtonNavigator {
    @FXML
    Button backButton, createAccountButton;
    @FXML
    Label enterIBANLabel, accountCreatedLabel, chooseAccountTypeLabel, invalidIBANLabel, noValueSelectedLabel;
    @FXML
    TextField IBANTextField;
    @FXML
    ComboBox<AccountType> accountTypeComboBox;

    private boolean accountCreated = false;

    /**
     * Checks if the given <code>String</code> is a valid IBAN.
     * Requirements :
     * - string must not be empty
     * - string must not be null
     * - string must only contain characters from a-z, from A-Z and from 0-9
     * - string must follow this format : AAXXXXXXXXXXXXXX where A is a letter and X is a digit
     *
     * @param IBAN - <code>String</code> - the IBAN to check
     * @return <code>boolean</code> - whether the given IBAN is a valid IBAN or not
     */
    public static boolean isValidIBAN(String IBAN) {
        if (IBAN == null) return false;
        if (!IBAN.matches("^[a-zA-Z0-9]*$") || IBAN.length() != 16)
            return false;  // IBAN.length() == 16 already checks IBAN != ""
        for (int i = 0; i < IBAN.length(); i++) {
            switch (i) {
                case 0:
                case 1:
                    if (!Character.isAlphabetic(IBAN.charAt(i))) return false;
                    break;
                default:
                    if (!Character.isDigit(IBAN.charAt(i))) return false;
            }
        }
        return true;
    }

    public void initialize() {
        accountTypeComboBox.setItems(FXCollections.observableArrayList(AccountType.COURANT, AccountType.EPARGNE, AccountType.TERME, AccountType.JEUNE));
    }

    @FXML
    void handleBackButtonClicked(MouseEvent event) {
        handleBackButtonNavigation(event);
        if (accountCreated) {
            IBANTextField.setText("");
            accountTypeComboBox.valueProperty().set(null);
            accountCreated = false;
        }
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
    void handleComponentKeyReleased(KeyEvent keyEvent) {
        if (keyEvent.getCode() == KeyCode.ESCAPE) {
            emulateBackButtonClicked();
            keyEvent.consume();
        } else if (keyEvent.getCode() == KeyCode.ENTER) {
            emulateCreateAccountButtonClicked();
            keyEvent.consume();
        }
    }

    private void emulateCreateAccountButtonClicked() {
        handleCreateAccountButtonClicked(null);
    }

    @FXML
    void handleCreateAccountButtonClicked(MouseEvent event) {
        String IBAN = IBANTextField.getText();

        if (!invalidIBANLabel.isVisible() && !isValidIBAN(IBAN)) invalidIBANLabel.setVisible(true);
        else if (invalidIBANLabel.isVisible() && isValidIBAN(IBAN)) invalidIBANLabel.setVisible(false);

        if (!noValueSelectedLabel.isVisible() && accountTypeComboBox.getValue() == null)
            noValueSelectedLabel.setVisible(true);
        if (noValueSelectedLabel.isVisible() && accountTypeComboBox.getValue() != null)
            noValueSelectedLabel.setVisible(false);

        AccountType accountType = accountTypeComboBox.getValue();
        int repType = 0;
        switch (accountType) {
            case COURANT:
                repType = 1;
                break;
            case JEUNE:
                repType = 2;
                break;
            case EPARGNE:
                repType = 3;
                break;
            case TERME:
                repType = 4;
                break;
        }

        if (!invalidIBANLabel.isVisible() && !noValueSelectedLabel.isVisible()) {
            // Creates account with all the values
            String swift = Main.getBank().getSwiftCode();

            String userId;
            if (!Main.getNewClient().equals(null)) {
                userId = Main.getNewClient();
            } else {
                userId = Main.getCurrentWallet().getAccountUser().getNationalRegistrationNumber();
            }
            Unirest.setTimeouts(0, 0);
            int finalRepType = repType;
            HttpResponse<String> response = ErrorHandler.handlePossibleError(() -> {
                HttpResponse<String> rep = null;
                try {
                    rep = Unirest.post("https://flns-spring-test.herokuapp.com/api/account")
                            .header("Authorization", "Bearer " + Main.getToken())
                            .header("Content-Type", "application/json")
                            .body("{\r\n    \"iban\": \"" + IBAN + "\",\r\n    \"swift\": \"" + swift + "\",\r\n    \"userId\": \"" + userId + "\",\r\n    \"accountTypeId\": "+finalRepType+"\r\n}")
                            .asString();
                } catch (UnirestException e) {
                    throw new RuntimeException(e);
                }
                return rep;
            });
            // TODO : A vérifier
            if(response.getStatus() == 409){
                if(response.getBody().equals("IBAN")){
                    invalidIBANLabel.setVisible(true);
                }
            } else{
                Main.errorCheck(response.getStatus());

                // Create account access
                Unirest.setTimeouts(0, 0);
                HttpResponse<String> response2 = ErrorHandler.handlePossibleError(() -> {
                    HttpResponse<String> rep = null;
                    try {
                        rep = Unirest.post("https://flns-spring-test.herokuapp.com/api/account-access")
                                .header("Authorization", "Bearer " + Main.getToken())
                                .header("Content-Type", "application/json")
                                .body("{\r\n    \"accountId\": \"" + IBAN + "\",\r\n    \"userId\": \"" + userId + "\",\r\n    \"access\": true,\r\n    \"hidden\": false\r\n}")
                                .asString();
                    } catch (UnirestException e) {
                        throw new RuntimeException(e);
                    }
                    return rep;
                });
                Main.errorCheck(response2.getStatus());

                Main.setNewClient(null);

                if(!Main.getRequest().equals(null)){
                    Request request = Main.getRequest();

                    if(response.getStatus() == 201 && response2.getStatus() == 201){
                        // Send a notification to the client
                        request.sendNotif("has created you an new account");

                        // Delete this request
                        Unirest.setTimeouts(0, 0);
                        HttpResponse<String> response3 = ErrorHandler.handlePossibleError(() -> {
                            HttpResponse<String> rep = null;
                            try {
                                rep = Unirest.delete("https://flns-spring-test.herokuapp.com/api/notification/"+ request.getID())
                                        .header("Authorization", "Bearer "+ Main.getToken())
                                        .asString();
                            } catch (UnirestException e) {
                                throw new RuntimeException(e);
                            }
                            return rep;
                        });
                    }
                }

                Main.setRequest(null);

                fadeInAndOutNode(1000, accountCreatedLabel);
                accountCreated = true;

                // Reset the form
                IBANTextField.setText("");
                accountTypeComboBox.valueProperty().set(null);
            }
        }
    }

    @FXML
    void handleIBANTextFieldKeyPressed() {
        accountCreated = false;
    }

    @FXML
    void handleAccountTypeComboBoxKeyPressed() {
        accountCreated = false;
    }
}
