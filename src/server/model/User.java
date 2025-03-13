package server.model;

import java.io.PrintWriter;

public class User {
    private String userId;
    private String role;
    private String status;
    private String connectionInfo;
    private PrintWriter writer;
    
    public User(String userId, String connectionInfo, PrintWriter writer) {
        this.userId = userId;
        this.role = "MEMBER";
        this.status = "ACTIVE";
        this.connectionInfo = connectionInfo;
        this.writer = writer;

    }
    
    public String getUserId() {
        return userId;
    }
    
    public String getRole() {
        return role;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
    
    public String getConnectionInfo() {
        return connectionInfo;
    }
    
    public PrintWriter getWriter() {
        return writer;
    }
    
    public void setCoordinator() {
        this.role = "COORDINATOR";
    }
}
