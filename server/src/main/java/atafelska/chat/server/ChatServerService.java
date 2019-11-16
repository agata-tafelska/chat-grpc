package atafelska.chat.server;

import atafelska.chat.*;
import atafelska.chat.server.storage.ChatStorage;
import io.grpc.stub.StreamObserver;

import java.util.HashMap;
import java.util.Map;

public class ChatServerService extends ChatServiceGrpc.ChatServiceImplBase {

    private ChatStorage chatStorage;
    private Map<User, ChatObserver> observers = new HashMap<>();

    ChatServerService(ChatStorage chatStorage) {
        this.chatStorage = chatStorage;
    }

    @Override
    public void getChat(User request, StreamObserver<Chat> responseObserver) {
        Logger.print("User: " + request + " joined chat");
        observers.put(request, new ChatObserver());
        responseObserver.onNext(
                Chat.newBuilder()
                        .addAllUser(observers.keySet())
                        .addAllMessages(chatStorage.getMessages())
                        .build()
        );
        responseObserver.onCompleted();

        updateCurrentUsers();
    }

    @Override
    public void unsubscribe(User request, StreamObserver<User> responseObserver) {
        Logger.print("User: " + request + " exited chat");
        observers.remove(request);
        responseObserver.onNext(request);
        responseObserver.onCompleted();

        updateCurrentUsers();
    }

    private void updateCurrentUsers() {
        CurrentUsers currentUsers = CurrentUsers.newBuilder().addAllUsers(observers.keySet()).build();
        observers.forEach(
                (user, chatObserver) -> {
                    if (chatObserver.usersObserver != null) {
                        Logger.print("Sending current users to: " + user);
                        chatObserver.usersObserver.onNext(currentUsers);
                    }
                }
        );
    }

    @Override
    public void observeUsers(User request, StreamObserver<CurrentUsers> responseObserver) {
        observers.get(request).usersObserver = responseObserver;
    }

    @Override
    public void observeMessages(User request, StreamObserver<Message> responseObserver) {
        observers.get(request).messageObserver = responseObserver;
    }

    @Override
    public void sendMessage(Message request, StreamObserver<Message> responseObserver) {
        chatStorage.addMessage(request);
        observers.forEach(
                (user, chatObserver) -> {
                    if (chatObserver.messageObserver != null) {
                        chatObserver.messageObserver.onNext(request);
                    }
                }
        );
    }

    private static class ChatObserver {
        private StreamObserver<Message> messageObserver;
        private StreamObserver<CurrentUsers> usersObserver;
    }
}
