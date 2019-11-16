package atafelska.chat.client.data;

import atafelska.chat.User;

import java.util.List;
import java.util.Observable;

public class UsersObservable extends Observable {
    private List<User> users;

    public void updateUsers(List<User> users) {
        this.users = users;
        notifyObservers();
    }

    @Override
    public void notifyObservers() {
        super.setChanged();
        notifyObservers(users);
    }
}
