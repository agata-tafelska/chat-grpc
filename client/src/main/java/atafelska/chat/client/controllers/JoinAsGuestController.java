package atafelska.chat.client.controllers;

import atafelska.chat.client.core.Logger;
import atafelska.chat.client.utils.InputUtils;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.AnchorPane;

import static atafelska.chat.client.TextConstants.INCORRECT_HOST_MESSAGE;

public class JoinAsGuestController extends BaseController {

    @FXML
    private AnchorPane joinAsGuestPane;
    @FXML
    private TextField editTextHost;
    @FXML
    private Label incorrectHostError;

    @Override
    public void onLoaded() {
        super.onLoaded();
        joinAsGuestPane.setOnKeyPressed(keyEvent -> {
            if (keyEvent.getCode() == KeyCode.ENTER){
                onJoinAsGuestButtonClicked();
            }
        });
    }

    public void onJoinAsGuestButtonClicked() {
        String host = editTextHost.getText();
        if (!InputUtils.isValidHost(host)) {
            Logger.print("Invalid host entered: " + host + ", returning");
            showInvalidHostError();
            return;
        }
        sceneCoordinator.joinChat(host, "guest", "", true);
    }

    private void showInvalidHostError() {
        incorrectHostError.setText(INCORRECT_HOST_MESSAGE);
        incorrectHostError.setVisible(true);
    }

    public void onSignInLinkClicked() {
        sceneCoordinator.showEntry();
    }
}
