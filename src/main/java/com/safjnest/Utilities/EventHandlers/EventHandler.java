package com.safjnest.Utilities.EventHandlers;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;

import com.jagrosh.jdautilities.command.SlashCommand;
import com.jagrosh.jdautilities.command.SlashCommandEvent;
import com.safjnest.Commands.LOL.Summoner;
import com.safjnest.Utilities.DatabaseHandler;
import com.safjnest.Utilities.SQL;
import com.safjnest.Utilities.Commands.SlashCommandsHandler;
import com.safjnest.Utilities.LOL.LOLHandler;

import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.entities.channel.middleman.MessageChannel;
import net.dv8tion.jda.api.events.channel.ChannelDeleteEvent;
import net.dv8tion.jda.api.events.guild.member.GuildMemberJoinEvent;
import net.dv8tion.jda.api.events.guild.member.GuildMemberRemoveEvent;
import net.dv8tion.jda.api.events.guild.member.update.GuildMemberUpdateBoostTimeEvent;
import net.dv8tion.jda.api.events.guild.voice.GuildVoiceUpdateEvent;
import net.dv8tion.jda.api.events.interaction.command.CommandAutoCompleteInteractionEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.StringSelectInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.Command.Choice;

/**
 * This class handles all events that could occur during the listening:
 * <ul>
 * <li>On update of a voice channel (to make the bot leave an empty voice
 * channel)</li>
 * <li>On join of a user (to make the bot welcome the new member)</li>
 * 
 * @author <a href="https://github.com/NeutronSun">NeutronSun</a>
 * 
 * @since 1.2
 */
public class EventHandler extends ListenerAdapter {
    private SQL sql;
    private SlashCommandsHandler sch;

    public EventHandler(SQL sql, SlashCommandsHandler sch) {
        this.sql = sql;
        this.sch = sch;
    }

    /**
     * On update of a voice channel (to make the bot leave an empty voice channel)
     */
    @Override
    public void onGuildVoiceUpdate(GuildVoiceUpdateEvent e) {
        if((e.getGuild().getAudioManager().isConnected() && e.getChannelLeft() != null) &&
            (e.getGuild().getAudioManager().getConnectedChannel().getId().equals(e.getChannelLeft().getId())) &&
            (e.getChannelLeft().getMembers().size() == 1)){
            e.getGuild().getAudioManager().closeAudioConnection();
        }
    }


    @Override
    public void onSlashCommandInteraction(SlashCommandInteractionEvent event) {
        SlashCommandEvent e = new SlashCommandEvent(event, null);    
        SlashCommand sc = sch.getCommand(e.getName());
        try {
            Method executeMethod = SlashCommand.class.getDeclaredMethod("execute", SlashCommandEvent.class);
            executeMethod.setAccessible(true);
            executeMethod.invoke(sc, e);
        } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException | NoSuchMethodException exp) {}
    
    }
    


    @Override
    public void onCommandAutoCompleteInteraction(CommandAutoCompleteInteractionEvent e) {
        if (e.getName().equals("playsound")) {
            if(e.getFocusedOption().getValue().equals("")){
                String query = "SELECT name, id FROM sound WHERE guild_id = '" + e.getGuild().getId() + "' ORDER BY RAND() LIMIT 25;";
                ArrayList<Choice> choices = new ArrayList<>();
                for(ArrayList<String> arr : DatabaseHandler.getSql().getAllRows(query, 2))
                    choices.add(new Choice(arr.get(0), arr.get(1)));
                e.replyChoices(choices).queue();
            }else{
                String query = "SELECT name, id FROM sound WHERE name LIKE '"+e.getFocusedOption().getValue()+"%' AND guild_id = '" + e.getGuild().getId() + "' ORDER BY RAND() LIMIT 25;";
                ArrayList<Choice> choices = new ArrayList<>();
                for(ArrayList<String> arr : DatabaseHandler.getSql().getAllRows(query, 2))
                    choices.add(new Choice(arr.get(0), arr.get(1)));
                e.replyChoices(choices).queue();
            }
            
        }
    }

    /**
     * On join of a user (to make the bot welcome the new member)
     */
    @Override
    public void onGuildMemberJoin(GuildMemberJoinEvent event) {
        MessageChannel channel = null;
        User newGuy = event.getUser();
        String query = "SELECT channel_id FROM welcome_message WHERE discord_id = '" + event.getGuild().getId()
                + "' AND bot_id = '" + event.getJDA().getSelfUser().getId() + "';";
        String notNullPls = sql.getString(query, "channel_id");
        if (notNullPls == null)
            return;
        channel = event.getGuild().getTextChannelById(notNullPls);
        query = "SELECT message_text FROM welcome_message WHERE discord_id = '" + event.getGuild().getId()
                + "' AND bot_id = '" + event.getJDA().getSelfUser().getId() + "';";
        String message = sql.getString(query, "message_text");
        message = message.replace("#user", newGuy.getAsMention());
        channel.sendMessage(message).queue();
        query = "SELECT role_id FROM welcome_roles WHERE discord_id = '" + event.getGuild().getId() + "' AND bot_id = '"
                + event.getJDA().getSelfUser().getId() + "';";
        ArrayList<String> roles = sql.getAllRowsSpecifiedColumn(query, "role_id");
        if (roles.size() > 0) {
            for (String role : roles) {
                event.getGuild().addRoleToMember(newGuy, event.getGuild().getRoleById(role)).queue();
            }
        }
    }

    @Override
    public void onGuildMemberRemove(GuildMemberRemoveEvent event){
        MessageChannel channel = null;
        String query = "SELECT channel_id FROM left_message WHERE discord_id = '" + event.getGuild().getId()
                + "' AND bot_id = '" + event.getJDA().getSelfUser().getId() + "';";
        String notNullPls = sql.getString(query, "channel_id");
        if (notNullPls == null)
            return;
        channel = event.getGuild().getTextChannelById(notNullPls);
        query = "SELECT message_text FROM left_message WHERE discord_id = '" + event.getGuild().getId()
                + "' AND bot_id = '" + event.getJDA().getSelfUser().getId() + "';";
        String message = sql.getString(query, "message_text");
        message = message.replace("#user", event.getUser().getAsMention());
        channel.sendMessage(message).queue();
    }

    /**
     * On update of a user's boost time (to make the bot praise the user)
     */
    public void onGuildMemberUpdateBoostTime​(GuildMemberUpdateBoostTimeEvent event) {
        User newguy = event.getUser();
        TextChannel welcome = event.getGuild().getSystemChannel();
        welcome.sendMessage("NO FUCKING WAY " + newguy.getAsMention() + " HA BOOSTATO IL SERVER!!\n"
                + event.getGuild().getBoostCount()).queue();
    }


    @Override
    public void onChannelDelete(ChannelDeleteEvent event){
        if(event.getChannelType().isAudio()){
            String query = "DELETE from rooms_nickname WHERE discord_id = '" + event.getGuild().getId()
                           + "' AND room_id = '" + event.getChannel().getId() + "';";
            DatabaseHandler.getSql().runQuery(query);
        }
    }

    @Override
    public void onStringSelectInteraction(StringSelectInteractionEvent event) {
        if (event.getComponentId().equals("rank-select")) {
            no.stelar7.api.r4j.pojo.lol.summoner.Summoner s = LOLHandler.getSummonerBySummonerId(event.getValues().get(0));
            event.deferReply().addEmbeds(Summoner.createEmbed(event.getJDA(), event.getJDA().getSelfUser().getId(), s).build()).queue();
        }
    }

}