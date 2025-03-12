package client.controller;

import java.util.Arrays;

import client.model.ConnectionManager;
import client.model.MessageListener;
import client.view.ChatWindowView;
import common.model.Message;

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
    public void onMessageReceived(Message<?> message) {
        switch (message.getType()) {
            case USER_LIST -> {
                String[] users = (String[]) message.getContent();
                chatWindowView.getUserListView().updateUserList(Arrays.asList(users), userId);
            }
            case USER_DETAILS_RESPONSE -> {
                String[] details = (String[]) message.getContent();
                displayUserDetails(details[0], details[1], details[2]);
            }
            case MESSAGE -> {
                chatWindowView.getChatView().displayMessage("Group", message.getSender(), (String)message.getContent());
            }
            case ANNOUNCEMENT -> {
                chatWindowView.getChatView().displayMessage("Group", "[SERVER]", (String)message.getContent());
            }
            default -> {}
        }
    }
    
    private void sendMessage() {
        String messageText = chatWindowView.getMessage();
        connectionManager.sendMessage(messageText);
    }

    private void showUserDetails() {
        String selectedUser = chatWindowView.getUserListView().getSelectedUser();  
        connectionManager.sendUserDetailsRequest(selectedUser);
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
