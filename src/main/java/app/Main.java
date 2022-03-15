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
    private static Profile user;
    public static Portfolio portfolio;
    private static Stage stage;
    private static String token = "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJzYXRhbkBoZWxsLmNvbSIsInJvbGUiOiJST0xFX1VTRVIiLCJpc3MiOiJodHRwOi8vbG9jYWxob3N0OjgwODAvYXBpL2xvZ2luIiwiZXhwIjoxNjQ4MjQxNTIxfQ.Hr0KX07H5BBM9-rI94BmLFMfHK4jdVFfxgM3KG0vOjQ";
    private static Wallet currentWallet;
    private static Account currentAccount;
    public static int cpt = 0;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage_) {
        stage = stage_;


        try {
            user = new Profile("123456789");
        } catch (UnirestException e) {
            e.printStackTrace();
        }
        try {
            portfolio = new Portfolio(user.getNationalRegistrationNumber());
        } catch (UnirestException e) {
            e.printStackTrace();
        }

        Scenes.AuthScene = SceneLoader.load("AuthScene.fxml", getClass());
        Scenes.SignInScene = SceneLoader.load("SignInScene.fxml", getClass());
        Scenes.LanguageScene = SceneLoader.load("LanguageScene.fxml", getClass());
        Scenes.SignUpScene = SceneLoader.load("SignUpScene.fxml", getClass());
        Scenes.MainScreenScene = SceneLoader.load("MainScreenScene.fxml", getClass());
        Scenes.NotificationsScene = SceneLoader.load("NotificationsScene.fxml", getClass());
        Scenes.RequestsScene = SceneLoader.load("RequestsScene.fxml", getClass());
        Scenes.RequestsStatusScene = SceneLoader.load("RequestsStatusScene.fxml", getClass());
        Scenes.RequestNewPortfolioScene = SceneLoader.load("RequestNewPortfolioScene.fxml", getClass());
        Scenes.RequestTransferPermissionScene = SceneLoader.load("RequestTransferPermissionScene.fxml", getClass());
        Scenes.ChangePasswordScene = SceneLoader.load("ChangePasswordScene.fxml", getClass());
        Scenes.FinancialProductsScene = SceneLoader.load("FinancialProductsScene.fxml", getClass());
        Scenes.ProductDetailsScene = SceneLoader.load("ProductDetailsScene.fxml", getClass());
        Scenes.TransactionsHistoryScene = SceneLoader.load("TransactionsHistoryScene.fxml", getClass());
        Scenes.ExportHistoryScene = SceneLoader.load("ExportHistoryScene.fxml", getClass());
        Scenes.TransferScene = SceneLoader.load("TransferScene.fxml", getClass());
        Scenes.EnterPINScene = SceneLoader.load("EnterPINScene.fxml", getClass());
        Scenes.VisualizeToolScene = SceneLoader.load("VisualizeToolScene.fxml", getClass());

        Flow.add(Scenes.AuthScene);

        stage.setResizable(false);
        stage.setTitle("Benkyng app");
        stage.setScene(Scenes.AuthScene);
        stage.show();


//
//        Profile a = null;
//        try {
//            a = new Profile("123456789");
//        } catch (UnirestException e) {
//            e.printStackTrace();
//        }
//        System.out.println(a.getFirstName());
//        System.out.println(a.getLastName());
//        System.out.println(a.getNationalRegistrationNumber());
//        Bank a = null;
//        try {
//            a = new Bank("ABCD");
//        } catch (UnirestException e) {
//            e.printStackTrace();
//        }
//
//        System.out.println(a.getName());

//        Account a = null;
//        try {
//            a = new Account("uwu69420");
//        } catch (UnirestException e) {
//            e.printStackTrace();
//        }
//
//        System.out.println(a.getIBAN());
//        System.out.println(a.getAccountType());
//        System.out.println(a.getAccountOwner().getFirstName());

//        try {
//            Portfolio a = new Portfolio("123456789");
//        } catch (UnirestException e) {
//            e.printStackTrace();
//        }
    }

    public static void setScene(Scene scene) {
        stage.setScene(scene);
    }

    public static void setUser(Profile user_) {
        user = user_;
    }

    public static Profile getUser() {
        return user;
    }

    public static String getToken(){
        return token;
    }

    public static Portfolio getPortfolio(){
        return portfolio;
    }

    public static Wallet getCurrentWallet() {
        return currentWallet;
    }

    public static Account getCurrentAccount() {
        return currentAccount;
    }

    public static void setCurrentWallet(Wallet currentWallet) {
        Main.currentWallet = currentWallet;
    }

    public static void setCurrentAccount(Account currentAccount) {
        Main.currentAccount = currentAccount;
    }

    public static void updatePortfolio(){
        try{
            portfolio = new Portfolio(user.getNationalRegistrationNumber());
        } catch (UnirestException e) {
            e.printStackTrace();
        }
    }
}
