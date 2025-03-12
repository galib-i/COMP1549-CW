package server.model;

import java.io.PrintWriter;
import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class UserManager {
    private final Map<String, User> connectedUsers;

    public UserManager() {
        this.connectedUsers = new ConcurrentHashMap<>();
    }

    public boolean addUser(String userId, String connectionInfo, PrintWriter writer) {
        if (connectedUsers.containsKey(userId)) {
            return false;
        }

        User user = new User(userId, connectionInfo, writer);
        connectedUsers.put(userId, user);

        return true;
    }

    public void removeUser(String userId) {
        connectedUsers.remove(userId);
    }
    
    public String[] getUserIds() {
        return connectedUsers.keySet().toArray(new String[0]);
    }
    
    public Collection<User> getUsers() {
        return connectedUsers.values();
    }
    
    public User getUserById(String userId) {
        return connectedUsers.get(userId);
    }

}
