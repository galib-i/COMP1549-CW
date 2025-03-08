package client.view;

import java.awt.*;
import java.awt.event.ActionListener;
import javax.swing.*;


public class ConnectionView extends JFrame {
    private JTextField userIdField, serverIpField, serverPortField;
    private JButton connectButton;

    public ConnectionView() {
        JPanel rootPanel = new JPanel(new BorderLayout(10, 10));
        rootPanel.setBorder(BorderFactory.createEmptyBorder(64, 32, 64, 32)); // Add padding
        JPanel mainPanel = new JPanel(new BorderLayout(5, 5));
        
        userIdField = new JTextField();
        JPanel userIdPanel = createLabelledField("User ID:", "", userIdField);
        mainPanel.add(userIdPanel, BorderLayout.NORTH);
        
        JPanel serverDetailsPanel = new JPanel(new BorderLayout(5, 5));
        serverDetailsPanel.setBorder(BorderFactory.createTitledBorder("Server"));

        serverIpField = new JTextField();
        serverPortField = new JTextField();
        JPanel serverIpPanel = createLabelledField("IP:", "localhost", serverIpField);
        JPanel serverPortPanel = createLabelledField("Port:", "1549", serverPortField);
        serverDetailsPanel.add(serverIpPanel, BorderLayout.NORTH);
        serverDetailsPanel.add(serverPortPanel, BorderLayout.CENTER);
        mainPanel.add(serverDetailsPanel, BorderLayout.CENTER);

        connectButton = new JButton("Connect");
        mainPanel.add(connectButton, BorderLayout.SOUTH);

        rootPanel.add(mainPanel, BorderLayout.CENTER);
        add(rootPanel);
        pack();
    
        setLocationRelativeTo(null); // Center window on the screen
        setResizable(false);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }
    
    private JPanel createLabelledField(String labelText, String placeholder, JTextField field) {
        field.setBorder(BorderFactory.createLineBorder(Color.GRAY, 1));
        field.setColumns(15);
        field.setText(placeholder);

        JLabel label = new JLabel(labelText);
        label.setFont(label.getFont().deriveFont(Font.PLAIN)); // Unbold default font
        label.setPreferredSize(new Dimension(45, 0));
    
        JPanel labelledField = new JPanel(new BorderLayout(5, 5));
        labelledField.add(label, BorderLayout.WEST);
        labelledField.add(field, BorderLayout.CENTER);
        
        return labelledField;
    }

    public record ConnectionDetails(String userId, String serverIp, String serverPort) {}

    public ConnectionDetails getConnectionDetails() {
        return new ConnectionDetails(userIdField.getText(), serverIpField.getText(), serverPortField.getText());
    }

    public void connectButtonAction(ActionListener listener) {
        connectButton.addActionListener(listener);
    }

    public void showMessage(String title, String message) {
        if (title == "Error") {
            JOptionPane.showMessageDialog(this, message, title, JOptionPane.ERROR_MESSAGE);
        } else {
            JOptionPane.showMessageDialog(this, message, title, JOptionPane.INFORMATION_MESSAGE);
        }
    }
}
