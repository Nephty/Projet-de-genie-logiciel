package app;

import back.user.*;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import front.navigation.Flow;
import front.scenes.SceneLoader;
import front.scenes.Scenes;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Locale;

import static back.user.ErrorHandler.refreshToken;

/**
 * Main runnable class that launches the application.
 */
public class Main extends Application {
    public static Locale appLocale, FR_BE_Locale, EN_US_Locale, NL_NL_Locale, PT_PT_Locale, LT_LT_Locale, RU_RU_Locale, DE_DE_Locale, PL_PL_Locale;
    public static Portfolio portfolio;
    private static Profile user;
    private static Stage stage;
    private static String token;
    private static String refreshToken;
    private static Wallet currentWallet = null;
    private static Account currentAccount = null;

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

    public static String getRefreshToken() {
        return refreshToken;
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
        String swift = null;
        String IBAN = null;
        if(!(getCurrentWallet() == null)){
            swift = currentWallet.getBank().getSwiftCode();
        }
        if(!(getCurrentAccount() == null)){
            IBAN = currentAccount.getIBAN();
        }
        portfolio = new Portfolio(user.getNationalRegistrationNumber());

        if(!(getCurrentWallet() == null)){
            ArrayList<Wallet> walletList = getPortfolio().getWalletList();

            for(int i = 0; i<walletList.size() ; i++){
                if(walletList.get(i).getBank().getSwiftCode().equals(swift)){
                    currentWallet = walletList.get(i);
                }
            }

            if(!(getCurrentAccount() == null)){
                ArrayList<Account> accountList = currentWallet.getAccountList();

                for(int j = 0; j< accountList.size(); j++){
                    if(accountList.get(j).getIBAN().equals(IBAN)){
                        currentAccount = accountList.get(j);
                    }
                }
            }
        }
    }

    /**
     * Check if the response is an error
     *
     * @param statut The error statut
     */
    public static void errorCheck(int statut) {
        if(statut == 412){
            ErrorHandler.refreshToken();
        }
        if (statut >= 400 || statut != 412) {
            ErrorManager(statut);
        }
    }

    /**
     * Manages HTTP errors
     *
     * @param statut The error code
     */
    public static void ErrorManager(int statut) {
        String message = "Error + " + statut + ": ";
        switch (statut) {
            case (401):
                message = message + "access unauthorized, try to login again";
                break;
            case (403):
                message = message + "forbidden, data are not correct, try again";
                break;
            case (404):
                message = message + "not found, try again";
                break;
            case (409):
                message = message + "conflict, data are not correct, try again";
                break;
            case (500):
                message = message + "internal server error, try again later";
                break;
            case (502):
                message = message + "bad gateway, try again later";
                break;
            case (503):
                message = message + "service unavalaible, try again later";
                break;
            default:
                message = message + "An error has occured";
                break;
        }
    }

    public static Stage getStage() {
        return stage;
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
        DE_DE_Locale = new Locale("de", "DE");
        PL_PL_Locale = new Locale("pl", "PL");

        appLocale = EN_US_Locale;

        Scenes.AuthScene = SceneLoader.load("AuthScene.fxml", appLocale);
        Scenes.SignInScene = SceneLoader.load("SignInScene.fxml", appLocale);
        Scenes.LanguageScene = SceneLoader.load("LanguageScene.fxml", appLocale);
        Scenes.SignUpScene = SceneLoader.load("SignUpScene.fxml", appLocale);

        Flow.add(Scenes.AuthScene);

        stage.setResizable(false);
        stage.setTitle("Benkyng app");
        stage.setScene(Scenes.AuthScene);
        stage.show();
    }
}
