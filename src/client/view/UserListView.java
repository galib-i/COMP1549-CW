package client.view;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Rectangle;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.swing.BorderFactory;
import javax.swing.DefaultListModel;
import javax.swing.JList;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.SwingUtilities;

public class UserListView extends JPanel {
    private static final String SUFFIX_YOU= " (You)";
    private static final String SUFFIX_COORDINATOR = " 👑";
    private static final String SUFFIX_INACTIVE = " 🌙";

    private final JList<String> usersList = new JList<>();
    private final DefaultListModel<String> usersModel = new DefaultListModel<>();
    private final JPopupMenu userContextMenu = new JPopupMenu();
    private final JMenuItem optionViewDetails = new JMenuItem("View user details");
    private final JMenuItem optionPrivateMessage = new JMenuItem("Private message");

    public UserListView() {
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createTitledBorder("Online"));
        setPreferredSize(new Dimension(120, 0));
        
        usersList.setModel(usersModel);

        rightClickAction();
        JScrollPane scrollPane = new JScrollPane(usersList);
        add(scrollPane, BorderLayout.CENTER);
    }

    public void updateUserList(Map<String, Map<String, String>> userList, String userId) {
        List<String> formattedUsers = new ArrayList<>();

        for (Map.Entry<String, Map<String, String>> entry : userList.entrySet()) {
            String user = entry.getKey();
            Map<String, String> userDetails = entry.getValue();
            String role = userDetails.get("role");
            String status = userDetails.get("status");
            String displayName = user;

            if (user.equals(userId)) {
                displayName += SUFFIX_YOU;
            }

            if (role.equals("COORDINATOR")) {
                displayName += SUFFIX_COORDINATOR;
            }

            if (status.equals("INACTIVE")) {    
                displayName += SUFFIX_INACTIVE;
            }

            formattedUsers.add(displayName);
        }

        SwingUtilities.invokeLater(() -> {
            usersModel.clear();
            usersModel.addAll(formattedUsers);
        });
    }

    private void rightClickAction() {
        userContextMenu.add(optionViewDetails);
        userContextMenu.add(optionPrivateMessage);
        
        usersList.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseReleased(MouseEvent e) {
                if (SwingUtilities.isRightMouseButton(e)) {
                    int index = usersList.locationToIndex(e.getPoint()); // Find the index of the clicked item
                    Rectangle clickedCellBounds = usersList.getCellBounds(index, index);
 
                    if (clickedCellBounds.contains(e.getPoint())) { // If clicked point is within the rectangular bounds
                        String clickedUser = usersModel.getElementAt(index);
                        if (!clickedUser.contains("(You)")) { // Ignore self-clicks
                            usersList.setSelectedIndex(index);
                            userContextMenu.show(usersList, e.getX(), e.getY());
                        }
                    }
                }
            }
        });
    }

    public void privateMessageAction(ActionListener listener) {
        optionPrivateMessage.addActionListener(listener);
    }

    public void viewDetailsAction(ActionListener listener) {
        optionViewDetails.addActionListener(listener);
    }

    public String getSelectedUser() {
        String selectedUser = usersList.getSelectedValue();
        return selectedUser.split(" ")[0]; // Remove any suffixes
    }

    public void showMessage(String title, String message) {
        SwingUtilities.invokeLater(() -> {
            JOptionPane.showMessageDialog(this, message, title, JOptionPane.INFORMATION_MESSAGE);
        });
    }
}