package client.controller;

import java.io.IOException;
import java.net.ConnectException;

import client.model.ConnectionManager;
import client.view.ConnectionView;
import client.view.MainView;

public class ConnectionController {
    private final ConnectionManager model;
    private final ConnectionView view;
    private MainView mainView;
    private MessageController messageController;
    
    public ConnectionController(ConnectionManager model, ConnectionView view) {
        this.model = model;
        this.view = view;
        
        view.connectButtonAction(e -> requestConnection());
    }
    
    private void requestConnection() {
        ConnectionView.ConnectionDetails details = view.getConnectionDetails();
        
        try {
            this.mainView = new MainView();
            mainView.updateCurrentServerLabel(details.serverIp(), details.serverPort());
            mainView.quitButtonAction(e -> quitConnection());

            this.messageController = new MessageController(model, mainView, details.userId());

            model.connect(details.userId(), details.serverIp(), details.serverPort());
            view.showMessage("Success", "Connected successfully!");

            mainView.setVisible(true);
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
        mainView.dispose();
        System.exit(0);
    }
}
