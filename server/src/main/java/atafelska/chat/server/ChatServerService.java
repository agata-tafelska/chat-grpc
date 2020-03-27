package atafelska.chat.server;

import atafelska.chat.*;
import atafelska.chat.server.storage.ChatStorage;
import io.grpc.Status;
import io.grpc.StatusRuntimeException;
import io.grpc.stub.StreamObserver;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import java.util.*;

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
    public void register(User request, StreamObserver<User> responseObserver) {
        Logger.print("User " + request.getName() + " wants to register.");
        List<User> users = chatStorage.getUsers();
        if (users != null) {
            for (User user : users) {
                if (request.getName().equals(user.getName())) {
                    responseObserver.onError(Status.ALREADY_EXISTS.asRuntimeException());
                    Logger.print("User with name: " + request.getName() + " already exists. Returning `ALREADY_EXISTS` error.");
                    return;
                }
            }
        }

        User createdUser = User.newBuilder()
                .setId(setUserId())
                .setName(request.getName())
                .setPassword(request.getPassword())
                .build();

        Logger.print("User id: " + createdUser.getId());

        chatStorage.addUser(createdUser);
        responseObserver.onNext(createdUser);
        responseObserver.onCompleted();
    }

    @Override
    public void getChat(User request, StreamObserver<Chat> responseObserver) {
        Logger.print("User: " + request.getName() + " wants to join chat");
        if (!areCredentialsCorrect(request)) {
            responseObserver.onError(Status.UNAUTHENTICATED.asRuntimeException());
            Logger.print("Login credentials are not correct. UNAUTHENTICATED error returned.");
            return;
        }

        User currentUser;

        if (request.getIsGuest()) {
            currentUser = User.newBuilder()
                    .setId(setUserId())
                    .setName(request.getName())
                    .setIsGuest(true)
                    .build();
        } else {
            String currentUserId = getUserId(request);
            if (currentUserId == null) {
                responseObserver.onError(Status.UNAUTHENTICATED.asRuntimeException());
                Logger.print("Login credentials are not correct. UNAUTHENTICATED error returned.");
                return;
            }
            currentUser = User.newBuilder()
                    .setId(currentUserId)
                    .setName(request.getName())
                    .setPassword(request.getPassword())
                    .setIsGuest(false)
                    .build();
            Logger.print("Current user: " + currentUser.getId() + " " + currentUser.getName() + " " + currentUser.getPassword());
        }

        observers.put(currentUser, new ChatObserver());

        responseObserver.onNext(
                Chat.newBuilder()
                        .addAllUser(observers.keySet())
                        .addAllMessages(chatStorage.getMessages())
                        .setCurrentUser(currentUser)
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

    private boolean areCredentialsCorrect(User requestUser) {
        if (requestUser.getIsGuest()) {
            return true;
        }
        List<User> users = chatStorage.getUsers();
        if (users == null) {
            return false;
        }
        for (User user : users) {
            if (user.getName().equals(requestUser.getName())) {
                return user.getPassword().equals(requestUser.getPassword());
            }
        }
        return false;
    }

    private byte[] hashPassword(String password) throws NoSuchAlgorithmException, InvalidKeySpecException {
        SecureRandom random = new SecureRandom();
        byte[] salt = new byte[16];
        random.nextBytes(salt);

        KeySpec spec = new PBEKeySpec(password.toCharArray(), salt, 65536, 128);
        SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");

        byte[] hash = factory.generateSecret(spec).getEncoded();
        return hash;
    }

    private String setUserId() {
        return String.valueOf(UUID.randomUUID());
    }

    private String getUserId(User user) {
        List<User> users = chatStorage.getUsers();
        String id = null;
        for (User storedUser: users) {
            if (user.getName().equals(storedUser.getName())) {
                id = storedUser.getId();
            }
        }
        return id;
    }
}
