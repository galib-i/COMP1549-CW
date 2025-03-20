package server.model;

import java.io.PrintWriter;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;

public class UserManager {
    private final Map<String, User> connectedUsers;
    private String coordinatorId;

    public UserManager() {
        this.connectedUsers = new LinkedHashMap<>(); // Keeps insertion order
        this.coordinatorId = null;
    }

    public boolean addUser(String userId, String socketAddress, PrintWriter writer) {
        if (connectedUsers.containsKey(userId)) {
            return false;
        }

        User user = new User(userId, socketAddress, writer);
        if (connectedUsers.isEmpty()) { // Sets the first user as the coordinator
            user.toCoordinator();
            coordinatorId = userId;
        }

        connectedUsers.put(userId, user);
        return true;
    }

    public void removeUser(String userId) {
        connectedUsers.remove(userId);
    
        if (userId.equals(coordinatorId) && !connectedUsers.isEmpty()) { // Reassigns coordinator role to the next user
            User newCoordinator = connectedUsers.entrySet().iterator().next().getValue();
            newCoordinator.toCoordinator();
            coordinatorId = newCoordinator.getUserId();
        }
    }
        
    public Collection<User> getUsers() {
        return connectedUsers.values();
    }

    public String[] getUserIds() {
        return connectedUsers.keySet().toArray(new String[0]);
    }

    public User getUser(String userId) {
        return connectedUsers.get(userId);
    }

    public String getCoordinatorId() {
        return coordinatorId;
    }
    
    public void toggleUserStatus(String userId) {
        User user = connectedUsers.get(userId);
        user.toggleStatus();
    }
}
