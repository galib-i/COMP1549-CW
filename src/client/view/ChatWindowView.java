package client.view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

public class ChatWindowView extends JFrame {
    private final ChatView chatView = new ChatView();
    private final UserListView userListView = new UserListView();
    private final JTextField messageField = new JTextField();
    private final JButton sendButton = new JButton("Send"); 
    private final JButton quitButton = new JButton("Quit");
    private final JLabel currentServerLabel = new JLabel();

    public ChatWindowView() {
        JPanel rootPanel = new JPanel(new BorderLayout(10, 10));
        rootPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10)); // Add padding
        JPanel mainPanel = new JPanel(new BorderLayout(0, 10)); 
      
        mainPanel.add(chatView, BorderLayout.CENTER);
        rootPanel.add(userListView, BorderLayout.EAST);
       
        JPanel inputPanel = new JPanel(new BorderLayout(10, 0));
        messageField.setBorder(BorderFactory.createLineBorder(Color.GRAY, 1));
        inputPanel.add(messageField, BorderLayout.CENTER);
        inputPanel.add(sendButton, BorderLayout.EAST);
        mainPanel.add(inputPanel, BorderLayout.SOUTH);
        rootPanel.add(mainPanel, BorderLayout.CENTER);

        JPanel infoPanel = new JPanel(new BorderLayout(5, 0));
        infoPanel.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));
        infoPanel.add(currentServerLabel, BorderLayout.WEST);
        infoPanel.add(quitButton, BorderLayout.EAST);
        rootPanel.add(infoPanel, BorderLayout.SOUTH);

        add(rootPanel);
        setSize(700, 400);
        setLocationRelativeTo(null); // Center window on the screen
 
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        exitWindowAction(); // x button mimics quit button click
    }


    private void exitWindowAction() {
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                quitButton.doClick();
            }
        });
    }

    public void updateCurrentServerLabel(String serverIp, String serverPort) {
        currentServerLabel.setText("Connected to %s:%s".formatted(serverIp, serverPort));
    }

    public void quitButtonAction(ActionListener listener) {
        quitButton.addActionListener(listener);
    }

    public void sendButtonAction(ActionListener listener) {
        sendButton.addActionListener(listener);
        messageField.addActionListener(listener); // Enter key mimics send button click
    }

    public String getMessage() {
        String message = messageField.getText();
        messageField.setText("");  // Clear message field
        if (message.isBlank()) {
            return null;
        }
        return message;
    }

    public ChatView getChatView() {
        return chatView;
    }

    public UserListView getUserListView() {
        return userListView;
    }
}
