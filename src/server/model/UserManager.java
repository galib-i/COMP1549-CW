package server.model;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;

public class UserManager {
    private final Map<String, User> connectedUsers;
    private String coordinatorId;

    public UserManager() {
        this.connectedUsers = new LinkedHashMap<>();
        this.coordinatorId = null;
    }

    public void addUser(User user) {
        connectedUsers.put(user.getUserId(), user);

        if (coordinatorId == null) {
            coordinatorId = user.getUserId();
            user.toCoordinator();
        }
    }

    public void removeUser(String userId) {
        connectedUsers.remove(userId);

        if (userId.equals(coordinatorId) && !connectedUsers.isEmpty()) {
            coordinatorId = connectedUsers.keySet().iterator().next();
            connectedUsers.get(coordinatorId).toCoordinator();
        }
    }

    public User getUser(String userId) {
        return connectedUsers.get(userId);
    }

    public Collection<User> getUsers() {
        return connectedUsers.values();
    }

    public String getCoordinatorId() {
        return coordinatorId;
    }

    public Map<String, String> getUserDetails(String userId, boolean allDetails) {
        User user = connectedUsers.get(userId);
        Map<String, String> userDetails = new LinkedHashMap<>();

        if (allDetails) {
            userDetails.put("userId", user.getUserId());
            userDetails.put("socketAddress", user.getSocketAddress());
        }

        userDetails.put("role", user.getRole().toString());
        userDetails.put("status", user.getStatus().toString());

        return userDetails;
    }

    public Map<String, Map<String, String>> getAllUserDetails() {
        Map<String, Map<String, String>> allDetails = new LinkedHashMap<>();

        connectedUsers.forEach((userId, user) -> 
            allDetails.put(userId, getUserDetails(userId, false))
        );
        
        return allDetails;
    }

    public void toggleUserStatus(String userId) {
        User user = connectedUsers.get(userId);
        user.toggleStatus();
    }
}
