package atafelska.chat.client.core;

import atafelska.chat.Chat;
import atafelska.chat.CurrentUsers;
import atafelska.chat.Message;
import atafelska.chat.User;
import atafelska.chat.client.TextConstants;
import atafelska.chat.client.data.MessageObservable;
import atafelska.chat.client.data.UsersObservable;
import atafelska.chat.client.net.ChatService;
import atafelska.chat.client.net.DefaultStreamObserver;
import atafelska.chat.client.net.DelayedTask;
import io.grpc.StatusRuntimeException;
import io.grpc.stub.StreamObserver;
import javafx.application.Platform;
import javafx.stage.Stage;

import javax.annotation.Nullable;
import java.util.*;

import static atafelska.chat.client.TextConstants.*;

public class SceneCoordinator {
    public static final String OPTION_PARAM_HOST_ERROR = "host_error_message";
    public static final String OPTION_PARAM_USERNAME_ERROR = "username_error_message";
    public static final String OPTION_PARAM_USERNAME_EXISTS_ERROR = "username_exists_error_message";
    public static final String OPTION_PARAM_HOST = "host";
    public static final String OPTION_PARAM_USERNAME = "username";
    public static final String OPTION_PARAM_ACCOUNT_CREATED = "account_created";
    private SceneConfiguration defaultSceneConfiguration = new SceneConfiguration(1024, 768);

    private Stage stage;
    private ChatService chatService = null;
    private User currentUser = null;

    private DelayedTask connectionLostDelayedTask = new DelayedTask(() -> {
        showError(null, ERROR_CONNECTION_LOST);
    });

    private MessageObservable messagesObservable = new MessageObservable();
    private UsersObservable usersObservable = new UsersObservable();
    private StreamObserver<CurrentUsers> usersStreamObserver = new DefaultStreamObserver<CurrentUsers>() {
        @Override
        public void onNext(CurrentUsers value) {
            /*
             Set connection lost countdown to 5 seconds
             Client expects next update in no more that this time
             In case of timeout, lost connection error will be shown
             */
            connectionLostDelayedTask.delayExecution(5);
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

    public void joinChat(String host, String username, String password, boolean isGuest) {
        showLoading();
        chatService = new ChatService(host);
        if (isGuest) {
            currentUser = User.newBuilder().setName(username).setPassword(password).setIsGuest(isGuest).build();
            Logger.print("Current user: " + currentUser);
        } else {
            currentUser = User.newBuilder().setName(username).setPassword(password).setIsGuest(isGuest).build();
            Logger.print("Current user: " + currentUser);
        }
        chatService.getChat(currentUser, new StreamObserver<Chat>() {
            @Override
            public void onNext(Chat value) {
                Logger.print("Chat received, updating values of UsersObservable and MessagesObservable");
                messagesObservable.setMessages(new ArrayList<>(value.getMessagesList()));
                usersObservable.updateUsers(value.getUserList());
                showChat();

                currentUser = value.getCurrentUser();

                chatService.observeUsers(currentUser, usersStreamObserver);
                chatService.observeMessages(currentUser, messageStreamObserver);

                connectionLostDelayedTask.initialize();
            }

            @Override
            public void onError(Throwable throwable) {
                if (throwable instanceof StatusRuntimeException) {
                    StatusRuntimeException exception = (StatusRuntimeException) throwable;
                    switch (exception.getStatus().getCode()) {
                        case UNAVAILABLE:
                            showError(ERROR_SERVICE_UNAVAILABLE, null);
                            return;
                        case UNAUTHENTICATED:
                            showError(null, ERROR_UNAUTHENTICATED);
                            return;
                    }
                }
                showError(null, ERROR_UNKNOWN);
            }

            @Override
            public void onCompleted() {
                // Do nothing
            }
        });
    }

    public void registerUser(String host, String username, String password) {
        showLoading();
        chatService = new ChatService(host);
        User user = User.newBuilder().setName(username).setPassword(password).build();
        chatService.register(user, new StreamObserver<User>() {
            @Override
            public void onNext(User value) {
                Map<String, String> params = new HashMap<>();
                params.put(OPTION_PARAM_ACCOUNT_CREATED, ACCOUNT_CREATED);
                Platform.runLater(() -> {
                    stage.setScene(SceneFactory.getScene(SceneFactory.SceneType.ENTRY, defaultSceneConfiguration, SceneCoordinator.this, params));
                });
            }

            @Override
            public void onError(Throwable throwable) {
                if (throwable instanceof StatusRuntimeException) {
                    StatusRuntimeException exception = (StatusRuntimeException) throwable;
                    switch (exception.getStatus().getCode()) {
                        case ALREADY_EXISTS:
                            Map<String, String> params = new HashMap<>();
                            params.put(OPTION_PARAM_USERNAME_EXISTS_ERROR, ERROR_DUPLICATED_USER);
                            Platform.runLater(() -> {
                                stage.setScene(SceneFactory.getScene(SceneFactory.SceneType.REGISTER, defaultSceneConfiguration, SceneCoordinator.this, params));
                            });
                            return;
                    }
                }
                showError(null, ERROR_UNKNOWN);
            }

            @Override
            public void onCompleted() {

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
            public void onError(Throwable throwable) {
                Logger.print("Unable to send message. Reason: " + throwable.getMessage());
                showError(null, ERROR_CONNECTION_LOST);
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

        connectionLostDelayedTask.cancel();
        currentUser = null;
        chatService = null;
        showEntry();
    }

    public void showEntry() {
        Platform.runLater(() -> {
            Logger.print("");
            stage.setScene(SceneFactory.getScene(SceneFactory.SceneType.ENTRY, defaultSceneConfiguration, this));
        });
    }

    public void showRegister() {
        Platform.runLater(() -> {
            Logger.print("");
            stage.setScene(SceneFactory.getScene(SceneFactory.SceneType.REGISTER, defaultSceneConfiguration, this));
        });
    }

    public void showJoinAsGuest() {
        Platform.runLater(() -> {
            Logger.print("");
            stage.setScene(SceneFactory.getScene(SceneFactory.SceneType.JOINASGUEST, defaultSceneConfiguration, this));
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

    private void showError(@Nullable String hostErrorMessage, @Nullable String usernameErrorMessage) {
        Map<String, String> params = new HashMap<>();
        params.put(OPTION_PARAM_HOST, chatService.getHost());
        params.put(OPTION_PARAM_USERNAME, currentUser.getName());

        if (hostErrorMessage != null) {
            params.put(OPTION_PARAM_HOST_ERROR, hostErrorMessage);
        }

        if (usernameErrorMessage != null) {
            params.put(OPTION_PARAM_USERNAME_ERROR, usernameErrorMessage);
        }

        Platform.runLater(() -> {
            Logger.print("");
            stage.setScene(SceneFactory.getScene(SceneFactory.SceneType.ENTRY, defaultSceneConfiguration, this, params));
        });
    }
}
