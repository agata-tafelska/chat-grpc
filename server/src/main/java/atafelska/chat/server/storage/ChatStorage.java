package atafelska.chat.server.storage;

import atafelska.chat.Message;
import atafelska.chat.User;

import java.util.List;

public interface ChatStorage {

    List<Message> getMessages();

    void addMessage(Message message);

    void addUser(User user);

    List<User> getUsers();

    void clearChat();
}
