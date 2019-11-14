package atafelska.chat.client.chatboard;

import atafelska.chat.client.core.BaseController;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.layout.VBox;

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
}
