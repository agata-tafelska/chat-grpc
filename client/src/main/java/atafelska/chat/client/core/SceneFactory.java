package atafelska.chat.client.core;

import atafelska.chat.client.controllers.BaseController;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;

import java.io.IOException;

public class SceneFactory {

    public enum SceneType {
        ENTRY,
        CHATBOARD,
        LOADING,
        ERROR
    }

    public static Scene getScene(SceneType type, SceneConfiguration configuration, SceneCoordinator coordinator) {
        String sceneResource;
        switch (type) {
            case ENTRY:
                sceneResource = "entry.fxml";
                break;
            case CHATBOARD:
                sceneResource = "chatboard.fxml";
                break;
            case LOADING:
                sceneResource = "loading.fxml";
                break;
            case ERROR:
                sceneResource = "error.fxml";
            default:
                throw new IllegalArgumentException("There is no resource for given scene type: "+ type);
        }

        Logger.print("Loading scene from resource: " + sceneResource);
        try {
            // Load XML resource
            FXMLLoader resourceLoader = new FXMLLoader(ClassLoader.getSystemClassLoader().getResource(sceneResource));
            Parent parent = resourceLoader.load();

            // Set SceneCoordinator to controller
            if (resourceLoader.getController() instanceof BaseController) {
                ((BaseController) resourceLoader.getController()).setSceneCoordinator(coordinator);
            }

            return new Scene(parent, configuration.getWidth(), configuration.getHeight());
        } catch (IOException exception) {
            throw new IllegalStateException(exception);
        }
    }

}
