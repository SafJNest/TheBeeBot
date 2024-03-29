package com.safjnest.Utilities.Guild;



import com.safjnest.Utilities.SQL.DatabaseHandler;

public class BlacklistData {
    
    /**
     * Threshold for blacklist
     */
    private int threshold;


    /**
     * Blacklist Channel
     */
    private String blackChannelId;


    /**
     * Flag to toggle the blacklist
     */
    private boolean blacklist_enabled;

    private GuildData guildData;


    public BlacklistData(int threshold, String blackChannelId, boolean blacklist_enabled, GuildData guildData) {
        this.threshold = threshold;
        this.blackChannelId = blackChannelId;
        this.blacklist_enabled = blacklist_enabled;
        this.guildData = guildData;
    }

    public int getThreshold() {
        return threshold;
    }

    public String getBlackChannelId() {
        return blackChannelId;
    }

    public boolean isBlacklistEnabled() {
        return blacklist_enabled;
    }

    public synchronized boolean setThreshold(int threshold) {
        boolean result = DatabaseHandler.setBlacklistThreshold(String.valueOf(threshold), String.valueOf(guildData.getId()));
        if (result) {
            this.threshold = threshold;
        }
        return result;
    }

    public synchronized boolean setBlackChannelId(String blackChannelId) {
        boolean result = DatabaseHandler.setBlacklistChannel(blackChannelId, String.valueOf(guildData.getId()));
        if (result) {
            this.blackChannelId = blackChannelId;
        }
        return result;
    }

    public synchronized boolean setBlacklistEnabled(boolean toggle) {
        boolean result = DatabaseHandler.toggleBlacklist(String.valueOf(guildData.getId()), toggle);
        if (result) {
            this.blacklist_enabled = toggle;
        }
        return result;
    }

    public boolean update() {
        return DatabaseHandler.enableBlacklist(String.valueOf(guildData.getId()), String.valueOf(threshold), blackChannelId);
    }

    @Override
    public String toString() {
        return "Threshold: " + threshold + "| Channel: " + blackChannelId + "| Enabled: " + blacklist_enabled;
    }

}
