package client.model;

import java.io.*;
import java.net.*;

public class ConnectionManager {
    private Socket socket;
    private BufferedReader in;
    private PrintWriter out;
    
    public void connect(String userId, String serverIp, String serverPort) throws IllegalArgumentException, ConnectException, IOException {
        validateInput(userId, serverIp, serverPort);
        
        socket = new Socket(serverIp, Integer.parseInt(serverPort));
        in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        out = new PrintWriter(socket.getOutputStream(), true);
            
        out.println(userId);  // Send the user ID to the server
    }

    public void disconnect() throws IOException {
        if (socket != null) {
            socket.close();
        }
    }

    private void validateInput(String userId, String serverIp, String serverPort) {
        if (userId.isEmpty() || serverIp.isEmpty() || serverPort.isEmpty()) {
            throw new IllegalArgumentException("All fields are required!");
        }

        if (!userId.matches("[A-Za-z0-9]+")) {
            throw new IllegalArgumentException("User ID must be alphanumeric!");
        }

        if (!serverIp.matches("^((25[0-5]|(2[0-4]|1\\d|[1-9]|)\\d)\\.?\\b){4}$") && !serverIp.equals("localhost")) {
            throw new IllegalArgumentException("Invalid IP address!");
        }

        try {
            int port = Integer.parseInt(serverPort);
            if (!(port > 0 && port < 65535)) {
                throw new IllegalArgumentException("Port must be between 0 and 65535!");
            }
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Port must be a number!");
        }
    }
}
