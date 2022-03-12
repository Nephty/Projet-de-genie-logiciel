package app;

import back.user.Profile;
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
    private static Stage stage;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage_) {
        stage = stage_;

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
}
