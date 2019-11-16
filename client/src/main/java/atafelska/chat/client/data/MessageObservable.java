package atafelska.chat.client.data;

import atafelska.chat.Message;

import java.util.List;
import java.util.Observable;

public class MessageObservable extends Observable {
    private List<Message> messages;

    public void setMessages(List<Message> messages) {
        this.messages = messages;
        notifyObservers();
    }

    public void addMessage(Message message) {
        messages.add(message);
        notifyObservers();
    }

    @Override
    public void notifyObservers() {
        super.setChanged();
        notifyObservers(messages);
    }
}
