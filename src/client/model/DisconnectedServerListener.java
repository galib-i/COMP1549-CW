package client.model;

/**
 * Interface for when the server is disconnected, but the clients are still running
 */
@FunctionalInterface
public interface DisconnectedServerListener {
    void whenServerDisconnected();
}