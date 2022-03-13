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
        Scenes.ChangePasswordScene = SceneLoader.load("ChangePasswordScene.fxml", getClass());
        Scenes.ManageRequestsScene = SceneLoader.load("ManageRequestsScene.fxml", getClass());
        Scenes.ManageTransferPermissionRequestsScene = SceneLoader.load("ManageTransferPermissionRequestsScene.fxml", getClass());
        Scenes.ManagePortfolioRequestsScene = SceneLoader.load("ManagePortfolioRequestsScene.fxml", getClass());
        Scenes.RequestsStatusScene = SceneLoader.load("RequestsStatusScene.fxml", getClass());
        Scenes.ClientsScene = SceneLoader.load("ClientsScene.fxml", getClass());
        Scenes.ExportDataScene = SceneLoader.load("ExportDataScene.fxml", getClass());
        Scenes.AddClientScene = null; // SceneLoader.load("AddClientScene.fxml", getClass());
        Scenes.ClientDetailsScene = null; // SceneLoader.load("ClientDetailsScene.fxml", getClass());
        Scenes.CreateClientAccountScene = null; // SceneLoader.load("CreateClientAccountScene.fxml", getClass());
        Scenes.ManageDataScene = null; // SceneLoader.load("ManageDataScene.fxml", getClass());
        Scenes.ImportDataScene = null; // SceneLoader.load("ImportDataScene.fxml", getClass());

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
