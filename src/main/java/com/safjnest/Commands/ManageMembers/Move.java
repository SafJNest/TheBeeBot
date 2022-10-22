package com.safjnest.Commands.ManageMembers;

import java.util.List;

import com.jagrosh.jdautilities.command.Command;
import com.safjnest.Utilities.CommandsHandler;
import com.safjnest.Utilities.PostgreSQL;
import com.jagrosh.jdautilities.command.CommandEvent;

import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.channel.concrete.VoiceChannel;

/**
 * @author <a href="https://github.com/NeutronSun">NeutroSun</a>
 * 
 * @since 1.3
 */
public class Move extends Command{

    private PostgreSQL sql;
    public Move(PostgreSQL sql){
        this.name = this.getClass().getSimpleName();
        this.aliases = new CommandsHandler().getArray(this.name, "alias");
        this.help = new CommandsHandler().getString(this.name, "help");
        this.cooldown = new CommandsHandler().getCooldown(this.name);
        this.category = new Category(new CommandsHandler().getString(this.name, "category"));
        this.arguments = new CommandsHandler().getString(this.name, "arguments");
        this.sql = sql;
    }

    @Override
    protected void execute(CommandEvent event) {
        User theGuy = null;
        List<Member> theGuys = null;
        VoiceChannel channel = null;
        boolean flag = true;
        String[] args = event.getArgs().split(" ",2);
        if(args[0].equalsIgnoreCase("me"))
            theGuy = event.getAuthor();

        else if(args[0].equalsIgnoreCase("here")){
            theGuys = event.getMember().getVoiceState().getChannel().getMembers();
        }else if(event.getMessage().getMentions().getUsers().size() > 0){
            theGuy = event.getMessage().getMentions().getUsers().get(0);
            flag = false;
        }else{
            try {
                if(event.getGuild().getVoiceChannelById(args[0])!=null)
                    theGuys = event.getGuild().getVoiceChannelById(args[0]).getMembers();
                theGuy = event.getJDA().getUserById(args[0]);
                
            } catch (Exception e) {
                event.reply("Error");
                return;
            }
        }
        //get the channel
        if(args[1].equalsIgnoreCase("")){
            event.reply("Missing user/s to move.");
            return;
        }else if(args[1].equalsIgnoreCase("here"))
            channel = event.getGuild().getVoiceChannelById(event.getMember().getVoiceState().getChannel().getId());
       
        else if(args[1].equalsIgnoreCase("afk")){
            channel = event.getGuild().getAfkChannel();
            if(channel == null){
                event.reply("Set an afk channel.");
                return;
            }
        }else if(event.getMessage().getMentions().getMembers().size() == 1 && flag)
            channel = event.getGuild().getVoiceChannelById(event.getMessage().getMentions().getMembers().get(0).getVoiceState().getChannel().getId());
        
        else if(event.getMessage().getMentions().getMembers().size() == 2)
            channel = event.getGuild().getVoiceChannelById(event.getMessage().getMentions().getMembers().get(1).getVoiceState().getChannel().getId());
        
        else{
            String query = "SELECT room_id FROM rooms_nickname WHERE discord_id = '" + event.getGuild().getId() + "' AND room_name = '" + args[1] +"';";
            String idRoom = (sql.getString(query, "room_id") == null) 
                            ? "" 
                            : sql.getString(query, "room_id");
            if(idRoom.equals("")){
                try {channel = event.getGuild().getVoiceChannelById(args[1]);} 
                catch (Exception e) {
                    event.reply("Missing channel or not found.");
                    return;
                }
            }
            channel = (event.getGuild().getVoiceChannelById(idRoom) != null) 
                        ? event.getGuild().getVoiceChannelById(idRoom)
                        : null;
            if(channel == null){
                event.reply("Missing channel or not found.");
                return;
            }else if(channel.getId().equals(event.getMember().getVoiceState().getChannel().getId())){
                event.reply("You are already there.");
                return;
            }
        }
        if(theGuy == null){
            for(Member member : theGuys){
                event.getGuild().moveVoiceMember(member, channel).queue();
            }
            event.reply("Moved  "+theGuys.size() +" users in:        "+ channel.getName());
            return;
        }
        event.getGuild().moveVoiceMember(event.getGuild().getMember(theGuy), channel).queue();
        event.reply(theGuy.getAsMention() + " moved in: " + channel.getName());
            

    }
}
