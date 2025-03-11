package server.controller;

import server.model.User;
import server.model.UserManager;

public class MessageController {
    private final UserManager userManager;

    public MessageController(UserManager userManager) {
        this.userManager = userManager;
    }
    
    public void sendGroupMessage(String senderId, String message) {
        for (User user : userManager.getUsers()) {
            user.getWriter().println(senderId + ": " + message);
        }
    }

    public void sendSystemMessage(String message) {
        for (User user : userManager.getUsers()) {
            user.getWriter().println("[SERVER]: " + message);
        }
    }
    
    public void sendUserDetails(String requesterId, String userId) {
        User requester = userManager.getUserById(requesterId);
        User requestedUser = userManager.getUserById(userId);
        
        if (requestedUser != null) {
            requester.getWriter().println("[USER_DETAILS]: " + userId + 
                                         "|" + requestedUser.getRole() + 
                                         "|" + requestedUser.getConnectionInfo());
        }
    }
}
