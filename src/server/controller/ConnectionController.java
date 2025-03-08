package server.controller;

import server.model.UserManager;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

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
            userManager.addUser(userId);

            // Handle disconnect
            try {
                while (socket.isConnected()) {
                    if (in.readLine() == null) break;
                }
            } finally {
                userManager.removeUser(userId);
                socket.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
