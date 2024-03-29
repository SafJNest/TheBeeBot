package com.safjnest.Utilities.EventHandlers;

import com.safjnest.Utilities.ExperienceSystem;
import com.safjnest.Utilities.Guild.GuildData;
import com.safjnest.Utilities.Guild.GuildSettings;
import com.safjnest.Utilities.Guild.UserData;
import com.safjnest.Utilities.Guild.Alert.AlertData;
import com.safjnest.Utilities.Guild.Alert.AlertType;
import com.safjnest.Utilities.Guild.Alert.RewardData;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.UserSnowflake;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
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

        private GuildSettings settings;

    /**
     * Constructor for the TheListenerBeebot class.
     */
    public EventHandlerBeebot(GuildSettings settings) {
        this.settings = settings;
    }

    @Override
    public void onMessageReceived(MessageReceivedEvent e) {
        if (e.getAuthor().isBot())
            return;
        
        GuildData guildData = settings.getServer(e.getGuild().getId());
        Guild guild = e.getGuild();
        
        TextChannel channel = e.getChannel().asTextChannel();
        
        User newGuy = e.getAuthor();

        if (!guildData.isExpSystemEnabled())
            return;	

        if(!guildData.getExpSystemRoom(e.getChannel().getIdLong()))
            return;

        double modifier = guildData.getExpValueRoom(channel.getIdLong());
        
        UserData user = guildData.getUserData(newGuy.getIdLong());
        if (!user.canReceiveExperience()) {
            return;
        }

        int exp = user.getExperience();
        int currentLevel = user.getLevel();
        exp = ExperienceSystem.calculateExp(exp, modifier);
        int lvl = ExperienceSystem.isLevelUp(exp, currentLevel);

        if(lvl == ExperienceSystem.NOT_LEVELED_UP) {
            user.setExpData(exp, currentLevel);
            return;
        }
        user.setExpData(exp, lvl);


        RewardData reward = guildData.getAlert(AlertType.REWARD, lvl);
        if (reward != null && !reward.isValid()) {
            String message = reward.getMessage();
            String[] roles = reward.getRolesAsArray();
            message = message.replace("#user", newGuy.getAsMention());
            message = message.replace("#level", String.valueOf(lvl));
            //message = message.replace("#role", role.getName());

            channel.sendMessage(message).queue();
            for (String roleID : roles) {
                Role role = guild.getRoleById(roleID);
                if (role == null)
                    continue;
                guild.addRoleToMember(UserSnowflake.fromId(newGuy.getId()), role).queue();
            }

            RewardData toDelete = null;
            if ((toDelete = guildData.getLowerReward(lvl)) != null && toDelete.isTemporary()) {
                roles = toDelete.getRolesAsArray();
                for (String roleID : roles) {
                    Role role = guild.getRoleById(roleID);
                    if (role == null)
                        continue;
                    guild.removeRoleFromMember(UserSnowflake.fromId(newGuy.getId()), role).queue();
                }
            }
            return;
        }
            

        AlertData alert = guildData.getAlert(AlertType.LEVEL_UP);
        if (alert != null && alert.isValid()) {
            String message = alert.getMessage();
            message = message.replace("#user", newGuy.getAsMention());
            message = message.replace("#level", String.valueOf(lvl));
            e.getChannel().asTextChannel().sendMessage(message).queue();
            return;
        }


        channel.sendMessage("Congratulations, you are now level: " + lvl).queue();
        return;
    }

}