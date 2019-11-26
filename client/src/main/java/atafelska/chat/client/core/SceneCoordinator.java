package atafelska.chat.client.core;

import atafelska.chat.Chat;
import atafelska.chat.CurrentUsers;
import atafelska.chat.Message;
import atafelska.chat.User;
import atafelska.chat.client.data.MessageObservable;
import atafelska.chat.client.data.UsersObservable;
import atafelska.chat.client.net.ChatService;
import atafelska.chat.client.net.DefaultStreamObserver;
import atafelska.chat.client.utils.ErrorUtils;
import io.grpc.stub.StreamObserver;
import javafx.application.Platform;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.Observer;

import static atafelska.chat.client.TextConstants.TITLE_CHAT;

public class SceneCoordinator {
    private SceneConfiguration defaultSceneConfiguration = new SceneConfiguration(1024, 768);

    private Stage stage;
    private ChatService chatService = null;
    private User currentUser = null;

    private MessageObservable messagesObservable = new MessageObservable();
    private UsersObservable usersObservable = new UsersObservable();
    private StreamObserver<CurrentUsers> usersStreamObserver = new DefaultStreamObserver<CurrentUsers>() {
        @Override
        public void onNext(CurrentUsers value) {
            usersObservable.updateUsers(value.getUsersList());
        }
    };
    private StreamObserver<Message> messageStreamObserver = new DefaultStreamObserver<Message>() {
        @Override
        public void onNext(Message value) {
            messagesObservable.addMessage(value);
        }
    };

    public SceneCoordinator(Stage stage) {
        this.stage = stage;
    }

    public void initApplication() {
        stage.setTitle(TITLE_CHAT);
        showEntry();
        stage.show();
    }

    public void joinChat(String host, String username) {
        showLoading();
        chatService = new ChatService(host);
        currentUser = User.newBuilder().setName(username).build();
        chatService.getChat(currentUser, new StreamObserver<Chat>() {
            @Override
            public void onNext(Chat value) {
                Logger.print("Chat received, updating values of UsersObservable and MessagesObservable");
                messagesObservable.setMessages(new ArrayList<>(value.getMessagesList()));
                usersObservable.updateUsers(value.getUserList());
                showChat();

                chatService.observeUsers(currentUser, usersStreamObserver);
                chatService.observeMessages(currentUser, messageStreamObserver);
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

    public void observeChat(Observer usersObserver, Observer messagesObserver) {
        usersObservable.addObserver(usersObserver);
        messagesObservable.addObserver(messagesObserver);

        usersObservable.notifyObservers();
        messagesObservable.notifyObservers();
    }

    public void sendMessage(String text) {
        Message message =
                Message.newBuilder()
                        .setUser(currentUser)
                        .setText(text)
                        .setTimestamp(System.currentTimeMillis())
                        .build();

        chatService.sendMessage(message, new StreamObserver<Message>() {
            @Override
            public void onNext(Message value) {
                // Do nothing
            }

            @Override
            public void onError(Throwable t) {
                Logger.print("Unable to send message. Reason: " + t.getMessage());
                t.printStackTrace();
            }

            @Override
            public void onCompleted() {
                // Do nothing
            }
        });

    }

    public void logout() {
        chatService.unsubscribe(currentUser, new StreamObserver<User>() {
            @Override
            public void onNext(User value) {
                Logger.print("Logout performed successfully");
            }

            @Override
            public void onError(Throwable t) {
                Logger.print("Error during logout. Reason: " + t.getMessage());
            }

            @Override
            public void onCompleted() {
                // Do nothing
            }
        });

        currentUser = null;
        chatService = null;
        showEntry();
    }

    private void showEntry() {
        Platform.runLater(() -> {
            Logger.print("");
            stage.setScene(SceneFactory.getScene(SceneFactory.SceneType.ENTRY, defaultSceneConfiguration, this));
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
