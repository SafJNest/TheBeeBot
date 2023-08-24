package com.safjnest.Commands.Dangerous;

import java.util.List;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import com.safjnest.Utilities.CommandsLoader;

import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.channel.concrete.VoiceChannel;

/**
 * @author <a href="https://github.com/NeutronSun">NeutroSun</a>
 * 
 * @since 1.3
 */
public class RandomMove extends Command{

    public RandomMove(){
        this.name = this.getClass().getSimpleName();
        this.aliases = new CommandsLoader().getArray(this.name, "alias");
        this.help = new CommandsLoader().getString(this.name, "help");
        this.cooldown = new CommandsLoader().getCooldown(this.name);
        this.category = new Category(new CommandsLoader().getString(this.name, "category"));
        this.arguments = new CommandsLoader().getString(this.name, "arguments");
        this.hidden = true;
    }

    @Override
    protected void execute(CommandEvent event) {
        User theGuy = null;
        List<Member> theGuys = null;
        List<VoiceChannel> channels = null;
        VoiceChannel channel = null;
        VoiceChannel startChannel = null;
        
        int n;
        String[] args = event.getArgs().split(" ");
        if(args[0].equalsIgnoreCase("me"))
            theGuy = event.getAuthor();
        

        if(args[0].equalsIgnoreCase("here")){
            theGuys = event.getMember().getVoiceState().getChannel().getMembers();
        }else if(event.getMessage().getMentions().getMembers().size() > 0){
            theGuy = event.getMessage().getMentions().getUsers().get(0);
        }else{
            event.reply("You have to mention or write the Id of the one you want to random move");
            return;
        }
        if(theGuy == null)
            startChannel = event.getGuild().getVoiceChannelById(event.getGuild().getMemberById(event.getAuthor().getId()).getVoiceState().getChannel().getId());
        else
            startChannel = event.getGuild().getVoiceChannelById(event.getGuild().getMemberById(theGuy.getId()).getVoiceState().getChannel().getId());
        
        n = Integer.parseInt(args[1]);
        channels = event.getGuild().getVoiceChannels();
        if(theGuy == null){
            for(int i = 0; i < n - 1; i++){
                channel = channels.get((int)(Math.random() * (channels.size()-1)));
                for(Member member : theGuys){
                    event.getGuild().moveVoiceMember(member, channel).queue();
                }
            }
            for(Member member : theGuys){
                event.getGuild().moveVoiceMember(member, startChannel).queue();
            }
            return;
        }
        for(int i = 0; i < n; i++){
            channel = channels.get((int)(Math.random() * (channels.size()-1)));
            event.getGuild().moveVoiceMember(event.getGuild().getMember(theGuy), channel).queue();
        }
        event.getGuild().moveVoiceMember(event.getGuild().getMember(theGuy), startChannel).queue();
        event.getMessage().delete().queue();
        return;
            

    }
}
