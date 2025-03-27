package client.controller;

import java.text.SimpleDateFormat;
import java.util.Map;

import client.model.ActivityTracker;
import client.model.ConnectionManager;
import client.model.MessageListener;
import client.view.ChatWindowView;
import common.model.Message;

public class MessageController implements MessageListener {
    private final ConnectionManager model;
    private final ChatWindowView view;
    
    public MessageController(ConnectionManager model, ChatWindowView view) {
        this.model = model;
        this.view = view;

        new ActivityTracker(model, view);
        
        view.getUserListView().viewDetailsAction(e -> showUserDetails());
        view.getUserListView().privateMessageAction(e -> openPrivateChat());
        view.sendButtonAction(e -> sendMessage());
        model.setMessageListener(this);
    }

    @Override
    public void handleCommunication(Message message) {
        switch (message.getType()) {
            case USER_LIST -> processUserListMessage(message);
            case USER_DETAILS_RESPONSE -> processUserDetails(message);
            case OPEN_PRIVATE_CHAT -> processPrivateChat(message);
            case USER_QUIT -> processUserQuit(message);
            case MESSAGE -> processMessage(message);
            default -> {}
        }
    }

    private void processUserListMessage(Message message) {
        Map<String, Map<String, String>> userList = (Map<String, Map<String, String>>)message.getContent();
        view.getUserListView().updateUserList(userList, model.getUserId());
    }

    private void processUserDetails(Message message) {
        Map<String, String> details = (Map<String, String>) message.getContent();
        String userId = details.get("userId");

        String formattedDetails = "User ID: %s\nRole: %s\nStatus: %s\nConnected through: %s"
            .formatted(userId, details.get("role"), details.get("status"), details.get("socketAddress")
        );
        
        view.getUserListView().showMessage("%s's details".formatted(userId), formattedDetails);
    }

    private void displayMessage(String sender, String recipient, Message message) { // BINGO
        System.out.println("Sender: %s, Recipient: %s, Message: %s".formatted(sender, recipient, message.getContent()));
        System.out.println(model.getUserId());
        String chatName;
        if (recipient.equals("Group") || (sender.equals("[SERVER]"))) {
            chatName = "Group";
        } else if (model.getUserId().equals(sender)) {
            chatName = recipient;
        } else {
            chatName = sender;
        }

        view.getChatView().displayMessage(chatName, message.getTimestamp(), sender, (String)message.getContent());
    }

    private void processMessage(Message message) {
        String sender = message.getSender();
        String recipient = message.getRecipient();

        if (sender.equals("SERVER")) {
            sender = "[SERVER]";
        }

        displayMessage(sender, recipient, message);
    }

    private void processPrivateChat(Message message) {
        String requestingUser = message.getSender();
        view.getChatView().openPrivateChat(requestingUser);
    }

    private void processUserQuit(Message message) {
        String userId = (String) message.getSender();
        view.getChatView().closePrivateChat(userId);
    }

    private void sendMessage() {
        String messageText = view.getMessage();
        if (messageText == null) {
            return;
        }
        String chatName = view.getChatView().getCurrentChatName();
        model.sendMessage(chatName, messageText);

    }

    private void showUserDetails() {
        String selectedUser = view.getUserListView().getSelectedUser();  
        model.sendUserDetailsRequest(model.getUserId(), selectedUser);
    }

    private void openPrivateChat() {
        String selectedUser = view.getUserListView().getSelectedUser();
        view.getChatView().openPrivateChat(selectedUser);

        model.openPrivateChat(selectedUser);
    }
}
