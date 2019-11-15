package atafelska.chat.client.core;

import atafelska.chat.Chat;
import atafelska.chat.User;
import atafelska.chat.client.net.ChatService;
import atafelska.chat.client.utils.ErrorUtils;
import io.grpc.stub.StreamObserver;
import javafx.application.Platform;
import javafx.stage.Stage;

import static atafelska.chat.client.TextConstants.TITLE_CHAT;

public class SceneCoordinator {
    private SceneConfiguration defaultSceneConfiguration = new SceneConfiguration(1024, 768);

    private Stage stage;
    private ChatService chatService = null;
    private String errorMessage = "";

    public SceneCoordinator(Stage stage) {
        this.stage = stage;
    }

    public void initApplication() {
        stage.setTitle(TITLE_CHAT);
        stage.setScene(SceneFactory.getScene(SceneFactory.SceneType.ENTRY, defaultSceneConfiguration, this));
        stage.show();
    }

    public void joinChat(String host, String username) {
        showLoading();
        chatService = new ChatService(host);
        chatService.getChat(User.newBuilder().setName(username).build(), new StreamObserver<Chat>() {
            @Override
            public void onNext(Chat value) {
                showChat();
            }

            @Override
            public void onError(Throwable t) {
                showError(ErrorUtils.getErrorMessage(t));
            }

            @Override
            public void onCompleted() {
                // Do nothing
            }
        });
    }

    private void showChat() {
        Platform.runLater(() -> {
            Logger.print("");
            stage.setScene(SceneFactory.getScene(SceneFactory.SceneType.CHATBOARD, defaultSceneConfiguration, this));
        });
    }

    private void showLoading() {
        stage.setScene(SceneFactory.getScene(SceneFactory.SceneType.LOADING, defaultSceneConfiguration, this));
    }

    private void showError(String errorMessage) {

    }
}
