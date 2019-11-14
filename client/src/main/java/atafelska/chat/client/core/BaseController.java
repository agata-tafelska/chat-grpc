package atafelska.chat.client.core;

public abstract class BaseController {
    protected SceneCoordinator sceneCoordinator;

    public void setSceneCoordinator(SceneCoordinator sceneCoordinator) {
        this.sceneCoordinator = sceneCoordinator;
    }
}
