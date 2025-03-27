package common.model;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

public class Message {
    public enum Type {
        USER_JOIN,
        REJECT_USER_JOIN,
        MESSAGE,
        OPEN_PRIVATE_CHAT,
        USER_LIST,
        USER_DETAILS_REQUEST,
        USER_DETAILS_RESPONSE,
        STATUS_UPDATE,
        USER_QUIT
    }

    private static final String SERVER = "SERVER";
    private static final String GROUP = "Group";
    private final Type type;
    private final String timestamp;
    private final String sender;
    private final String recipient;
    private final Object content;


    public Message(Type type, String sender, String recipient, Object content) {
        this.type = type;
        this.sender = sender;
        this.recipient = recipient;
        this.content = content;
        this.timestamp = new SimpleDateFormat("HH:mm:ss").format(new Date());
    }

    public Type getType() {
        return type;
    }

    public String getSender() {
        return sender;
    }

    public String getRecipient() {
        return recipient;
    }

    public Object getContent() {
        return content;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public static Message requestJoin(String requesterId) {
        return new Message(Type.USER_JOIN, requesterId, SERVER, null);
    }

    public static Message rejectJoin(String recipientId) {
        return new Message(Type.REJECT_USER_JOIN, SERVER, recipientId, null);
    }

    public static Message sendMessage(String sender, String recipient, String content) {
        return new Message(Type.MESSAGE, sender, recipient, content);
    }

    public static Message openPrivateChat(String senderId, String targetId) {
        return new Message(Type.OPEN_PRIVATE_CHAT, senderId, SERVER, targetId);
    }

    public static Message requestUserDetails(String senderId, String targetId) {
        return new Message(Type.USER_DETAILS_REQUEST, senderId, SERVER, targetId);
    }
    
    public static Message sendUserList(Map<String, Map<String, String>> userList) {
        return new Message(Type.USER_LIST, SERVER, GROUP, userList);
    }

    public static Message respondUserDetails(String recipientId, Map<String, String> details) {
        return new Message(Type.USER_DETAILS_RESPONSE, SERVER, recipientId, details);
    }
    
    public static Message updateStatus(String userId) {
        return new Message(Type.STATUS_UPDATE, SERVER, GROUP, userId);
    }

    public static Message closePrivateChat(String senderId) {
        return new Message(Type.USER_QUIT, senderId, SERVER, null);
    }
}
