package common.model;

public class Message<T> {
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
    private final T content;

    public Message(Type type, String sender, T content) {
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

    public T getContent() {
        return content;
    }

    public static Message<String> userJoin(String userId) {
        return new Message<>(Type.USER_JOIN, userId, null);
    }

    public static Message<String> rejectUserJoin(String userId) {
        return new Message<>(Type.REJECT_USER_JOIN, "SERVER", userId);
    }

    public static Message<String> sendMessage(String userId, String content) {
        return new Message<>(Type.MESSAGE, userId, content);
    }
    
    public static Message<String> announcement(String content) {
        return new Message<>(Type.ANNOUNCEMENT, "SERVER", content);
    }

    public static Message<String> userNotification(String content) {
        return new Message<>(Type.USER_NOTIFICATION, "SERVER", content);
    }
    
    public static Message<String> requestUserDetails(String userId) {
        return new Message<>(Type.USER_DETAILS_REQUEST, "CLIENT", userId);
    }
    
    public static Message<String[]> userList(String[] users) {
        return new Message<>(Type.USER_LIST, "SERVER", users);
    }
    
    public static Message<String[]> userDetailsResponse(String userId, String[] details) {
        return new Message<>(Type.USER_DETAILS_RESPONSE, userId, details);
    }
    
    public static Message<String> statusUpdate(String userId, String status) {
        return new Message<>(Type.STATUS_UPDATE, userId, status);
    }
}
