package server;

import java.io.IOException;
import java.net.ServerSocket;

import server.controller.ConnectionController;
import server.model.UserManager;

public class Server {
    private static final int PORT = 1549;

    public static void main(String[] args) {
        UserManager userManager = new UserManager();
        ConnectionController connectionController = new ConnectionController(userManager);
        
        System.out.println("Starting server...");
        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            while (true) {
                connectionController.handleNewConnection(serverSocket.accept());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}