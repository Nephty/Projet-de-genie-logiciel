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

    public static void clearData(){
        portfolio = null;
        user = null;
        token = null;
        refreshToken = null;
        currentWallet = null;
        currentAccount = null;
    }

    public static void updatePortfolio() {
        try {
            portfolio = new Portfolio(user.getNationalRegistrationNumber());
        } catch (UnirestException e) {
            e.printStackTrace();
        }
    }

    public static void ErrorManager(int statut){

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

/*
        Profile a = null;
        try {
            a = new Profile("123456789");
        } catch (UnirestException e) {
            e.printStackTrace();
        }
        System.out.println(a.getFirstName());
        System.out.println(a.getLastName());
        System.out.println(a.getNationalRegistrationNumber());
        Bank a = null;
        try {
            a = new Bank("ABCD");
        } catch (UnirestException e) {
            e.printStackTrace();
        }

        System.out.println(a.getName());

        Account a = null;
        try {
            a = new Account("uwu69420");
        } catch (UnirestException e) {
            e.printStackTrace();
        }

        System.out.println(a.getIBAN());
        System.out.println(a.getAccountType());
        System.out.println(a.getAccountOwner().getFirstName());

        try {
            Portfolio a = new Portfolio("123456789");
        } catch (UnirestException e) {
            e.printStackTrace();
        }
 */
    }
}
