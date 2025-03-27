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

/**
 * Controls the server side connection between the server and a client
 */
public class ConnectionController {
    private final UserManager userManager;
    private final MessageController messageController;

    public ConnectionController(UserManager userManager) {
        this.userManager = userManager;
        this.messageController = new MessageController(userManager);
    }
 
    public void handleNewConnection(Socket socket) {
        new Thread(() -> controlConnection(socket)).start();
    }

    private void controlConnection(Socket socket) {
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

            if (userManager.getUser(userId) != null) {
                Message rejectMessage = Message.rejectJoin(userId);
                writer.println(MessageFormatter.format(rejectMessage));
                return;
            }
            userAdded = true;
            String socketAddress = "%s:%d".formatted(socket.getInetAddress().getHostAddress(), socket.getPort());
            controlUserJoin(socket, reader, writer, userId, socketAddress);
            controlClientCommunication(reader, userId);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (userAdded) {
            controlDisconnection(userId, socket);
            }
        }
    }

    private void controlUserJoin(Socket socket, BufferedReader reader, PrintWriter writer, String userId, String socketAddress) {
        userManager.addUser(new User(userId, socketAddress, writer));
        messageController.controlUserJoin(userId);
    }

    private void controlClientCommunication(BufferedReader reader, String userId) {
        try {
            String messageStr;
            while ((messageStr = reader.readLine()) != null) {
                Message message = MessageFormatter.parse(messageStr);
                
                switch (message.getType()) {
                    case MESSAGE -> controlMessage(userId, message);
                    case OPEN_PRIVATE_CHAT -> controlPrivateChat(userId, message);
                    case USER_DETAILS_REQUEST -> controlUserDetails(userId, message);
                    case STATUS_UPDATE -> controlStatusUpdate(userId);
                    default -> {}
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void controlDisconnection(String userId, Socket socket) {
        try {
            boolean isCoordinator = userManager.getCoordinatorId().equals(userId);
            userManager.removeUser(userId);
            messageController.controlUserLeave(userId, isCoordinator);
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void controlMessage(String senderId, Message message) {
        String recipient = message.getRecipient();
        String content = (String) message.getContent();
        messageController.sendMessage(senderId, recipient, content);
    }

    private void controlUserDetails(String requesterId, Message message) {
        String targetId = (String) message.getContent();
        messageController.sendUserDetails(requesterId, targetId);
    }

    private void controlStatusUpdate(String userId) {
        messageController.controlStatusUpdate(userId);
    }

    private void controlPrivateChat(String senderId, Message message) {
        String targetUserId = (String) message.getContent();
        messageController.openPrivateChat(senderId, targetUserId);
    }
}

