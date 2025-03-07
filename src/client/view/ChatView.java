package client.view;

import javax.swing.*;
import java.awt.*;
import java.util.HashMap;

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
