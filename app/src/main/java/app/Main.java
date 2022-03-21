package app;

import back.user.Account;
import back.user.Portfolio;
import back.user.Profile;
import back.user.Wallet;
import com.mashape.unirest.http.exceptions.UnirestException;
import front.navigation.Flow;
import front.scenes.SceneLoader;
import front.scenes.Scenes;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.util.Locale;

/**
 * Main runnable class that launches the application.
 */
public class Main extends Application {
    public static Locale appLocale, FR_BE_Locale, EN_US_Locale, NL_NL_Locale, PT_PT_Locale, LT_LT_Locale, RU_RU_Locale;
    public static Portfolio portfolio;
    private static Profile user;
    private static Stage stage;
    private static String token;
    private static String refreshToken;
    private static Wallet currentWallet;
    private static Account currentAccount;

    public static void main(String[] args) {
        launch(args);
    }

    public static void setScene(Scene scene) {
        stage.setScene(scene);
    }

    public static Profile getUser() {
        return user;
    }

    public static void setUser(Profile user_) {
        user = user_;
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

    public static Portfolio getPortfolio() {
        return portfolio;
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

    /**
     * Clear all the data in Main
     */
    public static void clearData() {
        portfolio = null;
        user = null;
        token = null;
        refreshToken = null;
        currentWallet = null;
        currentAccount = null;
    }

    /**
     * Updates the portfolio by creating new one
     */
    public static void updatePortfolio() {
        try {
            portfolio = new Portfolio(user.getNationalRegistrationNumber());
        } catch (UnirestException e) {
            Main.ErrorManager(408);
        }
    }

    /**
     * Check if the response is an error
     * @param statut The error statut
     */
    public static void errorCheck(int statut){
        if(statut >= 400){
            ErrorManager(statut);
        }
    }

    /**
     * Manages HTTP errors
     * @param statut The error code
     */
    public static void ErrorManager(int statut) {
        String message = "Error + "+statut+": ";
        switch(statut){
            case(401):
                message = message + "access unauthorized, try to login again"; break;
            case(403):
                message = message + "forbidden, data are not correct, try again"; break;
            case(404):
                message = message + "not found, try again"; break;
            case(409):
                message = message + "conflict, data are not correct, try again"; break;
            case(500):
                message = message + "internal server error, try again later"; break;
            case(502):
                message = message + "bad gateway, try again later"; break;
            case(503):
                message = message + "service unavalaible, try again later"; break;
            default:
                message = message + "An error has occured"; break;
        }
        // TODO : Generate a pop-up with the message
    }

    @Override
    public void start(Stage stage_) {
        stage = stage_;

        FR_BE_Locale = new Locale("fr", "BE");
        EN_US_Locale = new Locale("en", "US");
        NL_NL_Locale = new Locale("nl", "NL");
        PT_PT_Locale = new Locale("pt", "PT");
        LT_LT_Locale = new Locale("lt", "LT");
        RU_RU_Locale = new Locale("ru", "RU");

        // TODO : back-end : get the user's fav language and set it as the appLocale

        appLocale = EN_US_Locale;

        Scenes.AuthScene = SceneLoader.load("AuthScene.fxml", appLocale);
        Scenes.SignInScene = SceneLoader.load("SignInScene.fxml", appLocale);
        Scenes.LanguageScene = SceneLoader.load("LanguageScene.fxml", appLocale);
        Scenes.SignUpScene = SceneLoader.load("SignUpScene.fxml", appLocale);
        Scenes.MainScreenScene = SceneLoader.load("MainScreenScene.fxml", appLocale);

        Flow.add(Scenes.AuthScene);

        stage.setResizable(false);
        stage.setTitle("Benkyng app");
        stage.setScene(Scenes.AuthScene);
        stage.show();
    }
}
