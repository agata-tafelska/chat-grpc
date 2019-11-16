package atafelska.chat.client.net;

import atafelska.chat.client.core.Logger;
import io.grpc.stub.StreamObserver;

public abstract class DefaultStreamObserver<T> implements StreamObserver<T> {

    @Override
    public void onError(Throwable t) {
        Logger.print("Unable to receive data. Reason: " + t.getMessage());
        t.printStackTrace();
    }

    @Override
    public void onCompleted() {
        // Do nothing
    }
}
