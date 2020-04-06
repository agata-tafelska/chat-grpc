package atafelska.chat.client.controllers;

import atafelska.chat.client.core.Logger;
import atafelska.chat.client.utils.InputUtils;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.AnchorPane;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;

import java.util.Map;

import static atafelska.chat.client.TextConstants.INCORRECT_HOST_MESSAGE;
import static atafelska.chat.client.TextConstants.INCORRECT_USERNAME_MESSAGE;
import static atafelska.chat.client.core.SceneCoordinator.*;

public class EntryController extends BaseController {

    @FXML
    private AnchorPane entryPane;
    @FXML
    private TextField editTextHost;
    @FXML
    private TextField editTextUsername;
    @FXML
    private PasswordField passwordField;
    @FXML
    private Label accountCreatedLabel;
    @FXML
    private Label messageLabel;

    @Override
    public void onLoaded() {
        super.onLoaded();
        entryPane.setOnKeyPressed(keyEvent -> {
            if (keyEvent.getCode() == KeyCode.ENTER){
                onSignInButtonClicked();
            }
        });
    }

    @Override
    public void onOptionalParamsLoaded(Map<String, String> optionalParams) {
        super.onOptionalParamsLoaded(optionalParams);
        if (optionalParams == null) return;

        if (optionalParams.containsKey(OPTION_PARAM_USERNAME_ERROR)) {
            messageLabel.setText(optionalParams.get(OPTION_PARAM_USERNAME_ERROR));
            messageLabel.setVisible(true);
        }

        if (optionalParams.containsKey(OPTION_PARAM_HOST_ERROR)) {
            messageLabel.setText(optionalParams.get(OPTION_PARAM_HOST_ERROR));
            messageLabel.setVisible(true);
        }

        if (optionalParams.containsKey(OPTION_PARAM_HOST)) {
            editTextHost.setText(optionalParams.get(OPTION_PARAM_HOST));
        }

        if (optionalParams.containsKey(OPTION_PARAM_USERNAME)) {
            editTextUsername.setText(optionalParams.get(OPTION_PARAM_USERNAME));
        }

        if (optionalParams.containsKey(OPTION_PARAM_ACCOUNT_CREATED)) {
            accountCreatedLabel.setText(optionalParams.get(OPTION_PARAM_ACCOUNT_CREATED));
            accountCreatedLabel.setVisible(true);
        }
    }

    public void onSignInButtonClicked() {
        String host = editTextHost.getText();
        String username = editTextUsername.getText();
        String password = passwordField.getText();

        if (!InputUtils.isValidHost(host)) {
            Logger.print("Invalid host entered: " + host + ", returning");
            showInvalidHostError();
            return;
        }

        Logger.print("Selected host: " + host + " and username: " + username);
        sceneCoordinator.joinChat(host, username, password, false);
    }

    public void onCreateAccountClicked() {
        sceneCoordinator.showRegister();
    }

    public void onJoinAsGuestClicked() {
        sceneCoordinator.showJoinAsGuest();
    }

    private void showInvalidHostError() {
        messageLabel.setText(INCORRECT_HOST_MESSAGE);
        messageLabel.setVisible(true);
    }
}
