package front.controllers;

import app.Main;
import back.user.ErrorHandler;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import front.navigation.Flow;
import front.navigation.navigators.BackButtonNavigator;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;

import java.util.Calendar;
import java.util.Objects;

/**
 * @author Arnaud MOREAU
 */
public class TransferSceneController extends Controller implements BackButtonNavigator {
    @FXML
    PasswordField passwordField;
    @FXML
    Button backButton, transferButton;
    @FXML
    TextField amountField, recipientField, IBANField, messageField, dateField;
    @FXML
    Label invalidAmountLabel, invalidRecipientLabel, invalidIBAN, invalidMessageLabel, invalidDateLabel,
            transferExecutedLabel, charactersLeftLabel, invalidPasswordLabel, insufficientBalanceLabel;

    private boolean transferExecuted = false;

    @Override
    public void handleBackButtonNavigation(MouseEvent event) {
        Main.setScene(Flow.back());
    }

    @Override
    public void emulateBackButtonClicked() {
        handleBackButtonNavigation(null);
    }

    @FXML
    void handleBackButtonClicked(MouseEvent event) {
        handleBackButtonNavigation(event);
        transferExecutedLabel.setVisible(false);
        if (transferExecuted) clearAllTextFields();
        if (transferExecuted) hideAllLabels();
        transferExecuted = false;
    }

    private void hideAllLabels() {
        invalidPasswordLabel.setVisible(false);
        charactersLeftLabel.setVisible(false);
        invalidAmountLabel.setVisible(false);
        invalidDateLabel.setVisible(false);
        invalidMessageLabel.setVisible(false);
        invalidRecipientLabel.setVisible(false);
        insufficientBalanceLabel.setVisible(false);
    }

    /**
     * Checks if every field is properly filled in. Initializes the transfer process.
     */
    public void transfer() {
        String amount = amountField.getText(), recipient = recipientField.getText(), IBAN = IBANField.getText(),
                message = messageField.getText(), date = dateField.getText(), password = passwordField.getText();

        // Manage the invalid "xxxx" labels visibility
        if (!isValidAmount(amount) && !invalidAmountLabel.isVisible()) invalidAmountLabel.setVisible(true);
        else if (isValidAmount(amount) && invalidAmountLabel.isVisible()) invalidAmountLabel.setVisible(false);
        if (!isValidRecipient(recipient) && !invalidRecipientLabel.isVisible()) invalidRecipientLabel.setVisible(true);
        else if (isValidRecipient(recipient) && invalidRecipientLabel.isVisible())
            invalidRecipientLabel.setVisible(false);
        if (!isValidIBAN(IBAN) && !invalidIBAN.isVisible()) invalidIBAN.setVisible(true);
        else if (isValidIBAN(IBAN) && invalidIBAN.isVisible()) invalidIBAN.setVisible(false);
        if (!isValidMessage(message) && !invalidMessageLabel.isVisible()) invalidMessageLabel.setVisible(true);
        else if (isValidMessage(message) && invalidMessageLabel.isVisible()) invalidMessageLabel.setVisible(false);
        if (!isValidDate(date) && !invalidDateLabel.isVisible()) invalidDateLabel.setVisible(true);
        else if (isValidDate(date) && invalidDateLabel.isVisible()) invalidDateLabel.setVisible(false);
        if (!isValidPassword(password) && !invalidPasswordLabel.isVisible()) invalidPasswordLabel.setVisible(true);
        else if (isValidPassword(password) && invalidPasswordLabel.isVisible()) invalidPasswordLabel.setVisible(false);
        if (!sufficientBalance(amount) && !insufficientBalanceLabel.isVisible()) insufficientBalanceLabel.setVisible(true);
        else if (sufficientBalance(amount) && insufficientBalanceLabel.isVisible()) insufficientBalanceLabel.setVisible(false);

        String recipientActual = Main.getCurrentAccount().getIBAN();
        if (!Objects.equals(recipientActual, IBAN)) {
            if (noLabelVisible()) {
                // Creates the transfer if everything is correct
                if (insufficientBalanceLabel.isVisible()) insufficientBalanceLabel.setVisible(false);
                HttpResponse<String> response = ErrorHandler.handlePossibleError(() -> {
                    HttpResponse<String> rep = null;
                    try {
                        rep = Unirest.post("https://flns-spring-test.herokuapp.com/api/transaction")
                                .header("Authorization", "Bearer " + Main.getToken())
                                .header("Content-Type", "application/json")
                                .body("{\r\n    \"transactionTypeId\": 1,\r\n    \"senderIban\": \"" + recipientActual + "\",\r\n    \"recipientIban\": \"" + IBAN + "\",\r\n    \"currencyId\": 0,\r\n    \"transactionAmount\": " + amount + ",\r\n    \"comments\": \"" + message + "\"\r\n}")
                                .asString();
                    } catch (UnirestException e) {
                        throw new RuntimeException(e);
                    }
                    return rep;
                });

                transferExecuted = true;
                Main.setScene(Flow.back());
                Main.setScene(Flow.back());
                Main.setScene(Flow.back());
                clearAllTextFields();
            }
        }
    }

    private boolean sufficientBalance(String amount) {
        return Main.getCurrentAccount().getSubAccountList().get(0).getAmount() >= Double.parseDouble(amount);
    }

    private boolean isValidPassword(String password) {
        HttpResponse<String> response = null;
        try {
            response = Unirest.post("https://flns-spring-test.herokuapp.com/api/login")
                    .header("Content-Type", "application/x-www-form-urlencoded")
                    .field("username", Main.getUser().getUsername())
                    .field("password", password)
                    .field("role", "ROLE_USER")
                    .asString();
            // Check the HTTP code status to inform the user if there is an error
            Main.errorCheck(response.getStatus());
        } catch (UnirestException e) {
            Main.ErrorManager(408);
        }

        if (response != null) {
            return response.getStatus() == 200;
        }
        return false;
    }

    public boolean noLabelVisible() {
        return !invalidDateLabel.isVisible() && !invalidRecipientLabel.isVisible() && !invalidIBAN.isVisible()
                && !invalidMessageLabel.isVisible() && !invalidDateLabel.isVisible() && !invalidPasswordLabel.isVisible()
                && !insufficientBalanceLabel.isVisible();
    }

    @FXML
    void handleTransferButtonClicked(MouseEvent event) {
        transfer();
    }

    private void clearAllTextFields() {
        amountField.setText("");
        recipientField.setText("");
        IBANField.setText("");
        messageField.setText("");
        dateField.setText("");
        passwordField.setText("");
    }

    @FXML
    void handleComponentKeyReleased(KeyEvent event) {
        if (event.getCode() == KeyCode.ESCAPE) {
            emulateBackButtonClicked();
        } else if (event.getCode() == KeyCode.ENTER) {
            transfer();
        }
        event.consume();
    }

    /**
     * Checks if the given <code>String</code> is a valid amount.
     * Requirements :
     * - string must not be empty
     * - string must not be null
     * - string must only contain characters from 0-9
     * - string must contain at least one digit that is not 0
     * - string must contain at most one dot (.)
     * - string length must be at most 12
     *
     * @param amount - <code>String</code> - the amount to check
     * @return <code>boolean</code> - whether the given amount is a valid amount or not
     */
    public static boolean isValidAmount(String amount) {
        if (amount == null) return false;
        if (amount.equals("") || (!amount.matches("^[0-9.]*$")) || amount.length() > 12) return false;
        boolean hasOneDot = false;
        boolean hasOnlyZeros = true;
        for (int i = 0; i < amount.length(); i++) {
            if (amount.charAt(i) == '.' && hasOneDot) return false;
            if (amount.charAt(i) == '.') hasOneDot = true;
            if (amount.charAt(i) >= 49 && amount.charAt(i) <= 57) hasOnlyZeros = false;
        }
        return !hasOnlyZeros;
    }

    /**
     * Checks if the given <code>String</code> is a valid recipient.
     * Requirements :
     * - string must only contain characters from a-z and from A-Z
     *
     * @param recipient - <code>String</code> - the recipient to check
     * @return <code>boolean</code> - whether the given recipient is a valid recipient or not
     */
    public static boolean isValidRecipient(String recipient) {
        if (recipient == null) return true;
        return recipient.matches("^[a-zA-Z]*$");
    }

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
        if ((!IBAN.matches("^[a-zA-Z0-9]*$")) || !(IBAN.length() == 16))
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

    /**
     * Checks if the given <code>String</code> is a valid message.
     * Requirements :
     * - string must only contain characters with ASCII code between 32 and 126 both included
     * - string length must be at most 256
     *
     * @param message - <code>String</code> - the message to check
     * @return <code>boolean</code> - whether the given message is a valid message or not
     */
    public static boolean isValidMessage(String message) {
        if (message == null) return true;
        if (message.equals("")) return true;
        if (message.length() > 256) return false;
        for (int i = 0; i < message.length(); i++) {
            if (!(message.charAt(i) >= 32 && message.charAt(i) <= 126)) return false;
        }
        return true;
    }

    /**
     * Checks if the given <code>String</code> is a valid date.
     * Requirements :
     * - string must only contain characters from 0-9 or dashes (-) or dots (.) (dashes and dots are mutually exclusive :
     * the string either contains dots or dashes, but not both)
     * - string must follow this format : XX.XX.XXXX or XX-XX-XXXX where X is a digit
     * - the year cannot be less than the current year and cannot be more than 1 year in the future
     *
     * @param date - <code>String</code> - the date to check
     * @return <code>boolean</code> - whether the given date is a valid date or not
     */
    public static boolean isValidDate(String date) {
        if (date == null) return true;
        if (date.equals("")) return true;
        if (!date.matches("^[0-9-.]*$") || !(date.length() == 10)) return false;
        boolean hasDot = false, hasDash = false;
        for (int i = 0; i < date.length(); i++) {
            switch (i) {
                case 0:
                case 1:
                case 3:
                case 4:
                case 6:
                case 7:
                case 8:
                case 9:
                    if (!Character.isDigit(date.charAt(i))) return false;
                    break;
                case 2:
                case 5:
                    if (date.charAt(i) == '.') hasDot = true;
                    if (date.charAt(i) == '-') hasDash = true;
                    if (date.charAt(i) == '.' && hasDash) return false;
                    if (date.charAt(i) == '-' && hasDot) return false;
                    break;
            }
        }
        int day = Integer.parseInt(date.charAt(0) + "" + date.charAt(1));
        int month = Integer.parseInt(date.charAt(3) + "" + date.charAt(4));
        int year = Integer.parseInt(date.charAt(6) + "" + date.charAt(7) + date.charAt(8) + date.charAt(9));
        int currentYear = Calendar.getInstance().get(Calendar.YEAR);
        if (!(day <= 31 && day >= 1)) return false;
        if (!(month <= 12 && month >= 0)) return false;
        return year >= currentYear && year <= currentYear + 1;
    }
}
