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

    public void sendMessage(String senderId, String content) {
        Message message = Message.sendMessage(senderId, content);
        String formattedMessage = MessageFormatter.format(message);
        
        broadcastMessage(formattedMessage);
    }

    public void sendAnnouncement(String content) {
        Message sendAnnouncement = Message.sendAnnouncement(content);
        String formattedMessage = MessageFormatter.format(sendAnnouncement);
        
        broadcastMessage(formattedMessage);
    }

    public void sendUserNotification(String targetId, String content) {
        User targetUser = userManager.getUser(targetId);
        Message notification = Message.notifyUser(content);
        String formattedMessage = MessageFormatter.format(notification);

        targetUser.getWriter().println(formattedMessage);
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
        sendAnnouncement("%s has joined the chat.".formatted(userId));
        sendServerUserList();
        String coordinatorId = userManager.getCoordinatorId();
        sendUserNotification(userId, "%s is the coordinator.".formatted(coordinatorId));
    }

    public void userLeft(String userId, boolean wasCoordinator) {
        Message quitMessage = Message.userQuit(userId);
        broadcastMessage(MessageFormatter.format(quitMessage));

        if (wasCoordinator) {
            sendAnnouncement("The old coordinator, %s, has left the chat.".formatted(userId));
            sendAnnouncement("%s is the coordinator.".formatted(userManager.getCoordinatorId()));
        } else {
            sendAnnouncement("%s has left the chat.".formatted(userId));
        }
        sendServerUserList();
    }

    public void sendPrivateMessage(String senderId, String targetUserId) {
        User targetUser = userManager.getUser(targetUserId);
        Message privateChatRequest = Message.sendPrivateMessage(senderId, targetUserId);
        String formattedMessage = MessageFormatter.format(privateChatRequest);
        targetUser.getWriter().println(formattedMessage);
    }

    public void processStatusUpdate(String userId) {
        userManager.toggleUserStatus(userId);
        sendServerUserList();
    }
}
