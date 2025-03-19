package client.controller;

import java.io.IOException;
import java.net.ConnectException;

import client.model.ConnectionManager;
import client.view.ChatWindowView;
import client.view.ConnectionView;

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
        String userId = details.userId();
        String serverIp = details.serverIp();
        String serverPort = details.serverPort();

        try {
            this.chatWindowView = new ChatWindowView();
            chatWindowView.updateCurrentServerLabel(serverIp, serverPort);
            chatWindowView.quitButtonAction(e -> quitConnection());

            this.messageController = new MessageController(model, chatWindowView, userId);

            model.connect(userId, serverIp, serverPort);

            chatWindowView.setVisible(true);
            view.dispose();

        } catch (ConnectException e) {
            view.showMessage("Error", "Connection refused!\n(Is the chat server running?)");
        } catch (IllegalArgumentException | IOException e) {
            view.showMessage("Error", e.getMessage());
        };
    }

    private void quitConnection() {
        model.disconnect();
        chatWindowView.dispose();
        System.exit(0);
    }
}
