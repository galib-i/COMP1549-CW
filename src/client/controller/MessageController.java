package client.controller;

import client.model.ConnectionManager;
import client.view.MainView;


public class MessageController {
    private final ConnectionManager connectionManager;
    private final MainView mainView;
    private final String userId;
    
    public MessageController(ConnectionManager connectionManager, MainView mainView, String userId) {
        this.connectionManager = connectionManager;
        this.mainView = mainView;
        this.userId = userId;

        mainView.sendButtonAction(e -> sendMessage());
    }
    
    private void sendMessage() {
        String messageText = mainView.getMessage();
        if (messageText != null) {
            connectionManager.sendMessage(messageText);
            displayMessage("Group", userId, messageText);
        }
    }
    
    public void displayMessage(String chatName, String sender, String content) {
        mainView.getChatView().displayMessage(chatName, sender, content);
    }
}
