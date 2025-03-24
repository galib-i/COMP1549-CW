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
        view.sendButtonAction(e -> sendMessage());
        model.setMessageListener(this);
    }
    
    @Override
    public void onMessageReceived(Message message) {
        switch (message.getType()) {
            case USER_LIST -> {
                @SuppressWarnings("unchecked")
                Map<String, Map<String, String>> userList = (Map<String, Map<String, String>>) message.getContent();
                view.getUserListView().updateUserList(userList, model.getUserId());
            }
            case USER_DETAILS_RESPONSE -> {
                @SuppressWarnings("unchecked")
                Map<String, String> details = (Map<String, String>) message.getContent();
                displayUserDetails(
                    details.get("userId"),
                    details.get("role"),
                    details.get("status"),
                    details.get("socketAddress")
                );
            }
            case MESSAGE -> {
                view.getChatView().displayMessage("Group", message.getTimestamp(), message.getSender(), (String)message.getContent());
            }
            case ANNOUNCEMENT, USER_NOTIFICATION -> {
                view.getChatView().displayMessage("Group", message.getTimestamp(), "[SERVER]", (String)message.getContent());
            }
            default -> {}
        }
    }
    
    private void sendMessage() {
        String messageText = view.getMessage();
        model.sendMessage(messageText);
    }

    private void showUserDetails() {
        String selectedUser = view.getUserListView().getSelectedUser();  
        model.sendUserDetailsRequest(selectedUser);
    }
    
    private void displayUserDetails(String userId, String role, String status, String socketAddress) {
        view.getUserListView().showMessage(
            userId + "'s details", 
            "User ID: " + userId + "\n" +
            "Role: " + role + "\n" +
            "Status: " + status + "\n" +
            "Connected through: " + socketAddress
        );
    }
}
