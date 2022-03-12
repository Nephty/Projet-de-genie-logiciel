package front.scenes;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;

import java.io.IOException;
import java.net.URL;

/**
 * Handles the loading of FXML files to create the Scenes.
 */
public class SceneLoader {

    /**
     * Takes the given scene name to find the corresponding FXML file.
     * @param sceneName - <code>String</code> - the name of the scene to load (that is, the name of the FXML file,
     *                  .fxml extension is not mandatory in the string, but it is in the name of the file
     * @param c - <code>Class</code> - the Main class. Required for resources generation
     * @return <code>Scene</code> - the Scene that was created from the FXML file
     */
    public static Scene load(String sceneName, Class c) {
        if (!sceneName.endsWith(".fxml")) sceneName += ".fxml";
        URL url = c.getResource("/xml/scenes/" + sceneName);
        FXMLLoader FXMLLoader = new FXMLLoader(url);
        try {
            Parent sceneParent = FXMLLoader.load();
            return new Scene(sceneParent, 1280, 720);
        } catch (IOException e) {
            System.out.println("FATAL ERROR while trying to load scene " + url);
            e.printStackTrace();
        }
        return null;
    }
}
