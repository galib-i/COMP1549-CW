package server.model;

import java.util.HashSet;

public class UserManager {
    private final HashSet<String> users;

    public UserManager() {
        this.users = new HashSet<>();
    }

}
