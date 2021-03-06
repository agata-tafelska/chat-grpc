package atafelska.chat.client.core;

import atafelska.chat.client.controllers.BaseController;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;

import javax.annotation.Nullable;
import java.io.IOException;
import java.util.Map;

public class SceneFactory {

    public enum SceneType {
        ENTRY,
        REGISTER,
        JOINASGUEST,
        CHATBOARD,
        LOADING
    }

    public static Scene getScene(SceneType type,
                                 SceneConfiguration configuration,
                                 SceneCoordinator coordinator) {
        return getScene(type, configuration, coordinator, null);
    }

    public static Scene getScene(SceneType type,
                                 SceneConfiguration configuration,
                                 SceneCoordinator coordinator,
                                 @Nullable Map<String, String> optionalParams) {
        String sceneResource;
        switch (type) {
            case ENTRY:
                sceneResource = "entry.fxml";
                break;
            case REGISTER:
                sceneResource = "register.fxml";
                break;
            case JOINASGUEST:
                sceneResource = "joinAsGuest.fxml";
                break;
            case CHATBOARD:
                sceneResource = "chatboard.fxml";
                break;
            case LOADING:
                sceneResource = "loading.fxml";
                break;
            default:
                throw new IllegalArgumentException("There is no resource for given scene type: " + type);
        }

        Logger.print("Loading scene from resource: " + sceneResource);
        try {
            // Load XML resource
            FXMLLoader resourceLoader = new FXMLLoader(ClassLoader.getSystemClassLoader().getResource(sceneResource));
            Parent parent = resourceLoader.load();

            // Set SceneCoordinator to controller
            if (resourceLoader.getController() instanceof BaseController) {
                BaseController controller = resourceLoader.getController();
                controller.setSceneCoordinator(coordinator);
                controller.onLoaded();
                controller.onOptionalParamsLoaded(optionalParams);
            }

            return new Scene(parent, configuration.getWidth(), configuration.getHeight());
        } catch (IOException exception) {
            throw new IllegalStateException(exception);
        }
    }

}
