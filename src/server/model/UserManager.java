package server.model;

import java.io.PrintWriter;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;

public class UserManager {
    private final Map<String, User> connectedUsers;

    public UserManager() {
        this.connectedUsers = new LinkedHashMap<>(); // Sorted by insertion order
    }

    public boolean addUser(String userId, String connectionInfo, PrintWriter writer) {
        if (connectedUsers.containsKey(userId)) {
            return false;
        }

        User user = new User(userId, connectionInfo, writer);
        if (connectedUsers.isEmpty()) { // Sets the first user as the coordinator
            user.setCoordinator();
        }

        connectedUsers.put(userId, user);
        return true;
    }

    public boolean removeUser(String userId) {
        boolean isCoordinator = userId.equals(getCoordinator());
        connectedUsers.remove(userId);
    
        if (isCoordinator && !connectedUsers.isEmpty()) { // Reassigns coordinator role if the current one leaves
            User newCoordinator = connectedUsers.entrySet().iterator().next().getValue();
            newCoordinator.setCoordinator();
            return true;
        }

        return false;
    }
        
    public Collection<User> getUsers() {
        return connectedUsers.values();
    }
    
    public String[] getUserIds() {
        return connectedUsers.keySet().toArray(new String[0]);
    }

    public User getUserById(String userId) {
        return connectedUsers.get(userId);
    }

    public String getCoordinator() {
        for (User user : connectedUsers.values()) {
            if (user.getRole().equals("COORDINATOR")) {
                return user.getUserId();
            }
        }

        return null;
    }
}
