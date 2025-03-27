package client.model;

import javax.swing.Timer;

import common.util.ConfigLoader;

/**
 * Tracks user activity and toggles the user's status
 * when the user is inactive after a set timeout
 */
public class ActivityModel {
    private final int TIMEOUT;
    private final ConnectionManager connectionManager;
    private Timer timer;
    private boolean active = true;
    
    public ActivityModel(ConnectionManager connectionManager) {
        this.connectionManager = connectionManager;
        ConfigLoader config = new ConfigLoader();
        this.TIMEOUT = config.getInt("inactivity.timeout.ms");
    }
    
    public void startTimer() {
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
    
    public void trackActivity() {
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
    
    public boolean isActive() {
        return active;
    }
}