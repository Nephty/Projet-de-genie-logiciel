package app;

import back.user.Account;
import back.user.Bank;
import back.user.Profile;
import back.user.Wallet;
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
    private static Bank bank;
    private static Stage stage;
    private static String token = "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJTYXRhbiIsInJvbGUiOiJST0xFX1VTRVIiLCJpc3MiOiJodHRwOi8vbG9jYWxob3N0OjgwODAvYXBpL2xvZ2luIiwiZXhwIjoxNjQ5NjE1MTEyLCJ1c2VySWQiOiIxMjM0NTY3ODkifQ.5LP2W6CDGPCgjnbTlQZNv18u7JZtgcU4pjpu6xMooJA";
    private static String refreshToken;
    private static Wallet currentWallet;
    private static Account currentAccount;

    public static void main(String[] args) {
        launch(args);
    }

    public static void setScene(Scene scene) {
        stage.setScene(scene);
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

    @Override
    public void start(Stage stage_) {
        stage = stage_;

        Scenes.AuthScene = SceneLoader.load("AuthScene.fxml");
        Scenes.SignInScene = SceneLoader.load("SignInScene.fxml");
        Scenes.LanguageScene = SceneLoader.load("LanguageScene.fxml");
        Scenes.SignUpScene = SceneLoader.load("SignUpScene.fxml");
        Scenes.MainScreenScene = SceneLoader.load("MainScreenScene.fxml");
        Scenes.ChangePasswordScene = SceneLoader.load("ChangePasswordScene.fxml");


        Flow.add(Scenes.AuthScene);

        stage.setResizable(false);
        stage.setTitle("Benkyng app");
        stage.setScene(Scenes.AuthScene);
        stage.show();
    }

    public static Stage getStage() {
        return stage;
    }
}
