package client.view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.util.HashMap;

import javax.swing.BorderFactory;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;

public class ChatView extends JPanel {
    private final JTabbedPane chatTabs;
    private final HashMap<String, ChatPanel> chats;

    public ChatView() {
        setLayout(new BorderLayout());
        chatTabs = new JTabbedPane();
        chats = new HashMap<>();

        ChatPanel groupChatPanel = new ChatPanel();
        chats.put("Group", groupChatPanel);
        chatTabs.addTab("Group", groupChatPanel);
        
        add(chatTabs, BorderLayout.CENTER);
    }

    public void displayMessage(String chatName, String sender, String message) {
        ChatPanel chatPanel = chats.get(chatName);
        chatPanel.chatArea.append(sender + ": " + message + "\n");
    }

    private static class ChatPanel extends JPanel {
        private final JTextArea chatArea;

        public ChatPanel() {
            setLayout(new BorderLayout());

            chatArea = new JTextArea();
            chatArea.setEditable(false);
            chatArea.setLineWrap(true);
            chatArea.setWrapStyleWord(true);
            
            JScrollPane scrollPane = new JScrollPane(chatArea);
            scrollPane.setBorder(BorderFactory.createLineBorder(Color.GRAY, 1));
            add(scrollPane, BorderLayout.CENTER);
        }
    }
}
