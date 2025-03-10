package client.model;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ConnectException;
import java.net.Socket;

public class ConnectionManager {
    private Socket socket;
    private BufferedReader in;
    private PrintWriter out;
    private Thread messageListenerThread;
    private MessageListener messageListener;
    
    public void connect(String userId, String serverIp, String serverPort) throws IllegalArgumentException, ConnectException, IOException {
        validateInput(userId, serverIp, serverPort);
        
        socket = new Socket(serverIp, Integer.parseInt(serverPort));
        in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        out = new PrintWriter(socket.getOutputStream(), true);
            
        out.println(userId);  // Send the userId to the server

        messageListenerThread = new Thread(this::listenForMessages);
        messageListenerThread.setDaemon(true);
        messageListenerThread.start();
    }

    public void setMessageListener(MessageListener listener) {
        this.messageListener = listener;
    }
    
    private void listenForMessages() {
        try {
            String message;
            while ((message = in.readLine()) != null) {
                if (messageListener != null && !message.isEmpty()) {
                    int separatorIndex = message.indexOf(": ");
                    if (separatorIndex > 0) {
                        String sender = message.substring(0, separatorIndex);
                        String content = message.substring(separatorIndex + 2);
                        messageListener.onMessageReceived(sender, content);
                    }
                }
            }
        } catch (IOException e) {
            if (!socket.isClosed()) {
                e.printStackTrace();
            }
        }
    }

    public void disconnect() {
        try {
            if (socket != null) {
                socket.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void sendMessage(String message) {
        out.println(message);
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
