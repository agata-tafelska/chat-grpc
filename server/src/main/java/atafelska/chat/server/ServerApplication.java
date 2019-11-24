package atafelska.chat.server;

import atafelska.chat.server.storage.ChatStorage;
import atafelska.chat.server.storage.FileChatStorage;
import atafelska.chat.server.storage.MemoryChatStorage;
import io.grpc.Server;
import io.grpc.ServerBuilder;

import java.io.IOException;

public class ServerApplication {

    public static void main(String[] args) {
        Logger.print("Starting server...");

        ChatStorage chatStorage;
        try {
            chatStorage = new FileChatStorage();
        } catch (Exception exception) {
            exception.printStackTrace();
            chatStorage = new MemoryChatStorage();
            Logger.print("Unable to initialize file storage. Fallback to in-memory storage.");
        }
        ChatServerService chatServerService = new ChatServerService(chatStorage);

        int port = 9000;
        Server server = ServerBuilder.forPort(port)
                .addService(chatServerService)
                .build();

        try {
            Logger.print("Listening on port: " + port);
            server.start();
            server.awaitTermination();
        } catch (IOException | InterruptedException exception) {
            Logger.print("Error occurred during server running. " + exception.getMessage());
            exception.printStackTrace();
        }
    }
}
