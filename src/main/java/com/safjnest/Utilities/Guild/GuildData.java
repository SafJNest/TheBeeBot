package com.safjnest.Utilities.Guild;

import java.util.ArrayList;
import java.util.HashMap;

import com.safjnest.Utilities.DatabaseHandler;

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

    public GuildData(Long id, String prefix, boolean expSystem) {
        this.id = id;
        this.prefix = prefix;
        this.expSystem = expSystem;
        loadRooms();
    }

    public Long getId() {
        return id;
    }

    public synchronized String getPrefix() {
        return prefix;
    }

    public boolean getExpSystem() {
        return expSystem;
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
        String query = "SELECT room_id, room_name, has_exp, exp_value, has_command_stats FROM rooms_settings WHERE guild_id ='" + id + "';";
        ArrayList<ArrayList<String>> result = DatabaseHandler.getSql().getAllRows(query, 5);
        for(ArrayList<String> row: result){;
            rooms.put(
                Long.parseLong(row.get(0)),
                new Room(
                    Long.parseLong(row.get(0)), 
                    row.get(1), 
                    row.get(2).equals("1") ? true : false, 
                    row.get(3),
                    row.get(4).equals("1") ? true : false
                    )
            );
        }
    }


    public Room getRoom(Long id){
        return rooms.get(id);
    }

    public Boolean getExpSystemRoom(Long id){
        try {
            return rooms.get(id).getExpSystem();
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

    public String getExpValueRoom(Long id){
        try {
            return rooms.get(id).getExpValue();
        } catch (Exception e) {
            return "1";
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

    public synchronized void setExpValueRoom(Long id, String value){
        rooms.get(id).setExpValue(value);
    }

    
}
