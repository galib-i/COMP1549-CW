package client.controller;

import java.util.Arrays;

import client.model.ActivityTracker;
import client.model.ConnectionManager;
import client.model.MessageListener;
import client.view.ChatWindowView;
import common.model.Message;

public class MessageController implements MessageListener {
    private final ConnectionManager connectionManager;
    private final ChatWindowView chatWindowView;
    private final String userId;
    private String coordinatorId = "";
    private ActivityTracker activityTracker;
    
    public MessageController(ConnectionManager connectionManager, ChatWindowView chatWindowView, String userId) {
        this.connectionManager = connectionManager;
        this.chatWindowView = chatWindowView;
        this.userId = userId;

        this.activityTracker = new ActivityTracker(connectionManager, chatWindowView);
        
        chatWindowView.getUserListView().viewDetailsAction(e -> showUserDetails());
        chatWindowView.sendButtonAction(e -> sendMessage());
        connectionManager.setMessageListener(this);
    }
    
    @Override
    public void onMessageReceived(Message<?> message) {
        switch (message.getType()) {
            case USER_LIST -> {
                String[] users = (String[]) message.getContent();
                coordinatorId = users[0];
                chatWindowView.getUserListView().updateUserList(Arrays.asList(users), userId, coordinatorId);
            }
            case USER_DETAILS_RESPONSE -> {
                String[] details = (String[]) message.getContent();
                displayUserDetails(details[0], details[1], details[2], details[3]);
            }
            case MESSAGE -> {
                chatWindowView.getChatView().displayMessage("Group", message.getSender(), (String)message.getContent());
            }
            case ANNOUNCEMENT, USER_NOTIFICATION -> {
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
    
    private void displayUserDetails(String userId, String role, String status, String socketAddress) {
        chatWindowView.getUserListView().showMessage(
            userId + "'s details", 
            "User ID: " + userId + "\n" +
            "Role: " + role + "\n" +
            "Status: " + status + "\n" +
            "Connected through: " + socketAddress
        );
    }
}
