package server.model;

import java.io.PrintWriter;

public class User {
    private String userId;
    private String role;
    private String status;
    private String socketAddress;
    private PrintWriter writer;
    
    public User(String userId, String socketAddress, PrintWriter writer) {
        this.userId = userId;
        this.role = "MEMBER";
        this.status = "ACTIVE";
        this.socketAddress = socketAddress;
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
    
    public String getSocketAddress() {
        return socketAddress;
    }
    
    public PrintWriter getWriter() {
        return writer;
    }
    
    public void setCoordinator() {
        this.role = "COORDINATOR";
    }
}
