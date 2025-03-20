package common.model;

public class Message {
    public enum Type {
        USER_JOIN,
        REJECT_USER_JOIN,
        MESSAGE,
        ANNOUNCEMENT,
        USER_NOTIFICATION,
        USER_LIST,
        USER_DETAILS_REQUEST,
        USER_DETAILS_RESPONSE,
        STATUS_UPDATE,
    }

    private final Type type;
    private final String sender;
    private final Object content;

    public Message(Type type, String sender, Object content) {
        this.type = type;
        this.sender = sender;
        this.content = content;
    }
    
    public Type getType() {
        return type;
    }

    public String getSender() {
        return sender;
    }

    public Object getContent() {
        return content;
    }

    public static Message userJoin(String userId) {
        return new Message(Type.USER_JOIN, userId, null);
    }

    public static Message rejectUserJoin(String userId) {
        return new Message(Type.REJECT_USER_JOIN, "SERVER", userId);
    }

    public static Message sendMessage(String userId, String content) {
        return new Message(Type.MESSAGE, userId, content);
    }
    
    public static Message announcement(String content) {
        return new Message(Type.ANNOUNCEMENT, "SERVER", content);
    }

    public static Message userNotification(String content) {
        return new Message(Type.USER_NOTIFICATION, "SERVER", content);
    }
    
    public static Message requestUserDetails(String userId) {
        return new Message(Type.USER_DETAILS_REQUEST, "CLIENT", userId);
    }
    
    public static Message userList(String[] users) {
        return new Message(Type.USER_LIST, "SERVER", users);
    }
    
    public static Message userDetailsResponse(String userId, String[] details) {
        return new Message(Type.USER_DETAILS_RESPONSE, userId, details);
    }
    
    public static Message statusUpdate(String userId) {
        return new Message(Type.STATUS_UPDATE, userId, null);
    }
}
