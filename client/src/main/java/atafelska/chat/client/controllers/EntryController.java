package atafelska.chat.client.controllers;

import atafelska.chat.client.core.Logger;
import atafelska.chat.client.utils.InputUtils;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.AnchorPane;

import static atafelska.chat.client.TextConstants.INCORRECT_HOST_MESSAGE;
import static atafelska.chat.client.TextConstants.INCORRECT_USERNAME_MESSAGE;

public class EntryController extends BaseController {

    @FXML
    private AnchorPane entryPane;
    @FXML
    private TextField editTextHost;
    @FXML
    private TextField editTextUsername;
    @FXML
    private Label incorrectUsernameError;
    @FXML
    private Label incorrectHostError;

    @Override
    public void onLoaded() {
        super.onLoaded();
        entryPane.setOnKeyPressed(keyEvent -> {
            if (keyEvent.getCode() == KeyCode.ENTER){
                onJoinButtonClicked();
            }
        });
    }

    public void onJoinButtonClicked() {
        String host = editTextHost.getText();
        String username = editTextUsername.getText();

        if (!InputUtils.isValidHost(host)) {
            Logger.print("Invalid host entered: " + host + ", returning");
            showInvalidHostError();
            return;
        }

        if (!InputUtils.isValidUserName(username)) {
            Logger.print("Invalid username entered: " + username + ", returning");
            showInvalidUsernameError();
            return;
        }

        Logger.print("Selected host: " + host + " and username: " + username);
        sceneCoordinator.joinChat(host, username);
    }

    public void showInvalidHostError() {
        incorrectHostError.setText(INCORRECT_HOST_MESSAGE);
        incorrectHostError.setVisible(true);
    }

    public void showInvalidUsernameError() {
        incorrectUsernameError.setText(INCORRECT_USERNAME_MESSAGE);
        incorrectUsernameError.setVisible(true);
    }
}
