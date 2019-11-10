package atafelska.chat.client;

import javafx.application.Application;
import javafx.stage.Stage;

import static atafelska.chat.client.TextConstants.TITLE_CHAT;

public class ClientApplication extends Application {

    private SceneConfiguration defaultSceneConfiguration = new SceneConfiguration(1024, 768);

    @Override
    public void start(Stage stage) throws Exception {
        stage.setTitle(TITLE_CHAT);
        stage.setScene(SceneFactory.getScene(SceneFactory.SceneType.ENTRY, defaultSceneConfiguration));
        stage.setResizable(false);
        stage.show();
    }
}
