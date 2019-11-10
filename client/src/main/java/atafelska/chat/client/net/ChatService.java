package atafelska.chat.client.net;

import atafelska.chat.Chat;
import atafelska.chat.ChatServiceGrpc;
import atafelska.chat.User;
import io.grpc.Channel;
import io.grpc.ManagedChannelBuilder;

public class ChatService {

    private ChatServiceGrpc.ChatServiceBlockingStub blockingStub;
    private ChatServiceGrpc.ChatServiceStub asyncStub;
    private Channel channel;

    public ChatService(String host, int port) {
        this(ManagedChannelBuilder.forAddress(host, port).usePlaintext());
    }

    private ChatService(ManagedChannelBuilder channelBuilder) {
        channel = channelBuilder.build();
        blockingStub = ChatServiceGrpc.newBlockingStub(channel);
        asyncStub = ChatServiceGrpc.newStub(channel);
    }

    public void getChat(User user) {
        Chat chat = blockingStub.getChat(user);
    }

}
