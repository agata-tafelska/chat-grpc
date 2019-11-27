package atafelska.chat.server;

import atafelska.chat.*;
import atafelska.chat.server.storage.ChatStorage;
import io.grpc.Status;
import io.grpc.StatusRuntimeException;
import io.grpc.stub.StreamObserver;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ChatServerService extends ChatServiceGrpc.ChatServiceImplBase {

    private ChatStorage chatStorage;
    private Map<User, ChatObserver> observers = new HashMap<>();

    ChatServerService(ChatStorage chatStorage) {
        this.chatStorage = chatStorage;
        initializeUsersConnectionPoller();
    }

    private void initializeUsersConnectionPoller() {
        new Thread(() -> {
            Logger.print("Starting users connection polling");
            while (true) {
                try {
                    Thread.sleep(1000);
                    updateCurrentUsers();
                } catch (InterruptedException exception) {
                    exception.printStackTrace();
                }
            }
        }).start();
    }

    @Override
    public void getChat(User request, StreamObserver<Chat> responseObserver) {
        Logger.print("User: " + request + " joined chat");
        if (observers.containsKey(request)) {
            responseObserver.onError(Status.ALREADY_EXISTS.asRuntimeException());
            Logger.print("User with name: " + request.getName() + " already exists. Returning `ALREADY_EXISTS` error.");
            return;
        }
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
        List<User> disconnectedUsers = new ArrayList<>();
        observers.forEach(
                (user, chatObserver) -> {
                    if (chatObserver.usersObserver != null) {
                        try {
                            Logger.print("Sending current users to: " + user);
                            chatObserver.usersObserver.onNext(currentUsers);
                        } catch (StatusRuntimeException exception) {
                            Logger.print("Unable to send users updates to: " + user);
                            disconnectedUsers.add(user);
                        }
                    }
                }
        );
        disconnectedUsers.forEach(user -> observers.remove(user));
    }

    @Override
    public void observeUsers(User request, StreamObserver<CurrentUsers> responseObserver) {
        Logger.print("User: " + request.getName() + " starting observing users");
        observers.get(request).usersObserver = responseObserver;
    }

    @Override
    public void observeMessages(User request, StreamObserver<Message> responseObserver) {
        Logger.print("User: " + request.getName() + " starting observing messages");
        observers.get(request).messageObserver = responseObserver;
    }

    @Override
    public void sendMessage(Message request, StreamObserver<Message> responseObserver) {
        Logger.print("**Message** " + request.getUser().getName() + ":[" + request.getText() + "]");
        chatStorage.addMessage(request);

        List<User> disconnectedUsers = new ArrayList<>();
        observers.forEach(
                (user, chatObserver) -> {
                    if (chatObserver.messageObserver != null) {
                        try {
                            Logger.print("Sending message updates to: " + user);
                            chatObserver.messageObserver.onNext(request);
                        } catch (StatusRuntimeException exception) {
                            Logger.print("Unable to send message updates to: " + user);
                            disconnectedUsers.add(user);
                        }
                    }
                }
        );
        disconnectedUsers.forEach(user -> observers.remove(user));
    }

    private static class ChatObserver {
        private StreamObserver<Message> messageObserver;
        private StreamObserver<CurrentUsers> usersObserver;
    }
}
