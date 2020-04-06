package atafelska.chat.client.controllers;

import atafelska.chat.client.core.Logger;
import atafelska.chat.client.utils.InputUtils;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.AnchorPane;

import java.util.Map;

import static atafelska.chat.client.TextConstants.*;
import static atafelska.chat.client.core.SceneCoordinator.*;
import static atafelska.chat.client.core.SceneCoordinator.OPTION_PARAM_USERNAME;

public class RegisterController extends BaseController {

    @FXML
    private AnchorPane registerPane;
    @FXML
    private TextField editTextHost;
    @FXML
    private TextField editTextUsername;
    @FXML
    private PasswordField passwordField;
    @FXML
    private PasswordField confirmPasswordField;
    @FXML
    private Label messageLabel;

    @Override
    public void onLoaded() {
        super.onLoaded();
        registerPane.setOnKeyPressed(keyEvent -> {
            if (keyEvent.getCode() == KeyCode.ENTER){
                onRegisterButtonClicked();
            }
        });
    }

    @Override
    public void onOptionalParamsLoaded(Map<String, String> optionalParams) {
        super.onOptionalParamsLoaded(optionalParams);
        if (optionalParams == null) return;
        if (optionalParams.containsKey(OPTION_PARAM_USERNAME_EXISTS_ERROR)) {
            messageLabel.setText(optionalParams.get(OPTION_PARAM_USERNAME_EXISTS_ERROR));
            messageLabel.setVisible(true);
        }
    }

    public void onSignInLinkClicked() {
        sceneCoordinator.showEntry();
    }

    public void onRegisterButtonClicked() {
        String host = editTextHost.getText();
        String username = editTextUsername.getText();
        String password = passwordField.getText();
        String confirmPassword = confirmPasswordField.getText();

        if (!InputUtils.isValidHost(host)) {
            Logger.print("Invalid host entered: " + host + ", returning");
            showInvalidHostError();
        } else if (!InputUtils.isValidUserName(username)) {
            Logger.print("Invalid username entered: " + username + ", returning");
            showInvalidUsernameError();
        } else if (!InputUtils.isValidPassword(password)) {
            Logger.print("Invalid password entered: " + password + ", returning");
            showInvalidPasswordError();
        } else if (!password.equals(confirmPassword)) {
            Logger.print("Passwords do not match: " + password + " " + confirmPassword +  ", returning");
            showPasswordMismatchError();
        } else {
            sceneCoordinator.registerUser(host, username, password);
        }
    }

    public void showInvalidHostError() {
        messageLabel.setText(INCORRECT_HOST_MESSAGE);
        messageLabel.setVisible(true);
    }

    public void showInvalidUsernameError() {
        messageLabel.setText(INCORRECT_USERNAME_MESSAGE);
        messageLabel.setVisible(true);
    }

    public void showInvalidPasswordError() {
        messageLabel.setText(INCORRECT_PASSWORD_MESSAGE);
        messageLabel.setVisible(true);
    }

    public void showPasswordMismatchError() {
        messageLabel.setText(PASSWORD_MISMATCH_ERROR);
        messageLabel.setVisible(true);
    }
}
