package server.model;

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

    public void addUser(User user) {
        connectedUsers.put(user.getUserId(), user);

        if (coordinatorId == null) { // First user is the coordinator
            coordinatorId = user.getUserId();
            user.toCoordinator();
        }
    }

    public void removeUser(String userId) {
        connectedUsers.remove(userId);

        if (userId.equals(coordinatorId) && !connectedUsers.isEmpty()) { // Reassign coordinator role if previous one leaves
            coordinatorId = connectedUsers.keySet().iterator().next();
            connectedUsers.get(coordinatorId).toCoordinator();
        }
    }

    public User getUser(String userId) {
        return connectedUsers.get(userId);
    }

    public String getCoordinatorId() {
        return coordinatorId;
    }

    public Collection<User> getUsers() {
        return connectedUsers.values();
    }

    public String[] getUserList() {
        return connectedUsers.keySet().toArray(new String[0]);
    }
    
    public void toggleUserStatus(String userId) {
        User user = connectedUsers.get(userId);
        user.toggleStatus();
    }
}
