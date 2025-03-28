package client.model;

import java.io.BufferedReader;
import java.io.IOException;
import java.net.SocketException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ConnectException;
import java.net.Socket;

import common.model.Message;
import common.util.MessageFormatter;

/**
 * Manages the connection between the client and the server, sends and receives communication
 */
public class ConnectionManager {
    private Socket socket;
    private BufferedReader reader;
    private PrintWriter writer;
    private Thread messageListenerThread;
    private MessageListener messageListener;
    private DisconnectedServerListener disconnectedServerListener;
    private String userId;
    
    /**
     * Sends a request to the server, checks if user is unique and starts a message listener thread
     * @param userId Id of the user to connect
     * @param serverIp Server IP address
     * @param serverPort Server port number
     * @throws IllegalArgumentException If input validation fails or user is not unique
     * @throws ConnectException If server cannot connect (e.g. server is not running)
     * @throws IOException Handles all other socket exceptions
     */
    public void connect(String userId, String serverIp, String serverPort) throws IllegalArgumentException, ConnectException, IOException {
        validateInput(userId, serverIp, serverPort);
        this.userId = userId;
        
        socket = new Socket(serverIp, Integer.parseInt(serverPort));
        reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        writer = new PrintWriter(socket.getOutputStream(), true);
        
        authenticateUser();

        messageListenerThread = new Thread(this::listenForMessages);
        messageListenerThread.start();
    }

    private void authenticateUser() throws IllegalArgumentException, IOException {
        Message joinMessage = Message.requestJoin(userId);
        writer.println(MessageFormatter.format(joinMessage));

        String response = reader.readLine();
        Message responseMsg = MessageFormatter.parse(response);
        
        if (responseMsg.getType() == Message.Type.REJECT_USER_JOIN) {
            socket.close();
            throw new IllegalArgumentException("User ID already in use!");
        } else {
            processMessage(response);
        }
    }

    public String getUserId() {
        return userId;
    }

    /**
     * Sets the message listener to handle incoming messages
     * @param listener MessageListener object
     * @see MessageListener
     */
    public void setMessageListener(MessageListener listener) {
        this.messageListener = listener;
    }
    /**
     * Sets the disconnection listener to handle server disconnection (server disconnects but client is still running)
     * @param listener
     * @see DisconnectedServerListener
     */
    public void setDisconnectionListener(DisconnectedServerListener listener) {
        this.disconnectedServerListener = listener;
    }

    public void sendMessage(String recipient, String message) {
        sendFormattedMessage(Message.sendMessage(userId, recipient, message));
    }
    
    public void sendUserDetailsRequest(String senderUserId, String targetUserId) {
        sendFormattedMessage(Message.requestUserDetails(userId, targetUserId));
    }
    
    public void toggleStatus() {        
        sendFormattedMessage(Message.updateStatus(userId));
    }
    
    public void openPrivateChat(String targetUserId) {
        sendFormattedMessage(Message.openPrivateChat(userId, targetUserId));
    }

    private void sendFormattedMessage(Message message) {
        if (message != null) {
            writer.println(MessageFormatter.format(message));
        }
    }

    private void listenForMessages() {
        try {
            String message;
            while ((message = reader.readLine()) != null) {
                processMessage(message);
            }
        } catch (SocketException e) {
            handleDisconnectedServer();
        } catch (IOException e) {
            if (!socket.isClosed()) {
                e.printStackTrace();
                handleDisconnectedServer();
            }
        }
    }
    
    private void processMessage(String messageString) {
        Message parsedMessage = MessageFormatter.parse(messageString);
        messageListener.controlCommunication(parsedMessage);
    }

    public void disconnect() {
        try {
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void handleDisconnectedServer() {
        disconnectedServerListener.whenServerDisconnected();
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
            if (!(port > 0 && port <= 65535)) {
                throw new IllegalArgumentException("Port must be between 0 and 65535!");
            }
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Port must be a number!");
        }
    }
}
