package client.controller;

import java.io.IOException;
import java.net.ConnectException;

import client.model.ConnectionManager;
import client.view.ConnectionView;
import client.view.ChatWindowView;

public class ConnectionController {
    private final ConnectionManager model;
    private final ConnectionView view;
    private ChatWindowView chatWindowView;
    private MessageController messageController;
    
    public ConnectionController(ConnectionManager model, ConnectionView view) {
        this.model = model;
        this.view = view;
        
        view.connectButtonAction(e -> requestConnection());
    }
    
    private void requestConnection() {
        ConnectionView.ConnectionDetails details = view.getConnectionDetails();
        
        try {
            this.chatWindowView = new ChatWindowView();
            chatWindowView.updateCurrentServerLabel(details.serverIp(), details.serverPort());
            chatWindowView.quitButtonAction(e -> quitConnection());

            this.messageController = new MessageController(model, chatWindowView, details.userId());

            model.connect(details.userId(), details.serverIp(), details.serverPort());
            view.showMessage("Success", "Connected successfully!");

            chatWindowView.setVisible(true);
            view.dispose();
            
        } catch (IllegalArgumentException e) {
            view.showMessage("Error", e.getMessage());
        } catch (ConnectException e) {
            view.showMessage("Error", "Connection refused!\n(Is the chat server running?)");
        } catch (IOException e) {
            view.showMessage("Error", e.getMessage());
        }
    }

    private void quitConnection() {
        model.disconnect();
        chatWindowView.dispose();
        System.exit(0);
    }
}
