package server.controller;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

import server.model.UserManager;

public class ConnectionController {
    private final UserManager userManager;
    private final MessageController messageController;

    public ConnectionController(UserManager userManager) {
        this.userManager = userManager;
        this.messageController = new MessageController(userManager);
    }

    public void handleNewConnection(Socket socket) {
        new Thread(() -> processConnection(socket)).start();
    }

    private void processConnection(Socket socket) {
        try {
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);

            String userId = in.readLine(); // Read the userId sent by client

            userManager.addUser(userId, out);
            messageController.sendSystemMessage(userId + " has joined the chat.");

            try {
                String message;
                while ((message = in.readLine()) != null) {
                    messageController.sendGroupMessage(userId, message);
                }
            } finally {
                messageController.sendSystemMessage(userId + " has left the chat.");
                userManager.removeUser(userId);

                socket.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
