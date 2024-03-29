package com.safjnest.Utilities.Guild.Alert;

public enum AlertType {
    BOOST("Boost Message", 0),
    LEAVE("Leave Message", 0),
    LEVEL_UP("Level Up Message", 0),
    WELCOME("Welcome Message", 0),
    REWARD("Reward", 0),;

    private final String description;
    private int level;

    AlertType(String description) {
        this.description = description;
        this.level = 0;
    }

    AlertType(String description, int level) {
        this.description = description;
        this.level = level;
    }

    public String getDescription() {
        return description;
    }

    public int getLevel() {
        return level;
    }
}
