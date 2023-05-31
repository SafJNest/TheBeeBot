package com.safjnest.Utilities.EXPSystem;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

import com.safjnest.Utilities.DatabaseHandler;


/**
 * This class is used to manage the experience system of the bot.
 * <p>
 * The experience system is used to give experience to the users of the bot when they send a message in a server every one minute.
 */
public class ExpSystem {

    /**
     * This HashMap is used to store the users and the time they sent a message.
     * The key is {@code userId-guildId} and the value is {@link com.safjnest.Utilities.EXPSystem.UserTime UserTime.
     */
    private HashMap<String, UserTime> users;
    

    /**
     * Constructor for the ExpSystem class.
     */
    public ExpSystem() {
        users = new HashMap<>();
    }

    /**
     * This method is used to check if the user can receive experience.
     * <p>If the user is not cached, it will be added to the cache and will return true.
     * @param userId
     * @param guildId
     * @return
     */
    public synchronized int receiveMessage(String userId, String guildId) {
        if(!users.containsKey(userId+"-"+guildId)){
            users.put(userId+"-"+guildId, new UserTime());
            return addExp(userId, guildId);
        }
        UserTime user =  users.get(userId+"-"+guildId);
        if (user.canReceiveExperience()) {
           return addExp(userId, guildId);
           
        }else{
           return -1;
        }
        
    }

    /**
     * This method is used to calculate the experience that the user will receive.
     * <p> The experience is calculated randomly between 15 and 25.
     * @return
     */
    public int calculateExp(){
        return new Random().nextInt((25 - 15) + 1) + 15;
    }

    public static int totalExpToLvlUp(int lvl){
        return (int) ((5 * (Math.pow(lvl, 2)) + (50 * lvl) + 100));
    }

    public static int expToLvlUp(int lvl, int exp){
        return (totalExpToLvlUp(lvl) - (int) ((5.0/6.0) * (lvl+1) * (2 * (lvl+1) * (lvl+1) + 27 * (lvl+1) + 91) - exp));
    }  

    /**
     * This method is used to add the experience to the user.
     * <p> If the user is not in the database, it will be added. If the user has enough experience to level up, it will be leveled up and the method will
     * return the new level. If the user is not leveled up, it will return -1.
     * @param userId
     * @param guildId
     * @return
     * int
     */
    public int addExp(String userId, String guildId){
        int exp, lvl, msg;
        String query = "select exp, level, messages from exp_table where user_id ='"+userId+"' and guild_id = '"+guildId+"';";
        ArrayList<String> arr = DatabaseHandler.getSql().getSpecifiedRow(query, 0);
        if(arr == null){
            query = "INSERT INTO exp_table (user_id, guild_id, exp, level, messages) VALUES ('"+userId+"','"+guildId+"',"+0+","+1+","+0+");";
            if(!DatabaseHandler.getSql().runQuery(query))
                return -1;
            exp = calculateExp();
            lvl = 1;
            msg = 1;
        }else{
            exp = Integer.valueOf(arr.get(0)) + calculateExp();
            lvl = Integer.valueOf(arr.get(1));
            msg = Integer.valueOf(arr.get(2)) + 1;
        }
        int expNeeded = (int) ((5.0/6.0) * (lvl+1) * (2 * (lvl+1) * (lvl+1) + 27 * (lvl+1) + 91) - exp);
        if(expNeeded <= 0){
            query = "update exp_table set exp = " + exp + ", level = " + (lvl+1) + ", messages = "+msg+" where user_id ='"+userId+"' and guild_id = '"+guildId+"';";
            DatabaseHandler.getSql().runQuery(query);
            return lvl+1;
        }
        
        query = "update exp_table set exp = " + exp + ", messages = "+msg+" where user_id ='"+userId+"' and guild_id = '"+guildId+"';";
        DatabaseHandler.getSql().runQuery(query);
        return -1;
        

    }   
}