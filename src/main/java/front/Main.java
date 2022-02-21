package front;

import back.user.User;
import front.navigation.Flow;
import front.scenes.SceneLoader;
import front.scenes.Scenes;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;


public class Main extends Application {
    private static User user;
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

        Flow.add(Scenes.AuthScene);
        stage.setResizable(false);
        stage.setTitle("FXML Welcome");
        stage.setScene(Scenes.AuthScene);
        stage.show();
    }

    public static void setScene(Scene scene) {
        stage.setScene(scene);
    }

    public static void setUser(User user_) {
        user = user_;
    }

    public static User getUser() {
        return user;
    }
}
