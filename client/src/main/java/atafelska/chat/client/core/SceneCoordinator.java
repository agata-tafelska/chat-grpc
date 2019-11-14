package atafelska.chat.client.core;

import atafelska.chat.client.net.ChatService;
import javafx.stage.Stage;

import static atafelska.chat.client.TextConstants.TITLE_CHAT;

public class SceneCoordinator {
    private SceneConfiguration defaultSceneConfiguration = new SceneConfiguration(1024, 768);

    private Stage stage;
    private ChatService chatService = null;

    public SceneCoordinator(Stage stage) {
        this.stage = stage;
    }

    public void initApplication() {
        stage.setTitle(TITLE_CHAT);
        stage.setScene(SceneFactory.getScene(SceneFactory.SceneType.ENTRY, defaultSceneConfiguration, this));
        stage.show();
    }
}
