package server.model;

import java.io.PrintWriter;
import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class UserManager {
    private final Map<String, User> connectedUsers;

    public UserManager() {
        this.connectedUsers = new ConcurrentHashMap<>();
    }

    public boolean userExists(String userId) {
        return connectedUsers.containsKey(userId);
    }

    public void addUser(String userId, String connectionInfo, PrintWriter writer) {
        User user = new User(userId, connectionInfo, writer);
        connectedUsers.put(userId, user);
        broadcastUserList();
    }

    public void removeUser(String userId) {
        connectedUsers.remove(userId);
        broadcastUserList();
    }
    
    public Set<String> getUserIds() {
        return connectedUsers.keySet();
    }
    
    public Collection<User> getUsers() {
        return connectedUsers.values();
    }
    
    public User getUserById(String userId) {
        return connectedUsers.get(userId);
    }
    
    private void broadcastUserList() {
        for (User user : getUsers()) {
            user.getWriter().println("[USERLIST]: Current users: " + getUserIds());
        }
    }
}
