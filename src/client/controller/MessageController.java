package client.controller;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import client.model.ConnectionManager;
import client.model.MessageListener;
import client.view.ChatWindowView;

public class MessageController implements MessageListener {
    private final ConnectionManager connectionManager;
    private final ChatWindowView chatWindowView;
    private final String userId;
    
    public MessageController(ConnectionManager connectionManager, ChatWindowView chatWindowView, String userId) {
        this.connectionManager = connectionManager;
        this.chatWindowView = chatWindowView;
        this.userId = userId;

        chatWindowView.sendButtonAction(e -> sendMessage());
        chatWindowView.getUserListView().viewDetailsAction(e -> showUserDetails());
        connectionManager.setMessageListener(this);
    }
    
    @Override
    public void onMessageReceived(String sender, String content) {
        // Process user list updates
        if (sender.equals("[USERLIST]")) {
            String userListStr = content.substring(content.indexOf('['), content.indexOf(']') + 1);
            List<String> users = parseUserList(userListStr);
            chatWindowView.getUserListView().updateUserList(users, userId);
        } else if (sender.equals("[USER_DETAILS]")) {
            // Format: userId|role|connectionInfo
            String[] parts = content.split("\\|");
            if (parts.length == 3) {
                String detailsUserId = parts[0];
                String role = parts[1];
                String connectionInfo = parts[2];
                displayUserDetails(detailsUserId, role, connectionInfo);
            }
        } else {
            chatWindowView.getChatView().displayMessage("Group", sender, content);
        }
    }
    
    private List<String> parseUserList(String userListStr) {
        String cleanListStr = userListStr.substring(1, userListStr.length() - 1);
        if (cleanListStr.trim().isEmpty()) {
            return new ArrayList<>();
        }
        String[] userArray = cleanListStr.split(", ");
        return Arrays.asList(userArray);
    }
    
    private void sendMessage() {
        String messageText = chatWindowView.getMessage();
        connectionManager.sendMessage(messageText);
    }

    private void showUserDetails() {
        String selectedUser = chatWindowView.getUserListView().getSelectedUser();
        if (selectedUser != null) {
            // Remove "(You)" suffix if present
            if (selectedUser.endsWith(" (You)")) {
                selectedUser = selectedUser.substring(0, selectedUser.length() - 6);
            }
            connectionManager.sendUserDetailsRequest(selectedUser);
        }
    }
    
    private void displayUserDetails(String userId, String role, String connectionInfo) {
        chatWindowView.getUserListView().showMessage(
            userId + "'s details", 
            "User ID: " + userId + "\n" +
            "Role: " + role + "\n" +
            "Connected through: " + connectionInfo
        );
    }
}
