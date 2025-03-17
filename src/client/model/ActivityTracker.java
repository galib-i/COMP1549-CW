package client.model;

import java.awt.event.WindowEvent;
import java.awt.event.WindowFocusListener;

import javax.swing.JFrame;
import javax.swing.Timer;

public class ActivityTracker {
    private static final int INACTIVITY_TIMEOUT = 30000; // 30 seconds
    private final ConnectionManager connectionManager;
    private Timer inactivityTimer;
    private boolean isActive = true;
    
    public ActivityTracker(ConnectionManager connectionManager, JFrame frame) {
        this.connectionManager = connectionManager;
        windowFocusListener(frame);
        startInactivityTimer();
    }
    
    private void windowFocusListener(JFrame frame) {
        frame.addWindowFocusListener(new WindowFocusListener() {
            @Override
            public void windowGainedFocus(WindowEvent e) {
                activityDetected();
            }
            
            @Override
            public void windowLostFocus(WindowEvent e) {}
        });
    }
    
    private void startInactivityTimer() {
        if (inactivityTimer != null) {
            inactivityTimer.stop();
        }

        inactivityTimer = new Timer(INACTIVITY_TIMEOUT, e -> {
            if (isActive) {
                isActive = false;
                connectionManager.sendStatusUpdate("INACTIVE");
            }
        });

        inactivityTimer.setRepeats(false);
        inactivityTimer.start();
    }
    
    public void activityDetected() {
        boolean wasInactive = !isActive;
        isActive = true;
        startInactivityTimer(); // Reset timer

        if (wasInactive) {
            connectionManager.sendStatusUpdate("ACTIVE");
        }
    }
    
    public void shutdown() {
        if (inactivityTimer != null) {
            inactivityTimer.stop();
        }
    }
}