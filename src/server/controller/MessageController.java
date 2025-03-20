package server.controller;

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
        Message<String> message = Message.sendMessage(senderId, content);
        String formattedMessage = MessageFormatter.format(message);
        
        broadcastMessage(formattedMessage);
    }

    public void sendAnnouncement(String content) {
        Message<String> announcement = Message.announcement(content);
        String formattedMessage = MessageFormatter.format(announcement);
        
        broadcastMessage(formattedMessage);
    }

    public void sendUserNotification(String targetId, String content) {
        User targetUser = userManager.getUser(targetId);
        Message<String> notification = Message.userNotification(content);
        String formattedMessage = MessageFormatter.format(notification);

        targetUser.getWriter().println(formattedMessage);
    }
    
    public void sendUserDetails(String requesterId, String targetId) {
        User requester = userManager.getUser(requesterId);
        User targetUser = userManager.getUser(targetId);
        
        if (requester == null || targetUser == null) {
            return;
        }

        String[] details = {targetUser.getUserId(), targetUser.getRole().toString(), targetUser.getStatus().toString(), targetUser.getSocketAddress()};
        Message<String[]> detailsResponse = Message.userDetailsResponse(targetId, details);
        String formattedMessage = MessageFormatter.format(detailsResponse);

        requester.getWriter().println(formattedMessage);
    }

    public void sendServerUserList() {
        String[] userArray = userManager.getUserIds();
        Message<String[]> userListMessage = Message.userList(userArray);
        String formattedMessage = MessageFormatter.format(userListMessage);
        
        broadcastMessage(formattedMessage);
    }
}
