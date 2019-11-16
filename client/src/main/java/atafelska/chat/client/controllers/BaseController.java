package atafelska.chat.client.controllers;

import atafelska.chat.client.core.SceneCoordinator;

public abstract class BaseController {
    protected SceneCoordinator sceneCoordinator;

    public void setSceneCoordinator(SceneCoordinator sceneCoordinator) {
        this.sceneCoordinator = sceneCoordinator;
    }

    public void onLoaded() {

    }
}
