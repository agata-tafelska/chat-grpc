package atafelska.chat.server.storage;

import atafelska.chat.Message;

import java.util.List;

public interface ChatStorage {

    List<Message> getMessages();

    void addMessage(Message message);

    void clearChat();
}
