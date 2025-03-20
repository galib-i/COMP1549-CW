package client.model;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ConnectException;
import java.net.Socket;

import common.model.Message;
import common.util.MessageFormatter;

public class ConnectionManager {
    private Socket socket;
    private BufferedReader reader;
    private PrintWriter writer;
    private Thread messageListenerThread;
    private MessageListener messageListener;
    private String userId;
    
    public void connect(String userId, String serverIp, String serverPort) throws IllegalArgumentException, ConnectException, IOException {
        validateInput(userId, serverIp, serverPort);
        
        this.userId = userId;
        
        socket = new Socket(serverIp, Integer.parseInt(serverPort));
        reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        writer = new PrintWriter(socket.getOutputStream(), true);
        
        // Attempt to connect user to server
        Message joinMessage = Message.userJoin(userId);
        writer.println(MessageFormatter.format(joinMessage));

        // Check if user ID is already in use
        String response = reader.readLine();
        Message responseMsg = MessageFormatter.parse(response);
        
        if (responseMsg.getType() == Message.Type.REJECT_USER_JOIN) {
            socket.close();
            throw new IllegalArgumentException("User ID already in use!");
        } else {
            processMessage(response);
        }

        messageListenerThread = new Thread(this::listenForMessages);
        messageListenerThread.setDaemon(true);
        messageListenerThread.start();
    }

    public void setMessageListener(MessageListener listener) {
        this.messageListener = listener;
    }
    
    private void listenForMessages() { // DO NOT MODIFY
        try {
            String message;
            while ((message = reader.readLine()) != null) {
                processMessage(message);
            }
        } catch (IOException e) {
            if (!socket.isClosed()) {
                e.printStackTrace();
            }
        }
    }
    
    private void processMessage(String messageString) {
        Message parsedMessage = MessageFormatter.parse(messageString);
        messageListener.onMessageReceived(parsedMessage);
    }

    public void disconnect() {
        try {
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String getUserId() {
        return userId;
    }

    public void sendMessage(String message) {
        if (message == null) {
            return;
        }
        Message chatMessage = Message.sendMessage(userId, message);
        writer.println(MessageFormatter.format(chatMessage));
    }
    
    public void sendUserDetailsRequest(String userId) {
        Message detailsRequest = Message.requestUserDetails(userId);
        writer.println(MessageFormatter.format(detailsRequest));
    }
    
    public void toggleStatus() {        
        Message statusMessage = Message.statusUpdate(userId);
        writer.println(MessageFormatter.format(statusMessage));
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
