package client.controller;

import java.util.Arrays;

import client.model.ActivityTracker;
import client.model.ConnectionManager;
import client.model.MessageListener;
import client.view.ChatWindowView;
import common.model.Message;

public class MessageController implements MessageListener {
    private final ConnectionManager model;
    private final ChatWindowView view;
    private String coordinatorId = "";
    
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
                String[] users = (String[]) message.getContent();
                coordinatorId = users[0];
                view.getUserListView().updateUserList(Arrays.asList(users), model.getUserId(), coordinatorId);
            }
            case USER_DETAILS_RESPONSE -> {
                String[] details = (String[]) message.getContent();
                displayUserDetails(details[0], details[1], details[2], details[3]);
            }
            case MESSAGE -> {
                view.getChatView().displayMessage("Group", message.getSender(), (String)message.getContent());
            }
            case ANNOUNCEMENT, USER_NOTIFICATION -> {
                view.getChatView().displayMessage("Group", "[SERVER]", (String)message.getContent());
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
