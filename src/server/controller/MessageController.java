package server.controller;

import java.util.Map;

import common.model.Message;
import common.util.MessageFormatter;
import server.model.User;
import server.model.UserManager;

public class MessageController {
    private final UserManager userManager;

    public MessageController(UserManager userManager) {
        this.userManager = userManager;
    }
    
    public void broadcastMessage(String content) {
        for (User user : userManager.getUsers()) {
            user.getWriter().println(content);
        }
    } 

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

    public void notifyUser(String sender, String recipient, String content) {
        User user = userManager.getUser(recipient);
        Message message = Message.sendMessage(sender, recipient, content);
        String formattedMessage = MessageFormatter.format(message);
            
        user.getWriter().println(formattedMessage);

    }

    public void sendUserDetails(String requesterId, String targetId) {
        User requester = userManager.getUser(requesterId);

        Map<String, String> details = userManager.getUserDetails(targetId, false);
        Message detailsResponse = Message.userDetailsResponse(targetId, details);
        String formattedMessage = MessageFormatter.format(detailsResponse);

        requester.getWriter().println(formattedMessage);
    }

    public void sendServerUserList() {
        Map<String, Map<String, String>> userList = userManager.getAllUserDetails();
        Message userListMessage = Message.sendUserList(userList);
        String formattedMessage = MessageFormatter.format(userListMessage);
        
        broadcastMessage(formattedMessage);
    }

    public void userJoined(String userId) {
        sendMessage("SERVER", "Group", "%s has joined the chat.".formatted(userId));
        sendServerUserList();
        String coordinatorId = userManager.getCoordinatorId();
        notifyUser("SERVER", userId, "%s is the coordinator.".formatted(coordinatorId));
    }

    public void userLeft(String userId, boolean wasCoordinator) {
        sendMessage("SERVER", "Group", "%s has left the chat.".formatted(userId));

        if (wasCoordinator) {
            sendMessage("SERVER", "Group", "The old coordinator, %s, has left the chat.".formatted(userId));
            sendMessage("SERVER", "Group", "%s is the coordinator.".formatted(userManager.getCoordinatorId()));
        }
        sendServerUserList();

        Message quitMessage = Message.userQuit(userId);
        broadcastMessage(MessageFormatter.format(quitMessage));
    }

    public void openPrivateChat(String senderId, String targetUserId) {
        User targetUser = userManager.getUser(targetUserId);
        Message privateChatRequest = Message.openPrivateChat(senderId, targetUserId);
        String formattedMessage = MessageFormatter.format(privateChatRequest);
        targetUser.getWriter().println(formattedMessage);
    }

    public void processStatusUpdate(String userId) {
        userManager.toggleUserStatus(userId);
        sendServerUserList();
    }
}
