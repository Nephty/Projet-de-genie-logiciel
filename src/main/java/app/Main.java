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

    public static void setScene(Scene scene) {
        stage.setScene(scene);
    }

    public static Profile getUser() {
        return user;
    }

    public static void setUser(Profile user_) {
        user = user_;
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
        Scenes.ManageRequestsScene = SceneLoader.load("ManageRequestsScene.fxml");
        Scenes.ManageTransferPermissionRequestsScene = SceneLoader.load("ManageTransferPermissionRequestsScene.fxml");
        Scenes.ManagePortfolioRequestsScene = SceneLoader.load("ManagePortfolioRequestsScene.fxml");
        Scenes.RequestsStatusScene = SceneLoader.load("RequestsStatusScene.fxml");
        Scenes.ClientsScene = SceneLoader.load("ClientsScene.fxml");
        Scenes.ExportDataScene = SceneLoader.load("ExportDataScene.fxml");
        Scenes.AddClientScene = SceneLoader.load("AddClientScene.fxml");
        Scenes.ClientDetailsScene = SceneLoader.load("ClientDetailsScene.fxml");
        Scenes.CreateClientAccountScene = SceneLoader.load("CreateClientAccountScene.fxml");
        Scenes.ManageDataScene = SceneLoader.load("ManageDataScene.fxml");
        Scenes.ImportDataScene = SceneLoader.load("ImportDataScene.fxml");

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
