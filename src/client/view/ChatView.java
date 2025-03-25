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
    private final JTabbedPane chatTabs = new JTabbedPane();
    private final HashMap<String, ChatPanel> chats = new HashMap<>();

    public ChatView() {
        setLayout(new BorderLayout());

        ChatPanel groupChatPanel = new ChatPanel();
        chats.put("Group", groupChatPanel);
        chatTabs.addTab("Group", groupChatPanel);
        
        add(chatTabs, BorderLayout.CENTER);
    }

    public void displayMessage(String chatName, String timestamp, String sender, String message) {
        ChatPanel chatPanel = chats.get(chatName);
        chatPanel.chatArea.append("[" + timestamp + "] " + sender + ": " + message + "\n");
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

    public void openPrivateChat(String userId) {
        ChatPanel privateChatPanel = new ChatPanel();
        chats.put(userId, privateChatPanel);
        chatTabs.addTab(userId, privateChatPanel);
    }

    public void closePrivateChat(String userId) {
        ChatPanel privateChatPanel = chats.get(userId);
        int index = chatTabs.indexOfComponent(privateChatPanel);
        chats.remove(userId);
        chatTabs.removeTabAt(index);
    }
}
