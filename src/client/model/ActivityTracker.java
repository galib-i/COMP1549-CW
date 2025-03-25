package client.model;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowEvent;
import java.awt.event.WindowFocusListener;

import javax.swing.JFrame;
import javax.swing.Timer;

import common.util.ConfigLoader;

public class ActivityTracker {
    private final int TIMEOUT;
    private final ConnectionManager connectionManager;
    private Timer timer;
    private boolean active = true;
    
    public ActivityTracker(ConnectionManager connectionManager, JFrame frame) {
        this.connectionManager = connectionManager;

        ConfigLoader config = new ConfigLoader();
        this.TIMEOUT = config.getInt("inactivity.timeout.ms");

        frame.addWindowFocusListener(new WindowFocusListener() {
            @Override
            public void windowGainedFocus(WindowEvent e) { 
                activityDetected(); 
            }

            public void windowLostFocus(WindowEvent e) {} // do nothing, required by WindowFocusListener
        });
        
        frame.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) { 
                activityDetected(); 
            }
        });
        
        startTimer();
    }
    
    private void startTimer() {
        if (timer != null) {
            timer.stop();
        }

        timer = new Timer(TIMEOUT, e -> {
            if (active) {
                active = false;
                connectionManager.toggleStatus();
            }
        });

        timer.setRepeats(false);
        timer.start();
    }
    
    public void activityDetected() {
        if (!active) {
            active = true;
            connectionManager.toggleStatus();
        }
        startTimer();
    }
    
    public void shutdown() {
        if (timer != null) {
            timer.stop();
        }
    }
}