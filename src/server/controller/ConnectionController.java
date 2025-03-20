package server.controller;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

import common.model.Message;
import common.util.MessageFormatter;
import server.model.User;
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
            BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            PrintWriter writer = new PrintWriter(socket.getOutputStream(), true);

            String initialMessage = reader.readLine();
            Message joinMessage = MessageFormatter.parse(initialMessage);

            if (joinMessage == null || joinMessage.getType() != Message.Type.USER_JOIN) {
                return;
            }

            String userId = joinMessage.getSender();
            String socketAddress = socket.getInetAddress().getHostAddress() + ":" + socket.getPort();

            if (userManager.getUser(userId) != null) { // Only allow unique userIds
                Message rejectMessage = Message.rejectUserJoin(userId);
                writer.println(MessageFormatter.format(rejectMessage));
                return;
            }

            userManager.addUser(new User(userId, socketAddress, writer));

            messageController.sendAnnouncement(userId + " has joined the chat.");
            messageController.sendServerUserList();

            String coordinatorId = userManager.getCoordinatorId();
            messageController.sendUserNotification(userId, coordinatorId + " is the current coordinator.");

            try {
                String messageStr;
                while ((messageStr = reader.readLine()) != null) {
                    Message message = MessageFormatter.parse(messageStr);
                    
                    switch (message.getType()) {
                        case MESSAGE -> {
                            messageController.sendMessage(userId, (String) message.getContent());
                        }
                        case USER_DETAILS_REQUEST -> {
                            String requestedUserId = (String) message.getContent();
                            messageController.sendUserDetails(userId, requestedUserId);
                        }
                        case STATUS_UPDATE -> {
                            userManager.toggleUserStatus(userId);
                            messageController.sendServerUserList(); // Update all clients
                        }
                        default -> {}
                    }
                }
            } finally {
                boolean isCoordinator = userManager.getCoordinatorId().equals(userId);
                userManager.removeUser(userId);

                if (isCoordinator) {
                    messageController.sendAnnouncement("The old coordinator, " + userId + ", has left the chat.");
                    messageController.sendAnnouncement(userManager.getCoordinatorId() + " is the new coordinator.");
                } else {
                    messageController.sendAnnouncement(userId + " has left the chat.");
                }

                messageController.sendServerUserList();
                socket.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
