package server.controller;

import java.util.Map;

import common.model.Message;
import common.util.MessageFormatter;
import server.model.User;
import server.model.UserManager;

/**
 * Controls the messages sent between the server and clients
 */
public class MessageController {
    private final UserManager userManager;

    public MessageController(UserManager userManager) {
        this.userManager = userManager;
    }
    
    /**
     * When a user joins the server, announce join, update user list and notify user of coordinator
     * @param userId Id of the user that joined
     */
    public void controlUserJoin(String userId) {
        sendMessage("[SERVER]", "Group", "%s has joined the chat.".formatted(userId));
        broadcastUserList();
        String coordinatorId = userManager.getCoordinatorId();
        sendMessageToUser(userId, Message.sendMessage("[SERVER]", userId, "%s is the coordinator.".formatted(coordinatorId)));
    }

    /**
     * When a user leaves the server, announce leave, update user list and if was coordinator, announce new one
     * @param userId Id of the user that left
     * @param isCoordinator True: user was the coordinator
     */
    public void controlUserLeave(String userId, boolean isCoordinator) {
        sendMessage("[SERVER]", "Group", "%s has left the chat.".formatted(userId));

        if (isCoordinator) {
            sendMessage("[SERVER]", "Group", 
                "The old coordinator, %s, has left the chat. %s is the new coordinator.".formatted(userId, userManager.getCoordinatorId()));
        }
        broadcastUserList();

        sendMessageToGroup(Message.closePrivateChat(userId));
    }

    public void controlStatusUpdate(String userId) {
        userManager.toggleUserStatus(userId);
        broadcastUserList();
    }

    public void openPrivateChat(String senderId, String targetUserId) {
        sendMessageToUser(targetUserId, Message.openPrivateChat(senderId, targetUserId));
    }

    /**
     * Sends a message to everyone or displays for both sender and recipient in a private chat
     * @param sender Id of the user or server sending the message
     * @param recipient Id of the user or "Group" receiving the message
     * @param content Message content
     */
    public void sendMessage(String sender, String recipient, String content) {
        Message message = Message.sendMessage(sender, recipient, content);
        String formattedMessage = MessageFormatter.format(message);

        if (recipient.equals("Group")) {
            broadcastMessage(formattedMessage);
        } else {
            User user = userManager.getUser(recipient);
            user.getWriter().println(formattedMessage);

            User senderUser = userManager.getUser(sender);
            senderUser.getWriter().println(formattedMessage);
        }
    }

    public void sendUserDetails(String requesterId, String targetId) {
        Map<String, String> details = userManager.getUserDetails(targetId, true);
        sendMessageToUser(requesterId, Message.respondUserDetails(targetId, details));
    }

    public void broadcastUserList() {
        sendMessageToGroup(Message.sendUserList(userManager.getAllUserDetails()));
    }

    public void notifyUser(String sender, String recipient, String content) {
        sendMessageToUser(recipient, Message.sendMessage(sender, recipient, content));
    }

    private void sendMessageToUser(String userId, Message message) {
        User user = userManager.getUser(userId);
        String formattedMessage = MessageFormatter.format(message);
        user.getWriter().println(formattedMessage);
    }

    private void sendMessageToGroup(Message message) {
        broadcastMessage(MessageFormatter.format(message));
    }

    public void broadcastMessage(String content) {
        for (User user : userManager.getUsers()) {
            user.getWriter().println(content);
        }
    } 
}
