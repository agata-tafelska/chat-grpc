package atafelska.chat.client.controllers;

import atafelska.chat.client.core.SceneCoordinator;

import java.util.Map;

public abstract class BaseController {
    protected SceneCoordinator sceneCoordinator;

    public void setSceneCoordinator(SceneCoordinator sceneCoordinator) {
        this.sceneCoordinator = sceneCoordinator;
    }

    /*
     Below method is called directly after Controller is loaded from FXML
     Override it for initializing UI logic
     */
    public void onLoaded() {

    }

    /*
     Below method is called after above `onLoaded` only when optional parameters are passed to Controller
     Override it when scene needs initial state stored in parameters
     */
    public void onOptionalParamsLoaded(Map<String, String> optionalParams) {

    }
}
