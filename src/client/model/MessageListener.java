package client.model;

public interface MessageListener {
    void onMessageReceived(String sender, String content);
}