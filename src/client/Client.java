package client;

import javax.swing.SwingUtilities;

import client.controller.ConnectionController;
import client.model.ConnectionManager;
import client.view.ConnectionView;

public class Client {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            ConnectionView view = new ConnectionView();
            ConnectionManager model = new ConnectionManager();
            ConnectionController controller = new ConnectionController(model, view);
            view.setVisible(true);
        });
    }
}
 