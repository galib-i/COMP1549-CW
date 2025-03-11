package server;

import java.io.IOException;
import java.net.ServerSocket;
import common.util.ConfigLoader;
import server.controller.ConnectionController;
import server.model.UserManager;

public class Server {
    public static void main(String[] args) {
        ConfigLoader config = new ConfigLoader();
        int port = config.getInt("default.server.port");

        UserManager userManager = new UserManager();
        ConnectionController connectionController = new ConnectionController(userManager);
        
        System.out.println("Starting server...");
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            while (true) {
                connectionController.handleNewConnection(serverSocket.accept());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}