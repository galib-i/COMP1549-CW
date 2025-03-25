package client.controller;

import java.io.IOException;

import client.model.ConnectionManager;
import client.view.ChatWindowView;
import client.view.LoginView;

public class ClientController {
    private final ConnectionManager connectionManager = new ConnectionManager();
    private LoginView loginView;
    private ChatWindowView chatWindowView;
    
    public void loadLogin() {
        loginView = new LoginView();
        new ConnectionController(connectionManager, loginView, this);
        loginView.setVisible(true);
    }
    
    public void loadChatWindow(String userId, String serverIp, String serverPort) throws IOException {
        chatWindowView = new ChatWindowView();
        chatWindowView.updateCurrentServerLabel(serverIp, serverPort);
        chatWindowView.quitButtonAction(e -> quitClient());
        
        new MessageController(connectionManager, chatWindowView);
        connectionManager.connect(userId, serverIp, serverPort);

        chatWindowView.setVisible(true);
        loginView.dispose();
    }
    
    private void quitClient() {
        connectionManager.disconnect();
        chatWindowView.dispose();
        System.exit(0);
    }
}