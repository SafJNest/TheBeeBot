package com.safjnest.Utilities.EXPSystem;

import java.time.LocalDateTime;

public class UserTime {
    private LocalDateTime lastMessageTime;

    public UserTime() {
        lastMessageTime = LocalDateTime.now(); // Inizialmente impostato un minuto fa
    }

    public synchronized boolean canReceiveExperience() {
        LocalDateTime now = LocalDateTime.now();
        if (now.isAfter(lastMessageTime.plusMinutes(1))) {
            lastMessageTime = now;
            return true;
        }
        return false;
    }
}