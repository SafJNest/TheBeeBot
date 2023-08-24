package com.safjnest.Utilities.Guild;

/**
 * Class that stores all the settings for a guild.
 * <ul>
 * <li>ID</li>
 * <li>Name(only for vocal)</li>
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
     * Name of the room
     */
    private String name;
    
    /**
     * If the room has the exp system
     * @see com.safjnest.Utilities.EXPSystem.ExpSystem EXPSystem  
     */
    private boolean expSystem;

    /**
     * Value of the exp system
     */
    private String expValue;

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
    public Room(Long id, String name, boolean expSystem, String expValue, boolean command) {
        this.id = id;
        this.name = name;
        this.expSystem = expSystem;
        this.expValue = expValue;
        this.command = command;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public boolean getExpSystem() {
        return expSystem;
    }

    public boolean getCommand() {
        return command;
    }

    public String getExpValue() {
        return expValue;
    }

    public void setExpSystem(boolean exp) {
        this.expSystem = exp;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setExpValue(String value) {
        this.expValue = value;
    }

}
