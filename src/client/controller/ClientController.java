package client.controller;

import java.io.IOException;

import client.model.ActivityModel;
import client.model.ConnectionManager;
import client.view.ChatWindowView;
import client.view.LoginView;

public class ClientController {
    private final ConnectionManager connectionManager;
    private final LoginView loginView;
    private final ChatWindowView chatWindowView;
    private final ActivityModel activityModel;

    public ClientController(ConnectionManager connectionManager) {
        this.connectionManager = connectionManager;
        this.loginView = new LoginView();
        this.chatWindowView = new ChatWindowView();
        this.activityModel = new ActivityModel(connectionManager);
    }
    
    public void loadLogin() {
        new ConnectionController(connectionManager, loginView, this);
        loginView.setVisible(true);
    }
    
    public void loadChatWindow(String userId, String serverIp, String serverPort) throws IOException {
        chatWindowView.updateCurrentServerLabel(serverIp, serverPort);
        chatWindowView.quitButtonAction(e -> quitClient());
        
        ActivityController activityController = new ActivityController(activityModel, chatWindowView);
        new MessageController(connectionManager, chatWindowView, activityController);
        
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