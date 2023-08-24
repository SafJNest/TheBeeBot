package com.safjnest.Utilities.EventHandlers;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import com.safjnest.Commands.League.Summoner;
import com.safjnest.SlashCommands.ManageGuild.RewardsSlash;
import com.safjnest.Utilities.DatabaseHandler;
import com.safjnest.Utilities.SQL;
import com.safjnest.Utilities.Guild.GuildSettings;
import com.safjnest.Utilities.LOL.Augment;
import com.safjnest.Utilities.LOL.RiotHandler;

import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.channel.middleman.MessageChannel;
import net.dv8tion.jda.api.events.channel.ChannelDeleteEvent;
import net.dv8tion.jda.api.events.guild.member.GuildMemberJoinEvent;
import net.dv8tion.jda.api.events.guild.member.GuildMemberRemoveEvent;
import net.dv8tion.jda.api.events.guild.member.update.GuildMemberUpdateBoostTimeEvent;
import net.dv8tion.jda.api.events.guild.voice.GuildVoiceUpdateEvent;
import net.dv8tion.jda.api.events.interaction.ModalInteractionEvent;
import net.dv8tion.jda.api.events.interaction.command.CommandAutoCompleteInteractionEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.StringSelectInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.Command;
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
    private GuildSettings gs;
    public EventHandler(SQL sql, GuildSettings gs) {
        this.sql = sql;
        this.gs = gs;
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
        if(!gs.getServer(event.getGuild().getId()).getCommandStatsRoom(event.getChannel().getIdLong()))
            return;
        String commandName = event.getName() + "Slash";
        String query = "INSERT INTO command_analytic(name, time, user_id) VALUES ('" + commandName + "', '" + new Timestamp(System.currentTimeMillis()) + "', '" + event.getUser().getId() + "');";
        sql.runQuery(query);
    }


    @Override
    public void onCommandAutoCompleteInteraction(CommandAutoCompleteInteractionEvent e) {
        ArrayList<Choice> choices = new ArrayList<>();
        switch (e.getName()){
            
            case "play":
                if(e.getFocusedOption().getValue().equals("")){
                    String query = "SELECT name, id FROM sound WHERE guild_id = '" + e.getGuild().getId() + "' ORDER BY RAND() LIMIT 25;";
                    for(ArrayList<String> arr : DatabaseHandler.getSql().getAllRows(query, 2))
                        choices.add(new Choice(arr.get(0), arr.get(1)));
                }else{
                    String query = "SELECT name, id FROM sound WHERE name LIKE '"+e.getFocusedOption().getValue()+"%' AND guild_id = '" + e.getGuild().getId() + "' ORDER BY RAND() LIMIT 25;";
                    for(ArrayList<String> arr : DatabaseHandler.getSql().getAllRows(query, 2))
                        choices.add(new Choice(arr.get(0), arr.get(1)));
                }
                break;

            case "help":

                List<Command> allCommands = e.getJDA().retrieveCommands().complete();
                if(e.getFocusedOption().getValue().equals("")){
                    Collections.shuffle(allCommands);
                    for(int i = 0; i < 10; i++)
                        choices.add(new Choice(allCommands.get(i).getName(), allCommands.get(i).getName()));
                }else{
                    for(Command c : allCommands){
                        if(c.getName().startsWith(e.getFocusedOption().getValue()))
                            choices.add(new Choice(c.getName(), c.getName()));
                    }
                }
                break;

            case "champion":
                List<String> champions = Arrays.asList(RiotHandler.getChampions());
                if(e.getFocusedOption().getValue().equals("")){
                    Collections.shuffle(champions);
                    for(int i = 0; i < 10; i++)
                        choices.add(new Choice(champions.get(i), champions.get(i)));
                }else{
                    int max = 0;
                    for(int i = 0; i < champions.size() && max < 10; i++){
                        if(champions.get(i).toLowerCase().startsWith(e.getFocusedOption().getValue().toLowerCase())){
                            choices.add(new Choice(champions.get(i), champions.get(i)));
                            max++;
                        }
                    }
                }
                break; 
            
            case "infoaugment":
                List<Augment> augments = RiotHandler.getAugments();
                if(e.getFocusedOption().getValue().equals("")){
                    Collections.shuffle(augments);
                    for(int i = 0; i < 10; i++)
                        choices.add(new Choice(augments.get(i).getName(), augments.get(i).getId()));
                }else{
                    int max = 0;
                    if(e.getFocusedOption().getValue().matches("\\d+")){
                        for(int i = 0; i < augments.size() && max < 10; i++){
                            if(augments.get(i).getId().startsWith(e.getFocusedOption().getValue())){
                                choices.add(new Choice(augments.get(i).getName(), augments.get(i).getId()));
                                max++;
                            }
                        }
                        }else{
                            for(int i = 0; i < augments.size() && max < 10; i++){
                                if(augments.get(i).getName().toLowerCase().startsWith(e.getFocusedOption().getValue().toLowerCase())){
                                    choices.add(new Choice(augments.get(i).getName(), augments.get(i).getId()));
                                    max++;
                                }
                            }
                    }
                }
            break; 
        }
        e.replyChoices(choices).queue();
    }

    @Override
    public void onModalInteraction(ModalInteractionEvent event) {
        if(event.getModalId().startsWith("rewards")){
            String role = event.getValue("rewards-role").getAsString();
            String lvl = event.getValue("rewards-lvl").getAsString();
            String msg = event.getValue("rewards-message").getAsString();

            if(msg.equals("//")) 
                msg = null;
            try {
                role = event.getGuild().getRolesByName(role.substring(1), true).get(0).getId();
            } catch (Exception e) {
                event.reply("Role not found").queue();
                return;
            }
            String query = "INSERT INTO rewards_table (guild_id, role_id, level, message_text) VALUES ('" + event.getGuild().getId() + "', '" + role + "', '" + lvl + "', '" + msg + "');";
            DatabaseHandler.getSql().runQuery(query);
            event.deferEdit().queue();
            RewardsSlash.createEmbed(event.getMessage(), event.getGuild()).queue();
        }
    }

    /**
     * On join of a user (to make the bot welcome the new member)
     */
    @Override
    public void onGuildMemberJoin(GuildMemberJoinEvent event) {
        MessageChannel channel = null;
        User newGuy = event.getUser();
        String query = "SELECT channel_id FROM welcome_message WHERE guild_id = '" + event.getGuild().getId()
                + "' AND bot_id = '" + event.getJDA().getSelfUser().getId() + "';";
        String notNullPls = sql.getString(query, "channel_id");
        if (notNullPls == null)
            return;
        channel = event.getGuild().getTextChannelById(notNullPls);
        query = "SELECT message_text FROM welcome_message WHERE guild_id = '" + event.getGuild().getId()
                + "' AND bot_id = '" + event.getJDA().getSelfUser().getId() + "';";
        String message = sql.getString(query, "message_text");
        message = message.replace("#user", newGuy.getAsMention());
        channel.sendMessage(message).queue();
        query = "SELECT role_id FROM welcome_roles WHERE guild_id = '" + event.getGuild().getId() + "' AND bot_id = '"
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
        String query = "SELECT channel_id FROM left_message WHERE guild_id = '" + event.getGuild().getId()
                + "' AND bot_id = '" + event.getJDA().getSelfUser().getId() + "';";
        String notNullPls = sql.getString(query, "channel_id");
        if (notNullPls == null)
            return;
        channel = event.getGuild().getTextChannelById(notNullPls);
        query = "SELECT message_text FROM left_message WHERE guild_id = '" + event.getGuild().getId()
                + "' AND bot_id = '" + event.getJDA().getSelfUser().getId() + "';";
        String message = sql.getString(query, "message_text");
        message = message.replace("#user", event.getUser().getAsMention());
        channel.sendMessage(message).queue();
    }

    /**
     * On update of a user's boost time (to make the bot praise the user)
     */
    public void onGuildMemberUpdateBoostTime(GuildMemberUpdateBoostTimeEvent event) {
        MessageChannel channel = null;
        String query = "SELECT channel_id FROM boost_message WHERE guild_id = '" + event.getGuild().getId()
                + "' AND bot_id = '" + event.getJDA().getSelfUser().getId() + "';";
        String notNullPls = sql.getString(query, "channel_id");
        if (notNullPls == null)
            return;
        channel = event.getGuild().getTextChannelById(notNullPls);
        query = "SELECT message_text FROM boost_message WHERE guild_id = '" + event.getGuild().getId()
                + "' AND bot_id = '" + event.getJDA().getSelfUser().getId() + "';";
        String message = sql.getString(query, "message_text");
        message = message.replace("#user", event.getUser().getAsMention());
        channel.sendMessage(message).queue();
    }


    @Override
    public void onChannelDelete(ChannelDeleteEvent event){
        if(event.getChannelType().isAudio()){
            String query = "DELETE from rooms_settings WHERE guild_id = '" + event.getGuild().getId()
                           + "' AND room_id = '" + event.getChannel().getId() + "';";
            DatabaseHandler.getSql().runQuery(query);
        }
    }

    @Override
    public void onStringSelectInteraction(StringSelectInteractionEvent event) {
        if (event.getComponentId().equals("rank-select")) {
            no.stelar7.api.r4j.pojo.lol.summoner.Summoner s = RiotHandler.getSummonerBySummonerId(event.getValues().get(0));
            event.deferReply().addEmbeds(Summoner.createEmbed(event.getJDA(), event.getJDA().getSelfUser().getId(), s).build()).queue();
        }
    }

}