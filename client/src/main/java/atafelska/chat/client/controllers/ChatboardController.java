package atafelska.chat.client.controllers;

import atafelska.chat.Message;
import atafelska.chat.User;
import atafelska.chat.client.core.Logger;
import atafelska.chat.client.utils.InputUtils;
import javafx.application.Platform;
import javafx.collections.ObservableListBase;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;

import java.util.List;
import java.util.Observer;

public class ChatboardController extends BaseController {

    @FXML
    private Button buttonLogout;
    @FXML
    private Button buttonSend;
    @FXML
    private ListView messagesListView;
    @FXML
    private ListView usersListView;
    @FXML
    private TextArea editTextMessage;

    @SuppressWarnings("unchecked")
    private Observer usersObserver =
            (observable, argument) -> {
                Logger.print("Users update received, updating UI");
                Platform.runLater(() -> {
                    List users = ((List) argument);
                    updateUsers(users);
                });
            };

    @SuppressWarnings("unchecked")
    private Observer messagesObserver =
            (observable, argument) -> {
                Logger.print("Messages update received, updating UI");
                Platform.runLater(() -> {
                    List messages = ((List) argument);
                    updateMessages(messages);
                });
            };

    @Override
    public void onLoaded() {
        super.onLoaded();
        initButtons();

        Logger.print("Starting observing chat");
        sceneCoordinator.observeChat(usersObserver, messagesObserver);
    }

    private void initButtons() {
        buttonSend.setOnAction(actionEvent -> sendMessage());
        buttonLogout.setOnAction(actionEvent -> sceneCoordinator.logout());
    }

    @SuppressWarnings("unchecked")
    private void updateUsers(List<User> users) {
        Logger.print("Showing users");
        ObservableListBase observableListBase = new ObservableListBase() {
            @Override
            public Object get(int index) {
                return users.get(index).getName();
            }

            @Override
            public int size() {
                return users.size();
            }
        };

        usersListView.setItems(observableListBase);
    }

    @SuppressWarnings("unchecked")
    private void updateMessages(List<Message> messages) {
        Logger.print("Showing messages");
        ObservableListBase observableListBase = new ObservableListBase() {
            @Override
            public Object get(int index) {
                return messages.get(index).getUser().getName() + ": " + messages.get(index).getText();
            }

            @Override
            public int size() {
                return messages.size();
            }
        };

        messagesListView.setItems(observableListBase);
    }

    private void sendMessage() {
        String message = editTextMessage.getText();

        if (!InputUtils.isMessageValid(message)) {
            Logger.print("Invalid message entered: " + message + ", returning");
            showInvalidMessageError();
            return;
        }

        Logger.print("Sending message: " + message);
        sceneCoordinator.sendMessage(message);
    }

    private void showInvalidMessageError() {
        // TODO Find nice way to show error, search for JavaFX standards
    }
}
