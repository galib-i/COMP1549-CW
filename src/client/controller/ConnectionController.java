package client.controller;

import java.io.IOException;
import java.net.ConnectException;

import client.model.ConnectionManager;
import client.view.LoginView;

public class ConnectionController {
    private final ConnectionManager model;
    private final LoginView view;
    private final ClientController clientController;
    
    public ConnectionController(ConnectionManager model, LoginView view, ClientController clientController) {
        this.model = model;
        this.view = view;
        this.clientController = clientController;
        
        view.connectButtonAction(e -> requestConnection());
    }
    
    private void requestConnection() {
        LoginView.ConnectionDetails details = view.getConnectionDetails();
        String userId = details.userId();
        String serverIp = details.serverIp();
        String serverPort = details.serverPort();

        try {
            model.connect(userId, serverIp, serverPort);
            clientController.loadChatWindow(userId, serverIp, serverPort);

        } catch (ConnectException e) {
            view.showMessage("Error", "Connection refused!\n(Is the chat server running?)");
        } catch (IllegalArgumentException | IOException e) {
            view.showMessage("Error", e.getMessage());
        }
    }
}
