package atafelska.chat.client.controllers;

import atafelska.chat.client.core.Logger;
import atafelska.chat.client.utils.InputUtils;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;

public class EntryController extends BaseController {

    @FXML
    private TextField editTextHost;
    @FXML
    private TextField editTextUsername;

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
        // TODO Find nice way to show error, search for JavaFX standards
    }

    public void showInvalidUsernameError() {
        // TODO Find nice way to show error, search for JavaFX standards
    }
}
