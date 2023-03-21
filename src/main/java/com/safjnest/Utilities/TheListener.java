package com.safjnest.Utilities;

import java.util.ArrayList;

import com.safjnest.Commands.Audio.List;
import com.safjnest.Commands.LOL.Summoner;
import com.safjnest.Utilities.LOL.LOLHandler;

import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.entities.channel.middleman.MessageChannel;
import net.dv8tion.jda.api.events.guild.member.GuildMemberJoinEvent;
import net.dv8tion.jda.api.events.guild.member.update.GuildMemberUpdateBoostTimeEvent;
import net.dv8tion.jda.api.events.guild.voice.GuildVoiceUpdateEvent;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.components.buttons.Button;

/**
 * This class handles all events that could occur during the listening:
 * <ul>
 * <li>On update of a voice channel (to make the bot leave an empty voice channel)</li>
 * <li>On join of a user (to make the bot welcome the new member)</li>
 * @author <a href="https://github.com/NeutronSun">NeutronSun</a>
 * 
 * @since 1.2
 */
public class TheListener extends ListenerAdapter{
    private SQL sql;

    public TheListener(SQL sql){
        this.sql = sql;
    }
    /**
     * On update of a voice channel (to make the bot leave an empty voice channel)
     */
    @Override
    public void onGuildVoiceUpdate(GuildVoiceUpdateEvent e){
        if(e.getGuild().getAudioManager().isConnected() && e.getChannelLeft()!=null && e.getChannelLeft().getMembers().size() == 1 ){
            e.getGuild().getAudioManager().closeAudioConnection();
        }
    }

    /**
     * On join of a user (to make the bot welcome the new member)
     */
    @Override 
    public void onGuildMemberJoin(GuildMemberJoinEvent event) {
        MessageChannel channel = null;
        User newGuy = event.getUser();
        String query = "SELECT channel_id FROM welcome_message WHERE discord_id = '" + event.getGuild().getId() + "' AND bot_id = '"+event.getJDA().getSelfUser().getId()+"';";
        String notNullPls = sql.getString(query, "channel_id");
        if(notNullPls == null)
            return;
        channel = event.getGuild().getTextChannelById(notNullPls);
        query = "SELECT message_text FROM welcome_message WHERE discord_id = '" + event.getGuild().getId() + "' AND bot_id = '"+event.getJDA().getSelfUser().getId()+"';";
        String message = sql.getString(query, "message_text");
        message = message.replace("#user", newGuy.getAsMention());
        channel.sendMessage(message).queue();
        query = "SELECT role_id FROM welcome_roles WHERE discord_id = '" + event.getGuild().getId() + "' AND bot_id = '"+event.getJDA().getSelfUser().getId()+"';";
        ArrayList<String> roles = sql.getListString(query, "role_id");
        if(roles.size() > 0){
            for(String role : roles){
                event.getGuild().addRoleToMember(newGuy, event.getGuild().getRoleById(role)).queue();
            }
        }
    }

    /**
     * On update of a user's boost time (to make the bot praise the user)
     */    
    public void onGuildMemberUpdateBoostTime​(GuildMemberUpdateBoostTimeEvent event){
        User newguy = event.getUser();
        TextChannel welcome = event.getGuild().getSystemChannel();
        welcome.sendMessage("NO FUCKING WAY " + newguy.getAsMention() + " HA BOOSTATO IL SERVER!!\n" + event.getGuild().getBoostCount()).queue();
    }

    @Override
    public void onButtonInteraction(ButtonInteractionEvent event){
        if(event.getButton().getId().equals("lexo")){
            event.deferEdit().queue();
            event.getMessage().editMessage(List.getListLexo(event.getJDA(), sql, event.getGuild().getId())).queue();
        }else if(event.getButton().getId().equals("idOrder")){
            event.deferEdit().queue();
            event.getMessage().editMessage(List.getListId(event.getJDA(), sql, event.getGuild().getId())).queue();
        }else if(event.getButton().getId().equals("mostPlayed")){
            event.deferEdit().queue();
            event.getMessage().editMessage(List.getListMostPlayed(event.getJDA(), sql, event.getGuild().getId())).queue();
        }else if(event.getButton().getId().equals("byUser")){
            event.deferEdit().queue();
            event.getMessage().editMessage(List.getListUser(event.getJDA(), sql, event.getMember().getId())).queue();
        }



        else if(event.getButton().getId().equals("right")){
            String nameSum = "";
            int index = 0;
            event.deferEdit().queue();
            String query = "SELECT summoner_id FROM lol_user WHERE discord_id = '" + event.getMember().getId() + "';";
            ArrayList<ArrayList<String>> accounts = DatabaseHandler.getSql().getTuple(query, 1);
            for(Button b : event.getMessage().getButtons()){
                if(!b.getLabel().equals("->") && !b.getLabel().equals("<-"))
                    nameSum = b.getLabel();
            }
            for(int i = 0; i < accounts.size(); i++){
                if(LOLHandler.getSummonerById(accounts.get(i).get(0)).getName().equals(nameSum))
                    index = i;
                
            }

            if((index+1) == accounts.size())
                index = 0;
            else
                index+=1;
            
            Button left = Button.primary("left", "<-");
            Button right = Button.primary("right", "->");
            Button center = Button.primary("center", LOLHandler.getSummonerById(accounts.get(index).get(0)).getName());
            event.getMessage().editMessageEmbeds(Summoner.createEmbed(event.getJDA().getSelfUser().getId(), LOLHandler.getSummonerById(accounts.get(index).get(0))).build())
                .setActionRow(left, center, right)
                .queue();
        }else if(event.getButton().getId().equals("left")){
            String nameSum = "";
            int index = 0;
            event.deferEdit().queue();
            String query = "SELECT summoner_id FROM lol_user WHERE discord_id = '" + event.getMember().getId() + "';";
            ArrayList<ArrayList<String>> accounts = DatabaseHandler.getSql().getTuple(query, 1);
            for(Button b : event.getMessage().getButtons()){
                if(!b.getLabel().equals("->") && !b.getLabel().equals("<-"))
                    nameSum = b.getLabel();
            }
            for(int i = 0; i < accounts.size(); i++){
                if(LOLHandler.getSummonerById(accounts.get(i).get(0)).getName().equals(nameSum))
                    index = i;
                
            }

            if(index == 0)
                index = accounts.size()-1;
            else
                index-=1;
            
            Button left = Button.primary("left", "<-");
            Button right = Button.primary("right", "->");
            Button center = Button.primary("center", LOLHandler.getSummonerById(accounts.get(index).get(0)).getName());
            event.getMessage().editMessageEmbeds(Summoner.createEmbed(event.getJDA().getSelfUser().getId(), LOLHandler.getSummonerById(accounts.get(index).get(0))).build())
                .setActionRow(left, center, right)
                .queue();
        }
    }


}