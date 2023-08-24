package com.safjnest.Utilities.EventHandlers;

import java.util.ArrayList;

import com.safjnest.Utilities.DatabaseHandler;
import com.safjnest.Utilities.EXPSystem.ExpSystem;
import com.safjnest.Utilities.Guild.GuildSettings;

import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.UserSnowflake;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.events.role.RoleDeleteEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

/**
 * This class handles a few events that are only used by Beebot. These events:
 * <ul>
 * <li>On Message Received (for gaining exp)</li>
 * </ul>
 * 
 * @author <a href="https://github.com/NeutronSun">NeutronSun</a>
 * 
 * @since 2.1
 */
public class EventHandlerBeebot extends ListenerAdapter {
    /**
     * The ExpSystem object that handles the exp system.
     */
    private ExpSystem farm;
    
    private GuildSettings settings;

    /**
     * Constructor for the TheListenerBeebot class.
     */
    public EventHandlerBeebot(GuildSettings settings, ExpSystem farm) {
        this.farm = farm;
        this.settings = settings;
    }

    @Override
    public void onMessageReceived(MessageReceivedEvent e) {
        if (e.getAuthor().isBot())
            return;
            
        
        if (!settings.getServer(e.getGuild().getId()).getExpSystem())
            return;	

        
        if(!settings.getServer(e.getGuild().getId()).getExpSystemRoom(e.getChannel().getIdLong()))
            return;

        double modifier = Double.parseDouble(settings.getServer(e.getGuild().getId()).getExpValueRoom(e.getChannel().getIdLong()));
        int lvl = farm.receiveMessage(e.getAuthor().getId(), e.getGuild().getId(), modifier);
        if (lvl != -1) {
            User newGuy = e.getAuthor();
            String query = "SELECT role_id, message_text FROM rewards_table WHERE guild_id = '" + e.getGuild().getId() + "' AND level = '" + lvl + "';";
            ArrayList<String> arr = DatabaseHandler.getSql().getSpecifiedRow(query, 0);
            if(arr != null){
                String message = arr.get(1);
                Role role = e.getGuild().getRoleById(arr.get(0));
                message = message.replace("#user", newGuy.getAsMention());
                message = message.replace("#level", String.valueOf(lvl));
                message = message.replace("#role", role.getName());
                e.getGuild().addRoleToMember(UserSnowflake.fromId(newGuy.getId()), role).queue();
                e.getChannel().asTextChannel().sendMessage(message).queue();
                return;
            }
            query = "SELECT message_text FROM levelup_message WHERE guild_id = '" + e.getGuild().getId() + "';";
            arr = DatabaseHandler.getSql().getSpecifiedRow(query, 0);
            if (arr != null){
                String message = arr.get(0);
                message = message.replace("#user", newGuy.getAsMention());
                message = message.replace("#level", String.valueOf(lvl));
                e.getChannel().asTextChannel().sendMessage(message).queue();
                return;
            }
            e.getChannel().asTextChannel().sendMessage("Congratulations, you are now level: " + lvl).queue();
        }
    }

    @Override
    public void onRoleDelete(RoleDeleteEvent event){
        String query = "DELETE FROM rewards_table WHERE role_id = '" + event.getRole().getId() + "';";
        DatabaseHandler.getSql().runQuery(query);
    }


}