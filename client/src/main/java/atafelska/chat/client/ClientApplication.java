package atafelska.chat.client;

import atafelska.chat.client.core.Logger;
import atafelska.chat.client.core.SceneCoordinator;
import javafx.application.Application;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import java.io.IOException;
import java.net.URL;

public class ClientApplication extends Application {
    private SceneCoordinator sceneCoordinator;

    @Override
    public void start(Stage stage) throws Exception {
        stage.setResizable(false);
        initializeIcon(stage);
        sceneCoordinator = new SceneCoordinator(stage);
        sceneCoordinator.initApplication();
    }

    private void initializeIcon(Stage stage) {
        try {
            URL iconImageURL = ClassLoader.getSystemClassLoader().getResource("chatIcon.png");
            if (iconImageURL == null) {
                Logger.print("Initializing icon failed. Unable to retrieve icon URL.");
                return;
            }

            Image iconImage = new Image(iconImageURL.openStream());
            stage.getIcons().add(iconImage);
        } catch (IOException exception) {
            Logger.print("Initializing icon failed");
            exception.printStackTrace();
        }
    }
}
