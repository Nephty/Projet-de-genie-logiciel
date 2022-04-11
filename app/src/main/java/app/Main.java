package app;

import back.user.Account;
import back.user.Bank;
import back.user.Request;
import back.user.Wallet;
import front.navigation.Flow;
import front.scenes.SceneLoader;
import front.scenes.Scenes;
import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.util.Locale;

/**
 * Main runnable class that launches the application.
 */
public class Main extends Application {
    public static Locale appLocale, FR_BE_Locale, EN_US_Locale, NL_NL_Locale, PT_PT_Locale, LT_LT_Locale, RU_RU_Locale, DE_DE_Locale, PL_PL_Locale;
    private static Bank bank;
    private static Stage stage;
    private static String token;
    private static String refreshToken;
    private static Wallet currentWallet;
    private static Account currentAccount;
    private static String newClient;
    private static Request request;

    public static void main(String[] args) {
        launch(args);
    }

    public static void setScene(Scene scene) {
        stage.setScene(scene);
    }

    public static String getNewClient() {
        return newClient;
    }

    public static void setNewClient(String NRN) {
        newClient = NRN;
    }

    public static void setRequest(Request req){
        request = req;
    }

    public static Request getRequest(){
        return request;
    }

    public static Bank getBank() {
        return bank;
    }

    public static void setBank(Bank newBank) {
        bank = newBank;
    }

    public static String getToken() {
        return token;
    }

    public static void setToken(String newToken) {
        token = newToken;
    }

    public static void setRefreshToken(String newToken) {
        refreshToken = newToken;
    }

    public static Wallet getCurrentWallet() {
        return currentWallet;
    }

    public static void setCurrentWallet(Wallet currentWallet) {
        Main.currentWallet = currentWallet;
    }

    public static Account getCurrentAccount() {
        return currentAccount;
    }

    public static void setCurrentAccount(Account currentAccount) {
        Main.currentAccount = currentAccount;
    }

    public static Stage getStage() {
        return stage;
    }

    /**
     * Check if the response is an error
     *
     * @param statut The error statut
     */
    public static void errorCheck(int statut) {
        if (statut >= 400) {
            ErrorManager(statut);
        }
    }

    /**
     * Manages HTTP errors
     *
     * @param statut The error code
     */
    public static void ErrorManager(int statut) {
        String message = "Error " + statut + ": ";
        switch (statut) {
            case (401):
                message += "access unauthorized, try to login again";
                break;
            case (403):
                message += "forbidden, data are not correct, try again";
                break;
            case (404):
                message += "not found, try again";
                break;
            case (409):
                message += "conflict, data are not correct, try again";
                break;
            case (500):
                message += "internal server error, try again later";
                break;
            case (502):
                message += "bad gateway, try again later";
                break;
            case (503):
                message += "service unavailable, try again later";
                break;
            default:
                message += "An error has occurred";
                break;
        }
        Stage errorWindow = new Stage();
        errorWindow.setWidth(544);
        errorWindow.setHeight(306);
        Label errorLabel = new Label(message);
        errorLabel.setAlignment(Pos.CENTER);
        Button closeButton = new Button("X");
        closeButton.setOnAction(e -> errorWindow.close());
        VBox vbox = new VBox();
        vbox.getChildren().addAll(errorLabel, closeButton);
        vbox.setAlignment(Pos.CENTER);
        Scene errorScene = new Scene(vbox);
        errorWindow.setScene(errorScene);
        errorWindow.showAndWait();
    }

    @Override
    public void start(Stage stage_) {
        stage = stage_;

        FR_BE_Locale = new Locale("fr", "BE");
        EN_US_Locale = new Locale("en", "US");
        NL_NL_Locale = new Locale("nl", "NL");
        PT_PT_Locale = new Locale("pt", "PT");
        LT_LT_Locale = new Locale("lt", "lt");
        RU_RU_Locale = new Locale("ru", "RU");
        DE_DE_Locale = new Locale("de", "DE");
        PL_PL_Locale = new Locale("pl", "PL");
        // TODO : if language = english, appLocale = english,...

        appLocale = EN_US_Locale;

        Scenes.AuthScene = SceneLoader.load("AuthScene.fxml", appLocale);
        Scenes.SignInScene = SceneLoader.load("SignInScene.fxml", appLocale);
        Scenes.LanguageScene = SceneLoader.load("LanguageScene.fxml", appLocale);
        Scenes.SignUpScene = SceneLoader.load("SignUpScene.fxml", appLocale);
        Scenes.MainScreenScene = SceneLoader.load("MainScreenScene.fxml", appLocale);
        Scenes.ChangePasswordScene = SceneLoader.load("ChangePasswordScene.fxml", appLocale);

        Flow.add(Scenes.AuthScene);

        stage.setResizable(false);
        stage.setTitle("Benkyng app");
        stage.setScene(Scenes.AuthScene);
        stage.show();
    }
}
