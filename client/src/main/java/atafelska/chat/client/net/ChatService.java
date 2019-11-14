package atafelska.chat.client.net;

import atafelska.chat.*;
import io.grpc.Channel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.stub.StreamObserver;

public class ChatService {
    private static final int PORT = 9000;

    private ChatServiceGrpc.ChatServiceStub asyncStub;
    private Channel channel;

    public ChatService(String host) {
        this(ManagedChannelBuilder.forAddress(host, PORT).usePlaintext());
    }

    private ChatService(ManagedChannelBuilder channelBuilder) {
        channel = channelBuilder.build();
        asyncStub = ChatServiceGrpc.newStub(channel);
    }

    public void getChat(User request, StreamObserver<Chat> responseObserver) {
        asyncStub.getChat(request, responseObserver);
    }

    public void unsubscribe(User request, StreamObserver<User> responseObserver) {
        asyncStub.unsubscribe(request, responseObserver);
    }

    public void observeMessages(User request, StreamObserver<Message> responseObserver) {
        asyncStub.observeMessages(request, responseObserver);
    }

    public void observeUsers(User request, StreamObserver<CurrentUsers> responseObserver) {
        asyncStub.observeUsers(request, responseObserver);
    }

    public void sendMessage(Message request, StreamObserver<Message> responseObserver) {
        asyncStub.sendMessage(request, responseObserver);
    }
}
