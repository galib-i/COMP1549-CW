package common.model;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

public class Message {
    public enum Type {
        USER_JOIN,
        REJECT_USER_JOIN,
        MESSAGE,
        PRIVATE_MESSAGE,
        ANNOUNCEMENT,
        USER_NOTIFICATION,
        USER_LIST,
        USER_DETAILS_REQUEST,
        USER_DETAILS_RESPONSE,
        STATUS_UPDATE,
        USER_QUIT,  // Add this new message type
    }

    private static final String SERVER = "SERVER";
    private static final String CLIENT = "CLIENT";
    private final Type type;
    private final String timestamp;
    private final String sender;
    private final Object content;


    public Message(Type type, String sender, Object content) {
        this.type = type;
        this.sender = sender;
        this.content = content;
        this.timestamp = new SimpleDateFormat("HH:mm:ss").format(new Date());
    }

    public Message(Type type, String sender) {
        this(type, sender, null);
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

    public String getTimestamp() {
        return timestamp;
    }

    public static Message requestJoin(String userId) {
        return new Message(Type.USER_JOIN, userId);
    }

    public static Message rejectJoin(String userId) {
        return new Message(Type.REJECT_USER_JOIN, SERVER, userId);
    }

    public static Message sendMessage(String userId, String content) {
        return new Message(Type.MESSAGE, userId, content);
    }
    
    public static Message sendAnnouncement(String content) {
        return new Message(Type.ANNOUNCEMENT, SERVER, content);
    }
    
    public static Message sendPrivateMessage(String senderUserId, String targetUserId) {
        return new Message(Type.PRIVATE_MESSAGE, senderUserId, targetUserId);
    }

    public static Message notifyUser(String content) {
        return new Message(Type.USER_NOTIFICATION, SERVER, content);
    }
    
    public static Message requestUserDetails(String targetUserId) {
        return new Message(Type.USER_DETAILS_REQUEST, CLIENT, targetUserId);
    }
    
    public static Message sendUserList(Map<String, Map<String, String>> userList) {
        return new Message(Type.USER_LIST, SERVER, userList);
    }

    public static Message userDetailsResponse(String userId, Map<String, String> details) {
        return new Message(Type.USER_DETAILS_RESPONSE, userId, details);
    }
    
    public static Message updateStatus(String userId) {
        return new Message(Type.STATUS_UPDATE, userId);
    }

    public static Message userQuit(String userId) {
        return new Message(Type.USER_QUIT, SERVER, userId);
    }
}
