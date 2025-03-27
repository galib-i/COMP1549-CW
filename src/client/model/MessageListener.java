package client.model;

import common.model.Message;

@FunctionalInterface
public interface MessageListener {
    void controlCommunication(Message message);
}