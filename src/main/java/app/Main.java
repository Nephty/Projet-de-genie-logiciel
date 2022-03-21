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

/**
 * Main runnable class that launches the application.
 */
public class Main extends Application {
    public static Portfolio portfolio;
    public static int cpt = 0;
    private static Profile user;
    private static Stage stage;
    //    private static String token = "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJzYXRhbkBoZWxsLmNvbSIsInJvbGUiOiJST0xFX1VTRVIiLCJpc3MiOiJodHRwOi8vbG9jYWxob3N0OjgwODAvYXBpL2xvZ2luIiwiZXhwIjoxNjQ4MjQxNTIxfQ.Hr0KX07H5BBM9-rI94BmLFMfHK4jdVFfxgM3KG0vOjQ";
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
            e.printStackTrace();
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
    }

    @Override
    public void start(Stage stage_) {
        stage = stage_;

        Scenes.AuthScene = SceneLoader.load("AuthScene.fxml");
        Scenes.SignInScene = SceneLoader.load("SignInScene.fxml");
        Scenes.LanguageScene = SceneLoader.load("LanguageScene.fxml");
        Scenes.SignUpScene = SceneLoader.load("SignUpScene.fxml");
        Scenes.MainScreenScene = SceneLoader.load("MainScreenScene.fxml");

        Flow.add(Scenes.AuthScene);

        stage.setResizable(false);
        stage.setTitle("Benkyng app");
        stage.setScene(Scenes.AuthScene);
        stage.show();
    }
}
