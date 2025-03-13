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
    private final ChatView chatView;
    private final UserListView userListView;
    private JTextField messageField;
    private JButton sendButton, quitButton;
    private JLabel currentServerLabel;

    public ChatWindowView() {
        JPanel rootPanel = new JPanel(new BorderLayout(10, 10));
        rootPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10)); // Add padding
        JPanel mainPanel = new JPanel(new BorderLayout(0, 10)); 
      
        chatView = new ChatView();
        mainPanel.add(chatView, BorderLayout.CENTER);

        userListView = new UserListView();
        rootPanel.add(userListView, BorderLayout.EAST);
       
        JPanel inputPanel = new JPanel(new BorderLayout(10, 0));
        messageField = new JTextField();
        messageField.setBorder(BorderFactory.createLineBorder(Color.GRAY, 1));
        sendButton = new JButton("Send");
        inputPanel.add(messageField, BorderLayout.CENTER);
        inputPanel.add(sendButton, BorderLayout.EAST);
        mainPanel.add(inputPanel, BorderLayout.SOUTH);
        rootPanel.add(mainPanel, BorderLayout.CENTER);

        JPanel currentServerPanel = new JPanel(new BorderLayout(5, 0));
        currentServerPanel.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));
        currentServerLabel = new JLabel();
        currentServerPanel.add(currentServerLabel, BorderLayout.WEST);
        quitButton = new JButton("Quit");
        currentServerPanel.add(quitButton, BorderLayout.EAST);
        rootPanel.add(currentServerPanel, BorderLayout.SOUTH);

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
        currentServerLabel.setText("Connected to " + serverIp + ":" + serverPort);
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
        if (message.trim().isEmpty()) {  // Ignore empty messages
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
