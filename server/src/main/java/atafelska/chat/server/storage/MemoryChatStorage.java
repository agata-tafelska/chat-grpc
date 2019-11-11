package atafelska.chat.server.storage;

import atafelska.chat.Message;
import atafelska.chat.server.Logger;

import java.util.ArrayList;
import java.util.List;

public class MemoryChatStorage implements ChatStorage {

    private List<Message> messages = new ArrayList<>();

    @Override
    public List<Message> getMessages() {
        return messages;
    }

    @Override
    public void addMessage(Message message) {
        Logger.print("Adding message: " + message);
        messages.add(message);
    }

    @Override
    public void clearChat() {
        Logger.print("Clearing chat");
        messages.clear();
    }
}
