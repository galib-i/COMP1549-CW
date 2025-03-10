package client.view;

import javax.swing.*;
import java.awt.*;
import java.util.Collection;

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


    public void addUser(String user) {
        SwingUtilities.invokeLater(() -> usersModel.addElement(user));
    }

    public void updateUserList(Collection<String> users, String userId) {
        SwingUtilities.invokeLater(() -> {
            usersModel.clear();
            for (String user : users) {
                if (user.equals(userId)) {
                    user += " (You)";
                }
                addUser(user);
            }
        });
    }
}
