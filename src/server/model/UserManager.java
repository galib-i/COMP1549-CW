package server.model;

import java.io.PrintWriter;
import java.util.concurrent.ConcurrentHashMap;
import java.util.Map;
import java.util.Set;
import java.util.Collection;

public class UserManager {
    private final Map<String, PrintWriter> connectedUsers;

    public UserManager() {
        this.connectedUsers = new ConcurrentHashMap<>();
    }

    public void addUser(String userId, PrintWriter writer) {
        connectedUsers.put(userId, writer);
        broadcastUserList();
    }

    public void removeUser(String userId) {
        connectedUsers.remove(userId);
        broadcastUserList();
    }
    
    public Set<String> getUsers() {
        return connectedUsers.keySet();
    }

    public Collection<PrintWriter> getUserWriters() {
        return connectedUsers.values();
    }

    private void broadcastUserList() {
        for (PrintWriter writer : getUserWriters()) {
            writer.println("[USERLIST]: Current users: " + getUsers());
        }
    }
}
