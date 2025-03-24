package client.model;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowEvent;
import java.awt.event.WindowFocusListener;

import javax.swing.JFrame;
import javax.swing.Timer;

public class ActivityTracker {
    private static final int INACTIVITY_TIMEOUT = 30000; // 30 seconds
    private final ConnectionManager connectionManager;
    private Timer inactivityTimer;
    private boolean active = true;
    
    public ActivityTracker(ConnectionManager connectionManager, JFrame frame) {
        this.connectionManager = connectionManager;

        frame.addWindowFocusListener(new WindowFocusListener() {
            public void windowGainedFocus(WindowEvent e) { 
                activityDetected(); 
            }

            public void windowLostFocus(WindowEvent e) {} // do nothing
        });
        
        frame.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) { 
                activityDetected(); 
            }
        });
        
        startInactivityTimer();
    }
    
    private void startInactivityTimer() {
        if (inactivityTimer != null) {
            inactivityTimer.stop();
        }

        inactivityTimer = new Timer(INACTIVITY_TIMEOUT, e -> {
            if (active) {
                active = false;
                connectionManager.toggleStatus();
            }
        });

        inactivityTimer.setRepeats(false);
        inactivityTimer.start();
    }
    
    public void activityDetected() {
        if (!active) {
            active = true;
            connectionManager.toggleStatus();
        }
        startInactivityTimer();
    }
    
    public void shutdown() {
        if (inactivityTimer != null) {
            inactivityTimer.stop();
        }
    }
}