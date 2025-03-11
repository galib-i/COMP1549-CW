package server.model;

import java.io.PrintWriter;

public class User {
    private String userId;
    private String role;
    private String connectionInfo;
    private PrintWriter writer;
    
    public User(String userId, String connectionInfo, PrintWriter writer) {
        this.userId = userId;
        this.role = "MEMBER";
        this.connectionInfo = connectionInfo;
        this.writer = writer;
    }
    
    public String getUserId() {
        return userId;
    }
    
    public String getRole() {
        return role;
    }
    
    public String getConnectionInfo() {
        return connectionInfo;
    }
    
    public PrintWriter getWriter() {
        return writer;
    }
    
    public void setRole(String role) {
        this.role = role;
    }
}
