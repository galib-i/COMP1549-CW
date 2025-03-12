package server;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;

import common.util.ConfigLoader;
import server.controller.ConnectionController;
import server.model.UserManager;

public class Server {
    public static void main(String[] args) {
        ConfigLoader config = new ConfigLoader();
        UserManager userManager = new UserManager();
        ConnectionController connectionController = new ConnectionController(userManager);

        String ip = config.get("default.server.ip");
        int port = config.getInt("default.server.port");

        System.out.println("STARTING SERVER (" + ip + ":" + port + ")\n");

        try (ServerSocket serverSocket = new ServerSocket(port, 0, InetAddress.getByName(ip))) {
            while (true) {
                connectionController.handleNewConnection(serverSocket.accept());
            }
        } catch (IOException e) {
            System.out.println("ERROR STARTING SERVER (" + e.getMessage() + ")\n");
        }
    }
}