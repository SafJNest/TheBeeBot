package com.safjnest.Utilities.Guild;

import com.safjnest.Utilities.SQL.DatabaseHandler;

/**
 * Class that stores all the settings for a guild.
 * <ul>
 * <li>ID</li>
 * <li>expSystem</li>
 * <li>expValue</li>
 * <li>command</li>
 * </ul>
 * @author <a href="https://github.com/Leon412">Leon412</a>
 */
public class ChannelData {
    

    private int ID;


    /**
     * ID of the room
     */
    private final long CHANNEL_ID;
        
    /**
     * If the room has the exp system
     * @see com.safjnest.Utilities.ExperienceSystem EXPSystem  
     */
    private boolean expEnabled;

    /**
     * Value of the exp system
     */
    private double expModifier;

    /**
     * If the room has the command system
     */
    private boolean statisticsEnabled;

    private GuildData guildData;
    
    /**
     * Defaul Constructor
     * @param id
     * @param name
     * @param expSystem
     * @param expValue
     * @param command
     */
    public ChannelData(int ID, long CHANNEL_ID, boolean expSystem, double expValue, boolean command, GuildData guildData) {
        this.ID = ID;
        this.CHANNEL_ID = CHANNEL_ID;
        this.expEnabled = expSystem;
        this.expModifier = expValue;
        this.statisticsEnabled = command;
        this.guildData = guildData;
    }

    public ChannelData(long CHANNEL_ID, GuildData guildData) {
        this.ID = 0;
        this.CHANNEL_ID = CHANNEL_ID;
        this.expEnabled = true;
        this.expModifier = 1;
        this.statisticsEnabled = true;
        this.guildData = guildData;
    }

    public int getId() {
        return this.ID;
    }

    public long getRoomId() {
        return this.CHANNEL_ID;
    }

    public boolean isExpSystemEnabled() {
        return expEnabled;
    }

    public boolean getCommand() {
        return statisticsEnabled;
    }


    public double getExpValue() {
        return expModifier;
    }

    public boolean setExpEnabled(boolean exp) {
        handleEmptyID();

        boolean result = DatabaseHandler.setChannelExpEnabled(this.ID, exp);
        if (result) {
            this.expEnabled = exp;
        }
        return result;
    }

    public boolean setExpModifier(double value) {
        handleEmptyID();

        boolean result = DatabaseHandler.setChannelExpModifier(this.ID, value);
        if (result) {
            this.expModifier = value;
        }
        return result;
    }

    public boolean terminator5LaRivolta() {
        if (this.ID == 0) {
            return true;
        }
        return DatabaseHandler.deleteChannelData(this.ID);
    }

    private void handleEmptyID() {
        if (this.ID == 0) {
            System.out.println("[CACHE] Pushing local ChannelData into Database=> " + CHANNEL_ID);
            this.ID = DatabaseHandler.insertChannelData(guildData.getId(), this.CHANNEL_ID);
        }
    }

    @Override
    public String toString() {
        return "ChannelData [ID=" + ID + ", CHANNEL_ID=" + CHANNEL_ID + ", expEnabled=" + expEnabled + ", expModifier="
                + expModifier + ", statisticsEnabled=" + statisticsEnabled + "]";
    }

}
