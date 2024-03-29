package com.safjnest.Utilities.Guild;

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
public class Room {
   
    /**
     * ID of the room
     */
    private Long id;
        
    /**
     * If the room has the exp system
     * @see com.safjnest.Utilities.ExperienceSystem EXPSystem  
     */
    private boolean expSystem;

    /**
     * Value of the exp system
     */
    private double expValue;

    /**
     * If the room has the command system
     */
    private boolean command;
    
    /**
     * Defaul Constructor
     * @param id
     * @param name
     * @param expSystem
     * @param expValue
     * @param command
     */
    public Room(Long id, boolean expSystem, double expValue, boolean command) {
        this.id = id;
        this.expSystem = expSystem;
        this.expValue = expValue;
        this.command = command;
    }

    public Long getId() {
        return id;
    }

    public boolean isExpSystemEnabled() {
        return expSystem;
    }

    public boolean getCommand() {
        return command;
    }

    public double getExpValue() {
        return expValue;
    }

    public void setExpSystem(boolean exp) {
        this.expSystem = exp;
    }

    public void setExpValue(double value) {
        this.expValue = value;
    }

}
