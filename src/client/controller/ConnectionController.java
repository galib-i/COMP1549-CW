package client.controller;

import java.io.IOException;
import java.net.ConnectException;

import client.model.ConnectionManager;
import client.view.ConnectionView;

public class ConnectionController {
    private final ConnectionManager model;
    private final ConnectionView view;
    
    public ConnectionController(ConnectionManager model, ConnectionView view) {
        this.model = model;
        this.view = view;
        
        view.setConnectButtonListener(e -> handleConnection());
    }
    
    private void handleConnection() {
        ConnectionView.ConnectionDetails details = view.getConnectionDetails();
        
        try {
            model.connect(details.userId(), details.serverIp(), details.serverPort());
            view.showMessage("Success", "Connected successfully!");
            // Open the chat
        } catch (IllegalArgumentException e) {
            view.showMessage("Error", e.getMessage());
        } catch (ConnectException e) {
            view.showMessage("Error", "Connection refused!\n(Is the chat server running?)");
        } catch (IOException e) {
            view.showMessage("Error", e.getMessage());
        }
    }

}
