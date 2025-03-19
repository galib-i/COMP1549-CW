package client.controller;

import client.model.ConnectionManager;
import client.view.ChatWindowView;
import client.view.LoginView;

public class ClientController {
    private final ConnectionManager connectionManager;
    private LoginView loginView;
    private ConnectionController connectionController;
    private ChatWindowView chatWindowView;
    private MessageController messageController;
    
    public ClientController() {
        this.connectionManager = new ConnectionManager();
    }
    
    public void loadLogin() {
        loginView = new LoginView();
        connectionController = new ConnectionController(connectionManager, loginView, this);
        loginView.setVisible(true);
    }
    
    public void loadChatWindow(String userId, String serverIp, String serverPort) {
        chatWindowView = new ChatWindowView();
        chatWindowView.updateCurrentServerLabel(serverIp, serverPort);
        chatWindowView.quitButtonAction(e -> quitClient());
        
        messageController = new MessageController(connectionManager, chatWindowView, userId);
        
        chatWindowView.setVisible(true);
        loginView.dispose();
    }
    
    private void quitClient() {
        connectionManager.disconnect();
        chatWindowView.dispose();
        System.exit(0);
    }
}