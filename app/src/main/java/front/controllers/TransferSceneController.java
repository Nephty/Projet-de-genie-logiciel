package front.controllers;

import app.Main;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import front.navigation.Flow;
import front.navigation.navigators.BackButtonNavigator;
import front.scenes.Scenes;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;

import java.util.Calendar;

public class TransferSceneController extends Controller implements BackButtonNavigator {
    private final int charactersLeft = 256;
    private final int previousMessageLength = 0;
    @FXML
    public Button backButton, transferButton;
    @FXML
    public TextField amountField, recipientField, IBANField, messageField, dateField;
    @FXML
    public Label invalidAmountLabel, invalidRecipientLabel, invalidIBAN, invalidMessageLabel, invalidDateLabel,
            transferExecutedLabel, charactersLeftLabel;

    public static void executeTransfer() {
        System.out.println("transfer executed.");
        // TODO : execute the given transfer (that method is ran by the PIN scene only if the PIN is correct)
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
        // TODO : Retirer les commentaires
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
        transferExecutedLabel.setVisible(false);
        clearAllTextFields();
    }

    /**
     * Checks if every field is properly filled in. Initializes the transfer process.
     */
    public void transfer() {
        String amount = amountField.getText(), recipient = recipientField.getText(), IBAN = IBANField.getText(),
                message = messageField.getText(), date = dateField.getText();

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

        String recipientActual = Main.getCurrentAccount().getIBAN();

        if(recipientActual != IBAN) {
            if (noLabelVisible()) {
                // TODO : Manage errors (insufficient balance)
                // Creates the transfer if everything is correct
                Unirest.setTimeouts(0, 0);
                HttpResponse<String> response = null;
                try {
                    response = Unirest.post("https://flns-spring-test.herokuapp.com/api/transaction")
                            .header("Authorization", "Bearer " + Main.getToken())
                            .header("Content-Type", "application/json")
                            .body("{\r\n    \"transactionTypeId\": 1,\r\n    \"senderIban\": \"" + recipientActual + "\",\r\n    \"recipientIban\": \"" + IBAN + "\",\r\n    \"currencyId\": 0,\r\n    \"transactionAmount\": " + amount + "\r\n}")
                            .asString();
                    Main.errorCheck(response.getStatus());
                } catch (UnirestException e) {
                    Main.ErrorManager(408);
                }

                Main.setScene(Flow.forward(Scenes.MainScreenScene));
                clearAllTextFields();
            }
        }
    }

    public boolean noLabelVisible() {
        return !invalidDateLabel.isVisible() && !invalidRecipientLabel.isVisible() && !invalidIBAN.isVisible()
                && !invalidMessageLabel.isVisible() && !invalidDateLabel.isVisible();
    }

    public void emulateTransferButtonClicked() {
        transfer();
    }

    @FXML
    public void handleAmountFieldKeyPressed(KeyEvent keyEvent) {
        if (keyEvent.getCode() == KeyCode.ENTER) {
            emulateTransferButtonClicked();
        }
    }

    @FXML
    public void handleRecipientFieldKeyPressed(KeyEvent keyEvent) {
        if (keyEvent.getCode() == KeyCode.ENTER) {
            emulateTransferButtonClicked();
        }
    }

    @FXML
    public void handleIBANFieldKeyPressed(KeyEvent keyEvent) {
        if (keyEvent.getCode() == KeyCode.ENTER) {
            emulateTransferButtonClicked();
        }
    }

    @FXML
    public void handleMessageFieldKeyPressed(KeyEvent keyEvent) {
        if (keyEvent.getCode() == KeyCode.ENTER) {
            emulateTransferButtonClicked();
        }

    }

    @FXML
    public void handleDateFieldKeyPressed(KeyEvent keyEvent) {
        if (keyEvent.getCode() == KeyCode.ENTER) {
            emulateTransferButtonClicked();
        }
    }

    @FXML
    public void handleTransferButtonClicked(MouseEvent event) {
        transfer();
    }

    private void clearAllTextFields() {
        amountField.setText("");
        recipientField.setText("");
        IBANField.setText("");
        messageField.setText("");
        dateField.setText("");
    }

    @FXML
    public void handleComponentKeyReleased(KeyEvent event) {
        if (event.getCode() == KeyCode.ESCAPE) {
            emulateBackButtonClicked();
            event.consume();
        }
    }
}