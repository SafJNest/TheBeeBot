package com.safjnest.Utilities.Guild;


import java.util.HashMap;


import com.safjnest.Utilities.SQL.DatabaseHandler;
import com.safjnest.Utilities.SQL.QueryResult;
import com.safjnest.Utilities.SQL.ResultRow;


/**
 * Class that stores all the settings for a guild.
 * <ul>
 * <li>Prefix</li>
 * <li>ID</li>
 * </ul>
 * @author <a href="https://github.com/Leon412">Leon412</a>
 */
public class GuildData {
    /**Server ID */
    private Long id;
    /**Prefix Server */
    private String prefix;
    /**Exp System */
    private boolean expSystem;
    /**
     * default constructor
     * @param id
     * @param prefix
     */

    private HashMap<Long, Room> rooms;

    private int threshold;

    private String blackChannelId;

    private boolean blacklist_enabled;

    public GuildData(Long id, String prefix, boolean expSystem, int threshold, String channel, boolean blacklist_enabled) {
        this.id = id;
        this.prefix = prefix;
        this.expSystem = expSystem;
        this.threshold = threshold;
        this.blackChannelId = channel;
        this.blacklist_enabled = blacklist_enabled;
        loadRooms();
    }

    public Long getId() {
        return id;
    }

    public String getPrefix() {
        return prefix;
    }

    public boolean isExpSystemEnabled() {
        return expSystem;
    }

    public int getThreshold() {
        return threshold;
    }

    public String getBlackChannelId() {
        return blackChannelId;
    }

    public synchronized void setBlackChannel(String blackChannel) {
        this.blackChannelId = blackChannel;
    }

    public synchronized void setThreshold(int threshold) {
        this.threshold = threshold;
    }

    public synchronized void setPrefix(String prefix) {
        this.prefix = prefix;
    }

    public synchronized void setExpSystem(boolean expSystem) {
        this.expSystem = expSystem;
    }

    public String toString(){
        return "ID: " + id + "| Prefix: " + prefix + " |ExpSystem: " + expSystem;
    }

    /**
     * Load all the rooms of the guild
     */
    public void loadRooms(){
        rooms = new HashMap<>();
        QueryResult result = DatabaseHandler.getRoomsData(String.valueOf(id));
        for(ResultRow row: result){;
            rooms.put(
                row.getAsLong("room_id"),
                new Room(
                    row.getAsLong("room_id"), 
                    row.get("room_name"), 
                    row.getAsBoolean("has_exp"), 
                    row.getAsDouble("exp_value"),
                    row.getAsBoolean("has_command_stats")
                    )
            );
        }
    }


    public Room getRoom(Long id){
        return rooms.get(id);
    }

    public Boolean getExpSystemRoom(Long id){
        try {
            return rooms.get(id).isExpSystemEnabled();
        } catch (Exception e) {
            return true;
        }
    }

    public Boolean getCommandStatsRoom(Long id){
        try {
            return rooms.get(id).getCommand();
        } catch (Exception e) {
            return true;
        }
    }

    public double getExpValueRoom(Long id){
        try {
            return rooms.get(id).getExpValue();
        } catch (Exception e) {
            return 1;
        }
    }

    public synchronized void addRoom(Room room){
        rooms.put(room.getId(), room);
    }

    public synchronized void setExpSystemRoom(Long id, boolean exp){
        rooms.get(id).setExpSystem(exp);
    }

    public synchronized void setNameRoom(Long id, String name){
        rooms.get(id).setName(name);
    }

    public synchronized void setExpValueRoom(Long id, double value){
        rooms.get(id).setExpValue(value);
    }

    public boolean blacklistEnabled() {
        return blacklist_enabled;
    }
    
    public void setBlacklistEnabled(boolean blacklist_enabled) {
        this.blacklist_enabled = blacklist_enabled;
    }
    
}
