package atafelska.chat.client.controllers;

import atafelska.chat.Message;
import atafelska.chat.User;
import atafelska.chat.client.core.Logger;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.layout.VBox;

import java.util.List;
import java.util.Observer;

public class ChatboardController extends BaseController {

    @FXML
    private Button buttonLogout;
    @FXML
    private Button buttonSend;
    @FXML
    private VBox listMessages;
    @FXML
    private VBox listUsers;
    @FXML
    private TextArea editTextMessage;

    @SuppressWarnings("unchecked")
    private Observer usersObserver =
            (observable, argument) -> {
                Logger.print("Users update received, updating UI");
                List users = ((List) argument);
                updateUsers(users);
            };

    @SuppressWarnings("unchecked")
    private Observer messagesObserver =
            (observable, argument) -> {
                Logger.print("Messages update received, updating UI");
                List messages = ((List) argument);
                updateMessages(messages);
            };

    @Override
    public void onLoaded() {
        super.onLoaded();
        Logger.print("Starting observing chat");
        sceneCoordinator.observeChat(usersObserver, messagesObserver);
    }

    private void updateUsers(List<User> users) {
        Logger.print("Showing users");
        users.forEach(
                user -> Logger.print(user.toString())
        );
    }

    private void updateMessages(List<Message> messages) {
        Logger.print("Showing messages");
        messages.forEach(
                message -> Logger.print(message.toString())
        );
    }
}
