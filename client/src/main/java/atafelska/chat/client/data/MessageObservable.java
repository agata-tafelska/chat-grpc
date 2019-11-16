package atafelska.chat.client.data;

import atafelska.chat.Message;

import java.util.ArrayList;
import java.util.Observable;

public class MessageObservable extends Observable {
    private ArrayList<Message> messages;

    public void setMessages(ArrayList<Message> messages) {
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
