package server.controller;

import java.io.PrintWriter;

import server.model.UserManager;
public class MessageController {
    private final UserManager userManager;

    public MessageController(UserManager userManager) {
        this.userManager = userManager;
    }
    
    public void sendGroupMessage(String sender, String message) {
        for (PrintWriter writer : userManager.getUserWriters()) {
            writer.println(sender + ": " + message);
        }
    }

    public void sendSystemMessage(String message) {
        for (PrintWriter writer : userManager.getUserWriters()) {
            writer.println("[SERVER]: " + message);
        }
    }
}
