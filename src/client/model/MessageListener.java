package client.model;

import common.model.Message;

public interface MessageListener {
    void onMessageReceived(Message<?> message);
}