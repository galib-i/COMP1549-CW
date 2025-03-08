package common.model;

import java.io.Serializable;
import java.util.List;

public class Message implements Serializable {
    private static final long serialVersionUID = 1L;
    
    public enum Type {
        CHAT,
        JOIN,
        LEAVE,
        USER_LIST  // New message type for user list updates
    }
    
    private final Type type;
    private final String sender;
    private final String content;
    private List<String> userList;  // For USER_LIST type messages
    
    // Constructor for regular messages
    public Message(Type type, String sender, String content) {
        this.type = type;
        this.sender = sender;
        this.content = content;
    }
    
    // Constructor for user list messages
    public Message(Type type, List<String> userList) {
        this.type = type;
        this.sender = null;
        this.content = null;
        this.userList = userList;
    }
    
    // Getters
    public Type getType() {
        return type;
    }
    
    public String getSender() {
        return sender;
    }
    
    public String getContent() {
        return content;
    }
    
    public List<String> getUserList() {
        return userList;
    }
}
