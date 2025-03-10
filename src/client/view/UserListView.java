package client.view;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.DefaultListModel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SwingUtilities;

public class UserListView extends JPanel {
    private final JList<String> usersList;
    private final DefaultListModel<String> usersModel;

    public UserListView() {
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createTitledBorder("Online"));
        setPreferredSize(new Dimension(120, 0));

        usersModel = new DefaultListModel<>();  // Allows list & UI to be updated at runtime
        usersList = new JList<>(usersModel);
        
        JScrollPane scrollPane = new JScrollPane(usersList);
        add(scrollPane, BorderLayout.CENTER);
    }

    public void updateUserList(List<String> users, String userId) {
        // Add suffix (You) to the current user
        Integer index = users.indexOf(userId);
        users.set(index, userId + " (You)");

        SwingUtilities.invokeLater(() -> {
            usersModel.clear();
            usersModel.addAll(users);
        });
    }
}
