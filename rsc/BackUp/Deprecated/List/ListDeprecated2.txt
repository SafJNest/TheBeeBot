package com.safjnest.Commands.Audio;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import com.safjnest.Commands.Dangerous.RandomMove;
import com.safjnest.Commands.Dangerous.VandalizeServer;
import com.safjnest.Commands.LOL.Champ;
import com.safjnest.Commands.LOL.FreeChamp;
import com.safjnest.Commands.LOL.LastMatches;
import com.safjnest.Commands.LOL.RankMatch;
import com.safjnest.Commands.LOL.SetSummoner;
import com.safjnest.Commands.LOL.Summoner;
import com.safjnest.Commands.ManageGuild.ChannelInfo;
import com.safjnest.Commands.ManageGuild.Clear;
import com.safjnest.Commands.ManageGuild.EmojiInfo;
import com.safjnest.Commands.ManageGuild.Leaderboard;
import com.safjnest.Commands.ManageGuild.ListRoom;
import com.safjnest.Commands.ManageGuild.ServerInfo;
import com.safjnest.Commands.ManageGuild.SetRoom;
import com.safjnest.Commands.ManageGuild.SetWelcome;
import com.safjnest.Commands.ManageGuild.UserInfo;
import com.safjnest.Commands.ManageGuild.UserStats;
import com.safjnest.Commands.ManageMembers.Ban;
import com.safjnest.Commands.ManageMembers.Image;
import com.safjnest.Commands.ManageMembers.Kick;
import com.safjnest.Commands.ManageMembers.ModifyNickname;
import com.safjnest.Commands.ManageMembers.Move;
import com.safjnest.Commands.ManageMembers.Mute;
import com.safjnest.Commands.ManageMembers.Permissions;
import com.safjnest.Commands.ManageMembers.UnMute;
import com.safjnest.Commands.ManageMembers.Unban;
import com.safjnest.Commands.Math.Calculator;
import com.safjnest.Commands.Math.Dice;
import com.safjnest.Commands.Math.Prime;
import com.safjnest.Commands.Misc.Anonym;
import com.safjnest.Commands.Misc.InviteBot;
import com.safjnest.Commands.Misc.Msg;
import com.safjnest.Utilities.CommandsHandler;
import com.safjnest.Utilities.SQL;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import net.dv8tion.jda.api.utils.messages.MessageCreateBuilder;

/**
 * @author <a href="https://github.com/NeutronSun">NeutronSun</a>
 * 
 * @since 1.1
 */
public class List extends Command{
    private SQL sql;

    public List(SQL sql){
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
        MessageCreateBuilder message = new MessageCreateBuilder();
        message.setContent(getListLexo(event.getJDA(), sql, event.getGuild().getId()));
        message.addActionRow(keria1, keria2, keria3, keria4);
        event.reply(message.build());
    }
    /*
     * 0 id
     * 1 name
     * 2 guild
     * 3 user id
     * 4 extension
     */
    public static String getListLexo(JDA jda, SQL sql, String serverId){
        String query = "SELECT id, name, guild_id, user_id, extension FROM sound WHERE guild_id = '" + serverId + "' ORDER BY name ASC;";
        return getSoundsName(jda, getMap(sql.getAllRows(query, 5)));
    }

    public static String getListUser(JDA jda, SQL sql, String userId){
        String query = "SELECT id, name, guild_id, user_id, extension FROM sound WHERE user_id = '" + userId + "' ORDER BY name ASC;";
        return getSoundsName(jda, getMap(sql.getAllRows(query, 5)));
    }
    
    public static String getListMostPlayed(JDA jda, SQL sql, String serverId){
        String query = "SELECT sound.id, sound.name, sound.guild_id, sound.user_id, SUM(times) FROM sound join play on sound.id=play.id_sound WHERE sound.guild_id = '" + serverId + "'GROUP BY sound.id ORDER BY SUM(times)DESC;";
        return getSoundsName(jda, getMapTimes(sql.getAllRows(query, 5)));
    }  
    
    public static String getListId(JDA jda, SQL sql, String serverId){
        String query = "SELECT id, name, guild_id, user_id, extension FROM sound where guild_id = '" + serverId + "'ORDER BY id;";
        return getSoundsName(jda, getMapId(sql.getAllRows(query, 5)));
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

    public static Object of(Anonym anonym, ChannelInfo channelInfo, Clear clear, Msg msg, ServerInfo serverInfo,
            UserInfo userInfo, EmojiInfo emojiInfo, InviteBot inviteBot, UserStats userStats, Leaderboard leaderboard,
            Ban ban, Unban unban, Kick kick, Move move, Mute mute, UnMute unMute, Image image, Permissions permissions,
            ModifyNickname modifyNickname, ListRoom listRoom, SetWelcome setWelcome, SetRoom setRoom, Prime prime,
            Calculator calculator, Dice dice, VandalizeServer vandalizeServer, RandomMove randomMove, Champ champ,
            Summoner summoner, FreeChamp freeChamp, RankMatch rankMatch, SetSummoner setSummoner,
            LastMatches lastMatches) {
        return null;
    }
}
