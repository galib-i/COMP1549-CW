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
    
    public void sendMessage(String senderId, String content) {
        Message<String> message = Message.sendMessage(senderId, content);
        String formattedMessage = MessageFormatter.format(message);
        
        for (User user : userManager.getUsers()) {
            user.getWriter().println(formattedMessage);
        }
    }

    public void sendServerMessage(String content) {
        Message<String> announcement = Message.announcement(content);
        String formattedMessage = MessageFormatter.format(announcement);
        
        for (User user : userManager.getUsers()) {
            user.getWriter().println(formattedMessage);
        }
    }
    
    public void sendUserDetails(String requesterId, String userId) {
        User requester = userManager.getUserById(requesterId);
        User requestedUser = userManager.getUserById(userId);
        
        String[] details = {requestedUser.getUserId(), requestedUser.getRole(), requestedUser.getConnectionInfo()};
        
        Message<String[]> detailsResponse = Message.userDetailsResponse(userId, details);
        String formattedMessage = MessageFormatter.format(detailsResponse);
        requester.getWriter().println(formattedMessage);
    }

    public void sendServerUserList() {
        String[] userArray = userManager.getUserIds();
        Message<String[]> userListMessage = Message.userList(userArray);
        String formattedMessage = MessageFormatter.format(userListMessage);
        
        for (User user : userManager.getUsers()) {
            user.getWriter().println(formattedMessage);
        }
    }
}
