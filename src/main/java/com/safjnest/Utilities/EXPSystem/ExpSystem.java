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


    public HashMap<String, UserTime> getUsers() {
        return users;
    }

    /**
     * This method is used to check if the user can receive experience.
     * <p>If the user is not cached, it will be added to the cache and will return true.
     * @param userId
     * @param guildId
     * @return
     */
    public synchronized int receiveMessage(String userId, String guildId, double modifier) {
        if(!users.containsKey(userId+"-"+guildId)){
            users.put(userId+"-"+guildId, new UserTime());
            return addExp(userId, guildId, modifier);
        }
        UserTime user =  users.get(userId+"-"+guildId);
        if (user.canReceiveExperience()) {
           return addExp(userId, guildId, modifier);
           
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


    /**
     * This method is used to calculate the total experience that the user needs to
     * get to be level {@code lvl} from zero.
     * <p>
     * The experience is calculated using the following formula:
     * {@code (5/6) * (lvl) * (2 * (lvl) * (lvl) + 27 * (lvl) + 91)}.
     * <table border="2">
     * <tr>
     * <td>LVL EXP</td>
     * </tr>
     * <tr>
     * <td>1 100</td>
     * </tr>
     * <tr>
     * <td>2 255</td>
     * </tr>
     * <tr>
     * <td>3 475</td>
     * </table>
     * @param lvl
     * @return
     */
    public static int totalExpToLvlUp(int lvl){
        return (int) ((5.0/6.0) * (lvl) * (2 * (lvl) * (lvl) + 27 * (lvl) + 91));
    }


    /**
     * This method is used to calculate the experience that the user needs to get level up.
     * <p>
     * So if the user is level 1 with 175 exp and to get level 2 needs a total of 255 experience, this method will return 80.
     * @param lvl
     * @param exp
     * @return 
     */
    public static int expToLvlUp(int lvl, int exp){
        if(lvl == 1 && exp < 100)
            lvl = 0;
        return (exp - totalExpToLvlUp(lvl));
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
    public int addExp(String userId, String guildId, double modifer){
        int exp, lvl, msg;
        String query = "select exp, level, messages from exp_table where user_id ='"+userId+"' and guild_id = '"+guildId+"';";
        ArrayList<String> arr = DatabaseHandler.getSql().getSpecifiedRow(query, 0);
        if(arr == null){
            query = "INSERT INTO exp_table (user_id, guild_id, exp, level, messages) VALUES ('"+userId+"','"+guildId+"',"+0+","+1+","+0+");";
            if(!DatabaseHandler.getSql().runQuery(query))
                return -1;
            exp = Math.round(Float.parseFloat(String.valueOf(Double.parseDouble(String.valueOf(calculateExp()))*modifer)));
            lvl = 1;
            msg = 1;
        }else{

            int newExp = Math.round(Float.parseFloat(String.valueOf(Double.parseDouble(String.valueOf(calculateExp()))*modifer)));
            exp = Integer.valueOf(arr.get(0)) + newExp;
            lvl = Integer.valueOf(arr.get(1));
            msg = Integer.valueOf(arr.get(2)) + 1;
        }
        int expNeeded = totalExpToLvlUp(lvl + 1) - exp;
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