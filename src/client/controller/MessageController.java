package client.controller;

import client.model.ConnectionManager;
import client.model.MessageListener;
import client.view.MainView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MessageController implements MessageListener {
    private final ConnectionManager connectionManager;
    private final MainView mainView;
    private final String userId;
    
    public MessageController(ConnectionManager connectionManager, MainView mainView, String userId) {
        this.connectionManager = connectionManager;
        this.mainView = mainView;
        this.userId = userId;

        mainView.sendButtonAction(e -> sendMessage());
        connectionManager.setMessageListener(this);
    }
    
    @Override
    public void onMessageReceived(String sender, String content) {
        // Process user list updates
        if (sender.equals("[USERLIST]")) {
            // Extract user list from message
            // Format is: "Current users: [user1, user2, ...]"
            String userListStr = content.substring(content.indexOf('['), content.indexOf(']') + 1);
            List<String> users = parseUserList(userListStr);
            mainView.getUserListView().updateUserList(users, userId);
        } else {
            mainView.getChatView().displayMessage("Group", sender, content);
        }
    }
    
    private List<String> parseUserList(String userListStr) {
        // Remove brackets and split by comma
        String cleanListStr = userListStr.substring(1, userListStr.length() - 1);
        if (cleanListStr.trim().isEmpty()) {
            return new ArrayList<>();
        }
        String[] userArray = cleanListStr.split(", ");
        return Arrays.asList(userArray);
    }
    
    private void sendMessage() {
        String messageText = mainView.getMessage();
        if (messageText != null) {
            connectionManager.sendMessage(messageText);
        }
    }
}
