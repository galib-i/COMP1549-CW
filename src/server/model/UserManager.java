package server.model;

import java.util.HashSet;

public class UserManager {
    private final HashSet<String> users;

    public UserManager() {
        this.users = new HashSet<>();
    }

    public void addUser(String userId) {
        users.add(userId);
        printCurrentUsers();
    }

    public void removeUser(String userId) {
        users.remove(userId);
        printCurrentUsers();
    }

    private void printCurrentUsers() {
        if (!users.isEmpty()) {
            users.forEach(user -> System.out.println(user));
        }
    }
}
