package test.client.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.net.ConnectException;
import client.model.ConnectionManager;

import static org.junit.jupiter.api.Assertions.*;

class ConnectionManagerTest {
    private ConnectionManager connectionManager;
    private final String validUserId = "UserId1";
    private final String validServerIp = "localhost"; 
    private final String validServerPort = "1549";

    @BeforeEach
    void setUp() {
        connectionManager = new ConnectionManager();
    }

    private void assertIllegalArgumentException(String userId, String serverIp, String serverPort, String expectedMessage) {
        IllegalArgumentException e = assertThrows(IllegalArgumentException.class, () -> connectionManager.connect(userId, serverIp, serverPort));
        assertEquals(expectedMessage, e.getMessage());
    }

    // userId tests
    @Test
    void connect_ThrowsIllegalArgumentException_IfConnectWithEmptyUserId() {
        assertIllegalArgumentException("", validServerIp, validServerPort, "All fields are required!");
    }

    @Test
    void connect_ThrowsIllegalArgumentException_IfConnectWithWhitespaceUserId() {
        assertIllegalArgumentException(" ", validServerIp, validServerPort, "User ID must be alphanumeric!");
    }

    @Test
    void connect_ThrowsIllegalArgumentException_IfConnectWithNonAlphanumericUserId() {
        assertIllegalArgumentException("#UserId!!!", validServerIp, validServerPort, "User ID must be alphanumeric!");
    }

    // serverIp tests
    @Test
    void connect_ThrowsIllegalArgumentException_IfConnectWithEmptyServerIp() {
        assertIllegalArgumentException(validUserId, "", validServerPort, "All fields are required!");
    }

    @Test
    void connect_ThrowsIllegalArgumentException_IfConnectWithInvalidServerIpFormat() {
        assertIllegalArgumentException(validUserId, "192.168", validServerPort, "Invalid IP address!");
        assertIllegalArgumentException(validUserId, "256.256.256.256", validServerPort, "Invalid IP address!");
        assertIllegalArgumentException(validUserId, "serverIp", validServerPort, "Invalid IP address!");
    }

    // serverPort tests
    @Test
    void connect_ThrowsIllegalArgumentException_IfConnectWithEmptyServerPort() {
        assertIllegalArgumentException(validUserId, validServerIp, "", "All fields are required!");
    }

    @Test
    void connect_ThrowsIllegalArgumentException_IfConnectWithInvalidServerPortRange() {
        assertIllegalArgumentException(validUserId, validServerIp, "-1", "Port must be between 0 and 65535!");
        assertIllegalArgumentException(validUserId, validServerIp, "65536", "Port must be between 0 and 65535!");
    }

    // Connection tests
    @Test
    void connect_ThrowsConnectException_IfChatServerOffline() {
        assertThrows(ConnectException.class, () -> connectionManager.connect(validUserId, validServerIp, validServerPort));
    }

    // Connection tests
    /*
    @Test
    void testConnectWithValidInput() {
    }
*/
}
    
