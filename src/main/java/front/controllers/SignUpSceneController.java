package front.controllers;

import front.Main;
import front.navigation.Flow;
import front.navigation.navigators.BackButtonNavigator;
import front.navigation.navigators.LanguageButtonNavigator;
import front.scenes.Scenes;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;

public class SignUpSceneController implements BackButtonNavigator, LanguageButtonNavigator {
    @FXML
    Label NRNTakenLabel, emailTakenLabel, usernameTakenLabel, passwordDoesNotMatchLabel, languageNotChosen,
        invalidLastNameLabel, invalidFirstNameLabel, invalidEmailLabel, invalidNRNLabel, invalidUsernameLabel;
    @FXML
    Button backButton, languageButton, signUpButton;
    @FXML
    TextField lastNameField, firstNameField, NRNField, emailAddressField, usernameField;
    @FXML
    PasswordField passwordField, confirmPasswordField;
    @FXML
    Label favoriteLanguageLabel;
    @FXML
    ComboBox<String> languageComboBox;
    @FXML
    CheckBox checkBox;



    boolean usernameTaken = false, emailTaken = false, NRNTaken = false, passwordMatches = true, languageChosen = true;

    public void handleBackButtonClicked(MouseEvent event) {
        handleBackButtonNavigation(event);
    }

    public void handleLanguageButtonClicked(MouseEvent event) {
        handleLanguageButtonNavigation(event);
    }

    @Override
    public void handleBackButtonNavigation(MouseEvent event) {
        Main.setScene(Flow.back());
    }

    @Override
    public void handleLanguageButtonNavigation(MouseEvent event) {
        Main.setScene(Flow.forward(Scenes.LanguageScene));
    }

    public void handleSignUpButtonClicked(MouseEvent mouseEvent) {
        // TODO : create all incorrectly formatted labels
        String lastName = lastNameField.getText(), firstName = firstNameField.getText(), email = emailAddressField.getText(),
                NRN = NRNField.getText(), username = usernameField.getText(), password = passwordField.getText(),
                passwordConfirmation = confirmPasswordField.getText();

        // Check if all fields are correctly formatted
        // Is the last name valid (A-Z, a-z) ? Show/hide the incorrect last name label accordingly
        if (!isValidLastName(lastName) && !invalidLastNameLabel.isVisible()) {
            invalidLastNameLabel.setVisible(true);
        } else if (isValidLastName(lastName) && invalidLastNameLabel.isVisible()) {
            invalidLastNameLabel.setVisible(false);
        }
        // Is the first name valid (A-Z, a-z) ? Show/hide the incorrect first name label accordingly
        if (!isValidFirstName(firstName) && !invalidFirstNameLabel.isVisible()) {
            invalidFirstNameLabel.setVisible(true);
        } else if (isValidFirstName(firstName) && invalidFirstNameLabel.isVisible()) {
            invalidFirstNameLabel.setVisible(false);
        }
        // Is the email valid (A-Z, a-z, 0-9, contains only one @, contains a . after the @) ? Show/hide the incorrect email label accordingly
        if (!isValidEmail(email) && !invalidEmailLabel.isVisible()) {
            invalidEmailLabel.setVisible(true);
        } else if (isValidEmail(email) && invalidEmailLabel.isVisible()) {
            invalidEmailLabel.setVisible(false);
        }
        // Is the NRN valid (XX.XX.XX-XXX.XX format with numbers)
        if (!isValidNRN(NRN) && !invalidNRNLabel.isVisible()) {
            invalidNRNLabel.setVisible(true);
        } else if (isValidNRN(NRN) && invalidNRNLabel.isVisible()) {
            invalidNRNLabel.setVisible(false);
        }


        // Manage the "xxxx already taken" labels visibility
        if (usernameTaken && !usernameTakenLabel.isVisible()) usernameTakenLabel.setVisible(true);
        else if (!usernameTaken && usernameTakenLabel.isVisible()) usernameTakenLabel.setVisible(false);

        if (emailTaken && !emailTakenLabel.isVisible()) emailTakenLabel.setVisible(true);
        else if (!emailTaken && emailTakenLabel.isVisible()) emailTakenLabel.setVisible(false);

        if (NRNTaken && !NRNTakenLabel.isVisible()) NRNTakenLabel.setVisible(true);
        else if (!NRNTaken && NRNTakenLabel.isVisible()) NRNTakenLabel.setVisible(false);
        // TODO : back-end signing up process implementation
    }

    /**
     * Checks if the given <code>String</code> is a valid last name.
     * Conditions :
     *  - string must not be empty
     *  - string must not be null
     *  - string must only contain characters from a-z and from A-Z
     * @param lastName - <code>String</code> - the last name to check
     * @return <code>boolean</code> - whether the given last name is a valid last name or not
     */
    public static boolean isValidLastName(String lastName) {
        return (!lastName.equals("") && (lastName != null) && (lastName.matches("^[a-zA-Z-]*$")));
    }

    /**
     * Checks if the given <code>String</code> is a valid first name.
     * Conditions :
     *  - string must not be empty
     *  - string must not be null
     *  - string must only contain characters from a-z, from A-Z or a dash (-).
     * @param firstName - <code>String</code> - the last name to check
     * @return <code>boolean</code> - whether the given first name is a valid first name or not
     */
    public static boolean isValidFirstName(String firstName) {
        return isValidLastName(firstName);
    }

    /**
     * Checks if the given <code>String</code> is a valid email.
     * Conditions :
     *  - string must not be empty
     *  - string must not be null
     *  - string must only contain characters from a-z, from A-Z, from 0-9 or characters that are either @ or .
     *  - string must only contain one @
     *  - string must contain at most one . after the @
     *  - string must contain at least one character from a-z or from A-Z before the @
     * @param email - <code>String</code> - the email to check
     * @return <code>boolean</code> - whether the given email is a valid email or not
     */
    public static boolean isValidEmail(String email) {
        if (email.equals("") || email == null || !email.matches("^[a-zA-Z0-9@.]*$")) return false;
        boolean hasOneAt = false;  // if the string contains at least one @
        boolean hasOneDotAfterAt = false;  // if the string contains at least one . after the first @
        boolean hasOneCharBeforeAt = false; // if the string contains at least one character before the first @
        for (char c : email.toCharArray()) {
            if (c == '@' && hasOneAt) return false; // string has 2 @, invalid email
            if (c == '@') hasOneAt = true; // first @ we encounter
            if (c == '.' && hasOneDotAfterAt) return false; // second . we encounter after the first ., invalid email
            if (c == '.' && hasOneAt) hasOneDotAfterAt = true; // first . we encounter after the first @
            if (("" + c).matches("^[a-zA-Z]") && !hasOneAt) hasOneCharBeforeAt = true;
        }
        return hasOneAt && hasOneDotAfterAt && hasOneCharBeforeAt;
    }

    /**
     * Checks if the given string is a valid NRN.
     * Conditions :
     *  - string must not be empty
     *  - string must not be null
     *  - string must match the format XX.XX.XX-XXX.XX where X in an integer in range 0-9
     * @param NRN - <code>String</code> - the NRN to check
     * @return <code>boolean</code> whether the given NRN is a valid NRN or not
     */
    public static boolean isValidNRN(String NRN) {
        if (NRN.equals("") || NRN == null || !NRN.matches("^[0-9-.]") || NRN.length() != 15) return false;
        for (int i = 0; i < 15; i++) {
            switch (i) {
                case 0:
                case 1:
                case 3:
                case 4:
                case 6:
                case 7:
                case 9:
                case 10:
                case 11:
                case 13:
                case 14:
                    if (!Character.isDigit(NRN.charAt(i))) return false;
                case 2:
                case 5:
                case 12:
                    if (NRN.charAt(i) != '.') return false;
                case 8:
                    if (NRN.charAt(i) != '-') return false;
            }
        }
        return true;
    }
}
