package server.model;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Allows the server to manage connected users and their details
 */
public class UserManager {
    private final Map<String, User> connectedUsers;
    private String coordinatorId;

    public UserManager() {
        this.connectedUsers = new LinkedHashMap<>(); // Maintains join order
        this.coordinatorId = null;
    }

    /**
     * Registers user to the server and if the first one, assign coordinator role
     * @param user Object of the user to be added
     */
    public void addUser(User user) {
        connectedUsers.put(user.getUserId(), user);

        if (coordinatorId == null) {
            coordinatorId = user.getUserId();
            user.promoteToCoordinator();
        }
    }

    /**
     * Removes user from the server and assigns coordinator role to the next user (start of the LinkedHashMap)
     * @param userId Id of the user to be removed
     */
    public void removeUser(String userId) {
        connectedUsers.remove(userId);

        if (userId.equals(coordinatorId) && !connectedUsers.isEmpty()) {
            coordinatorId = connectedUsers.keySet().iterator().next();
            connectedUsers.get(coordinatorId).promoteToCoordinator();
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

    /**
     * Returns user details based on the request
     * @param userId Id of the requested user
     * @param allDetails True: include all details else only role and status
     * @return Map of user details
     */
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
        // Key: userId, Value: userDetails
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
