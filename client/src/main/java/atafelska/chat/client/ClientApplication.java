package atafelska.chat.client;

import atafelska.chat.client.core.SceneCoordinator;
import javafx.application.Application;
import javafx.stage.Stage;

public class ClientApplication extends Application {
    private SceneCoordinator sceneCoordinator;

    @Override
    public void start(Stage stage) throws Exception {
        stage.setResizable(false);

        sceneCoordinator = new SceneCoordinator(stage);
        sceneCoordinator.initApplication();
    }
}
