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
        String userId = null;
        boolean userAdded = false;

        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            PrintWriter writer = new PrintWriter(socket.getOutputStream(), true);

            String joinRequest = reader.readLine();
            Message joinResponse = MessageFormatter.parse(joinRequest);

            if (joinResponse == null || joinResponse.getType() != Message.Type.USER_JOIN) {
                return;
            }

            userId = joinResponse.getSender();
            String socketAddress = "%s:%d".formatted(socket.getInetAddress().getHostAddress(), socket.getPort());

            if (userManager.getUser(userId) != null) { // Only allow unique userIds
                Message rejectMessage = Message.rejectJoin(userId);
                writer.println(MessageFormatter.format(rejectMessage));
                return;
            }
            userAdded = true;
            handleUserJoin(socket, reader, writer, userId, socketAddress);
            handleClientCommunication(socket, reader, userId);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (userAdded) {
            processDisconnection(userId, socket);
            }
        }
    }

    private void handleUserJoin(Socket socket, BufferedReader reader, PrintWriter writer, String userId, String socketAddress) {
        userManager.addUser(new User(userId, socketAddress, writer));
        messageController.userJoined(userId);
    }

    private void handleClientCommunication(Socket socket, BufferedReader reader, String userId) {
        try {
            String messageStr;
            while ((messageStr = reader.readLine()) != null) {
                Message message = MessageFormatter.parse(messageStr);
                
                switch (message.getType()) {
                    case MESSAGE -> processMessage(userId, message);
                    case OPEN_PRIVATE_CHAT -> processPrivateChat(userId, message);
                    case USER_DETAILS_REQUEST -> processUserDetails(userId, message);
                    case STATUS_UPDATE -> processStatusUpdate(userId);
                    default -> {}
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void processDisconnection(String userId, Socket socket) {
        try {
            boolean isCoordinator = userManager.getCoordinatorId().equals(userId);
            userManager.removeUser(userId);
            messageController.userLeft(userId, isCoordinator);
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void processMessage(String senderId, Message message) {
        String recipient = message.getRecipient();
        String content = (String) message.getContent();
        messageController.sendMessage(senderId, recipient, content);
    }

    private void processUserDetails(String requesterId, Message message) {
        String targetId = (String) message.getContent();
        messageController.sendUserDetails(requesterId, targetId);
    }

    private void processStatusUpdate(String userId) {
        messageController.processStatusUpdate(userId);
    }

    private void processPrivateChat(String senderId, Message message) {
        String targetUserId = (String) message.getContent();
        messageController.openPrivateChat(senderId, targetUserId);
    }
}

