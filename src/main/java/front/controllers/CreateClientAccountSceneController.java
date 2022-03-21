package front.controllers;

import app.Main;
import back.user.AccountType;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import front.animation.FadeInTransition;
import front.animation.threads.FadeOutThread;
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
import javafx.util.Duration;

public class CreateClientAccountSceneController extends Controller implements BackButtonNavigator {
    @FXML
    public Button backButton, createAccountButton;
    @FXML
    public Label enterIBANLabel, accountCreatedLabel, chooseAccountTypeLabel, invalidIBANLabel, noValueSelectedLabel;
    @FXML
    public TextField IBANTextField;
    @FXML
    public ComboBox<AccountType> accountTypeComboBox;

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
        return (IBAN.matches("^[a-zA-Z0-9]*$")) && IBAN.length() == 16;  // IBAN.length() == 16 already checks IBAN != ""
        // TODO : Retirer commentaires
//        for (int i = 0; i < IBAN.length(); i++) {
//            switch (i) {
//                case 0:
//                case 1:
//                    if (!Character.isAlphabetic(IBAN.charAt(i))) return false;
//                    break;
//                default:
//                    if (!Character.isDigit(IBAN.charAt(i))) return false;
//            }
//        }
    }

    public void initialize() {
        accountTypeComboBox.setItems(FXCollections.observableArrayList(AccountType.COURANT, AccountType.EPARGNE, AccountType.TERME, AccountType.JEUNE));
    }

    @FXML
    public void handleBackButtonClicked(MouseEvent event) {
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
    public void handleComponentKeyReleased(KeyEvent keyEvent) {
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
    public void handleCreateAccountButtonClicked(MouseEvent event) {
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

        if (!invalidIBANLabel.isVisible() && !isIBANTaken(IBAN) && !noValueSelectedLabel.isVisible()) {
            // Creates account
            String swift = Main.getBank().getSwiftCode();
            Unirest.setTimeouts(0, 0);
            HttpResponse<String> response = null;
            try {
                response = Unirest.post("https://flns-spring-test.herokuapp.com/api/account")
                        .header("Authorization", "Bearer " + Main.getToken())
                        .header("Content-Type", "application/json")
                        .body("{\r\n    \"iban\": \"" + IBAN + "\",\r\n    \"swift\": \"" + swift + "\",\r\n    \"userId\": \"" + Main.getCurrentWallet().getAccountUser().getNationalRegistrationNumber() + "\",\r\n    \"payment\": false,\r\n    \"accountTypeId\": " + repType + "\r\n}")
                        .asString();
            } catch (UnirestException e) {
                e.printStackTrace();
            }

            // Create account access
            Unirest.setTimeouts(0, 0);
            HttpResponse<String> response2 = null;
            try {
                response2 = Unirest.post("https://flns-spring-test.herokuapp.com/api/account-access")
                        .header("Authorization", "Bearer " + Main.getToken())
                        .header("Content-Type", "application/json")
                        .body("{\r\n    \"accountId\": \"" + IBAN + "\",\r\n    \"userId\": \"" + Main.getCurrentWallet().getAccountUser().getNationalRegistrationNumber() + "\",\r\n    \"access\": true,\r\n    \"hidden\": false\r\n}")
                        .asString();
            } catch (UnirestException e) {
                e.printStackTrace();
            }


            int fadeInDuration = 1000;
            int fadeOutDuration = fadeInDuration;
            int sleepDuration = 1000;
            FadeOutThread sleepAndFadeOutAccountCreatedLabelFadeThread;
            FadeInTransition.playFromStartOn(accountCreatedLabel, Duration.millis(fadeInDuration));
            sleepAndFadeOutAccountCreatedLabelFadeThread = new FadeOutThread();
            sleepAndFadeOutAccountCreatedLabelFadeThread.start(fadeOutDuration, sleepDuration + fadeInDuration, accountCreatedLabel);
            accountCreated = true;

            // Reset the form
            IBANTextField.setText("");
            accountTypeComboBox.valueProperty().set(null);
        }
    }

    public boolean isIBANTaken(String IBAN) {
        // TODO : back-end : implement method that checks if IBAN is already taken
        return false;
    }

    @FXML
    public void handleIBANTextFieldKeyPressed() {
        accountCreated = false;
    }

    @FXML
    public void handleAccountTypeComboBoxKeyPressed() {
        accountCreated = false;
    }
}