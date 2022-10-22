package com.safjnest.Commands.Audio;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import com.safjnest.Utilities.CommandsHandler;
import com.safjnest.Utilities.PostgreSQL;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import net.dv8tion.jda.api.utils.messages.MessageCreateBuilder;

/**
 * @author <a href="https://github.com/NeutronSun">NeutronSun</a>
 * 
 * @since 1.1
 */
public class List extends Command{
    private PostgreSQL sql;

    public List(PostgreSQL sql){
        this.name = this.getClass().getSimpleName();;
        this.aliases = new CommandsHandler().getArray(this.name, "alias");
        this.help = new CommandsHandler().getString(this.name, "help");
        this.cooldown = new CommandsHandler().getCooldown(this.name);
        this.category = new Category(new CommandsHandler().getString(this.name, "category"));
        this.arguments = new CommandsHandler().getString(this.name, "arguments");
        this.sql = sql;
    }

	@Override
	protected void execute(CommandEvent event) {
        Button keria1 = Button.primary("lexo", "Server");
        Button keria2 = Button.primary("idOrder", "ID Order");
        Button keria3 = Button.primary("mostPlayed", "Most played");
        Button keria4 = Button.primary("byUser", "Yours");
        Button keria5 = Button.primary("global", "Global");
        MessageCreateBuilder message = new MessageCreateBuilder();
        message.setContent(getListLexo(event.getJDA(), sql, event.getGuild().getId()));
        message.addActionRow(keria1, keria2, keria3, keria4, keria5);
        event.reply(message.build());
    }

    public static String getListLexo(JDA jda, PostgreSQL sql, String serverId){
        String query = "SELECT id, name, guild_id, user_id, extension FROM sound WHERE guild_id = '" + serverId + "';";
        return getSoundsName(jda, getMap(sql.getTuple(query, 5)));
    }

    public static String getListUser(JDA jda, PostgreSQL sql, String userId){
        String query = "SELECT id, name, guild_id, user_id, extension FROM sound WHERE user_id = '" + userId + "';";
        return getSoundsName(jda, getMap(sql.getTuple(query, 5)));
    }
    
    public static String getListGlobal(JDA jda, PostgreSQL sql){
        String query = "SELECT id, name, guild_id, user_id, extension FROM sound;";
        return getSoundsName(jda, getMap(sql.getTuple(query, 5)));
    }

    public static String getListMostPlayed(JDA jda, PostgreSQL sql){
        String query = "SELECT sound.id, sound.name, sound.guild_id, sound.user_id, SUM(times) FROM sound join play on sound.id=play.id_sound GROUP BY sound.id ORDER BY SUM(times)DESC;";
        return getSoundsName(jda, getMapTimes(sql.getTuple(query, 5)));
    }  
    
    public static String getListId(JDA jda, PostgreSQL sql){
        String query = "SELECT id, name, guild_id, user_id, extension FROM sound ORDER BY id;";
        return getSoundsName(jda, getMapId(sql.getTuple(query, 5)));
    } 

    private static String getSoundsName(JDA jda, Map<String, ArrayList<String>> sortedMap){
        String soundNames = "";
        int cont = 0;
        for(String serverId : sortedMap.keySet()) {
            String serverName = (jda.getGuildById(serverId) != null) ? jda.getGuildById(serverId).getName() : "Im not in the server"; 
            soundNames += "**" + serverName + "**" + ":\n";
            for(String soundName : sortedMap.get(serverId)){
                soundNames += soundName + " - ";
                cont++;
            }
            soundNames = soundNames.substring(0, soundNames.length() - 3) + "\n";
        }
        soundNames += "\nTotal sounds: " + cont;
        return soundNames;
    }


    private static Map<String, ArrayList<String>> getMap(ArrayList<ArrayList<String>> arr){
        HashMap<String, ArrayList<String>> alpha = new HashMap<>();
        for(int i = 0; i < arr.size(); i++){
            if(!alpha.containsKey(arr.get(i).get(2)))
                alpha.put(arr.get(i).get(2), new ArrayList<>());
            alpha.get(arr.get(i).get(2)).add(arr.get(i).get(1));
        }
        Map<String, ArrayList<String>> sortedMap = new TreeMap<>(alpha);
        sortedMap.putAll(alpha);
        return sortedMap;
    }

    private static Map<String, ArrayList<String>> getMapTimes(ArrayList<ArrayList<String>> arr){
        HashMap<String, ArrayList<String>> alpha = new HashMap<>();
        for(int i = 0; i < arr.size(); i++){
            if(!alpha.containsKey(arr.get(i).get(2)))
                alpha.put(arr.get(i).get(2), new ArrayList<>());
            alpha.get(arr.get(i).get(2)).add(arr.get(i).get(1)+" ("+arr.get(i).get(4)+")");
        }
        Map<String, ArrayList<String>> sortedMap = new TreeMap<>(alpha);
        return sortedMap;
    }

    private static Map<String, ArrayList<String>> getMapId(ArrayList<ArrayList<String>> arr){
        HashMap<String, ArrayList<String>> alpha = new HashMap<>();
        for(int i = 0; i < arr.size(); i++){
            if(!alpha.containsKey(arr.get(i).get(2)))
                alpha.put(arr.get(i).get(2), new ArrayList<>());
            alpha.get(arr.get(i).get(2)).add(arr.get(i).get(1)+" ("+arr.get(i).get(0)+")");
        }
        Map<String, ArrayList<String>> sortedMap = new TreeMap<>(alpha);
        return sortedMap;
    }
}
