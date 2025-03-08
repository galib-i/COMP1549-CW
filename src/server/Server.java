package server;

import server.controller.ConnectionController;
import server.model.UserManager;

import java.io.IOException;
import java.net.ServerSocket;

public class Server {
    private static final int PORT = 1549;

    public static void main(String[] args) {
        UserManager userManager = new UserManager();
        ConnectionController connectionController = new ConnectionController(userManager);
        
        System.out.println("Server is running...");
        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            while (true) {
                connectionController.handleNewConnection(serverSocket.accept());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}