package client;

import javax.swing.SwingUtilities;

import client.controller.ClientController;

public class Client {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new ClientController().loadLogin());
    }
}
