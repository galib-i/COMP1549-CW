package server.controller;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

import common.model.Message;
import common.util.MessageFormatter;
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

            String initialMessage = in.readLine();
            Message<?> joinMessage = MessageFormatter.parse(initialMessage);

            if (joinMessage == null || joinMessage.getType() != Message.Type.USER_JOIN) {
                return;
            }

            String userId = joinMessage.getSender();
            String connectionInfo = socket.getInetAddress().getHostAddress() + ":" + socket.getPort();

            if (!userManager.addUser(userId, connectionInfo, out)) {
                Message<String> rejectMessage = Message.rejectUserJoin(userId);
                out.println(MessageFormatter.format(rejectMessage));
                return;
            }

            messageController.sendServerMessage(userId + " has joined the chat.");
            messageController.sendServerUserList();

            String coordinatorId = userManager.getCoordinator();
            messageController.sendUserNotification(userId, coordinatorId + " is the current coordinator.");

            try {
                String messageStr;
                while ((messageStr = in.readLine()) != null) {
                    Message<?> message = MessageFormatter.parse(messageStr);
                    
                    if (message == null) {
                        continue;
                    }
                    
                    switch (message.getType()) {
                        case MESSAGE -> {
                            messageController.sendMessage(userId, (String) message.getContent());
                        }
                        case USER_DETAILS_REQUEST -> {
                            String requestedUserId = (String) message.getContent();
                            messageController.sendUserDetails(userId, requestedUserId);
                        }
                        default -> {}
                    }
                }
            } finally {
                messageController.sendServerMessage(userId + " has left the chat.");
                boolean isCoordinator = userManager.removeUser(userId);
                if (isCoordinator) {
                    String newCoordinatorId = userManager.getCoordinator();
                    messageController.sendServerMessage("The previous coordinator, " + userId + ", has left - the new coordinator is " + newCoordinatorId + ".");
                }

                messageController.sendServerUserList();
                socket.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
