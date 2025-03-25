package client.controller;

import java.util.Map;

import client.model.ActivityTracker;
import client.model.ConnectionManager;
import client.model.MessageListener;
import client.view.ChatWindowView;
import common.model.Message;

public class MessageController implements MessageListener {
    private final ConnectionManager model;
    private final ChatWindowView view;
    
    public MessageController(ConnectionManager model, ChatWindowView view) {
        this.model = model;
        this.view = view;

        new ActivityTracker(model, view);
        
        view.getUserListView().viewDetailsAction(e -> showUserDetails());
        view.getUserListView().privateMessageAction(e -> openPrivateChat());
        view.sendButtonAction(e -> sendMessage());
        model.setMessageListener(this);
    }

    @Override
    public void handleCommunication(Message message) {
        switch (message.getType()) {
            case USER_LIST -> processUserListMessage(message);
            case USER_DETAILS_RESPONSE -> processUserDetails(message);
            case PRIVATE_MESSAGE -> processPrivateMessage(message);
            case USER_QUIT -> processUserQuit(message);
            case MESSAGE, ANNOUNCEMENT, USER_NOTIFICATION -> processMessage(message);
            default -> {}
        }
    }

    private void processUserListMessage(Message message) {
        Map<String, Map<String, String>> userList = (Map<String, Map<String, String>>)message.getContent();
        view.getUserListView().updateUserList(userList, model.getUserId());
    }

    private void processUserDetails(Message message) {
        Map<String, String> details = (Map<String, String>) message.getContent();
        String userId = details.get("userId");

        String formattedDetails = "User ID: %s\nRole: %s\nStatus: %s\nConnected through: %s"
            .formatted(userId, details.get("role"), details.get("status"), details.get("socketAddress")
        );
        
        view.getUserListView().showMessage("%s's details".formatted(userId), formattedDetails);
    }

    private void displayMessage(Message message, String sender) {
        view.getChatView().displayMessage("Group", message.getTimestamp(), sender, (String)message.getContent());
    }

    private void processMessage(Message message) {
        String sender = message.getSender();
        if (message.getType() != Message.Type.MESSAGE) {
            sender = "[SERVER]";
        }
        displayMessage(message, sender);
    }

    private void processPrivateMessage(Message message) {
        String requestingUser = message.getSender();
        view.getChatView().openPrivateChat(requestingUser);
    }

    private void processUserQuit(Message message) {
        String userId = (String) message.getContent();
        view.getChatView().closePrivateChat(userId);
    }

    private void sendMessage() {
        String messageText = view.getMessage();
        model.sendMessage(messageText);
    }

    private void showUserDetails() {
        String selectedUser = view.getUserListView().getSelectedUser();  
        model.sendUserDetailsRequest(selectedUser);
    }

    private void openPrivateChat() {
        String selectedUser = view.getUserListView().getSelectedUser();
        view.getChatView().openPrivateChat(selectedUser);

        model.sendPrivateMessage(selectedUser);
    }
}
