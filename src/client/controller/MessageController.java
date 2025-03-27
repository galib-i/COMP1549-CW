package client.controller;

import java.util.Map;

import client.model.ConnectionManager;
import client.model.MessageListener;
import client.view.ChatWindowView;
import common.model.Message;

public class MessageController implements MessageListener {
    private final ConnectionManager model;
    private final ChatWindowView view;
    
    public MessageController(ConnectionManager model, ChatWindowView view, ActivityController activityController) {
        this.model = model;
        this.view = view;
        
        view.getUserListView().viewDetailsAction(e -> showUserDetails());
        view.getUserListView().privateMessageAction(e -> openPrivateChat());
        view.sendButtonAction(e -> sendMessage());
        model.setMessageListener(this);
    }

    @Override
    public void controlCommunication(Message message) {
        switch (message.getType()) {
            case USER_LIST -> controlUserListResponse(message);
            case USER_DETAILS_RESPONSE -> controlUserDetailsResponse(message);
            case OPEN_PRIVATE_CHAT -> openPrivateChat(message);
            case USER_QUIT -> controlDisconnection(message);
            case MESSAGE -> processMessage(message);
            default -> {}
        }
    }

    private void sendMessage() {
        String messageText = view.getMessage();
        if (messageText == null) {
            return;
        }
        String chatName = view.getChatView().getCurrentChatName();
        model.sendMessage(chatName, messageText);
    }

    private void processMessage(Message message) {
        String sender = message.getSender();
        String recipient = message.getRecipient();

        if (sender.equals("SERVER")) {
            sender = "[SERVER]";
        }

        displayMessage(sender, recipient, message);
    }

    private void displayMessage(String sender, String recipient, Message message) {
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

    private void controlUserListResponse(Message message) {
        Map<String, Map<String, String>> userList = (Map<String, Map<String, String>>)message.getContent();
        view.getUserListView().updateUserList(userList, model.getUserId());
    }

    private void showUserDetails() {
        String selectedUser = view.getUserListView().getSelectedUser();  
        model.sendUserDetailsRequest(model.getUserId(), selectedUser);
    }

    private void controlUserDetailsResponse(Message message) {
        Map<String, String> details = (Map<String, String>) message.getContent();
        String userId = details.get("userId");

        String formattedDetails = "User ID: %s\nRole: %s\nStatus: %s\nConnected through: %s"
            .formatted(userId, details.get("role"), details.get("status"), details.get("socketAddress")
        );
        
        view.getUserListView().showMessage("%s's details".formatted(userId), formattedDetails);
    }

     private void openPrivateChat() {
        String selectedUser = view.getUserListView().getSelectedUser();
        view.getChatView().openPrivateChat(selectedUser);

        model.openPrivateChat(selectedUser);
    }

    private void openPrivateChat(Message message) {
        String requestingUser = message.getSender();
        view.getChatView().openPrivateChat(requestingUser);
        view.getChatView().displayMessage("Group", message.getTimestamp(), "[SERVER]", "%s has opened a private chat with you.".formatted(requestingUser));
    }

    private void controlDisconnection(Message message) {
        String userId = (String) message.getSender();
        view.getChatView().closePrivateChat(userId);
    }
}
