package com.safjnest.SlashCommands.ManageMembers;

import java.util.Arrays;
import java.util.List;

import com.safjnest.Utilities.CommandsHandler;
import com.jagrosh.jdautilities.command.SlashCommand;
import com.jagrosh.jdautilities.command.SlashCommandEvent;

import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.channel.ChannelType;
import net.dv8tion.jda.api.entities.channel.concrete.VoiceChannel;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;

/**
 * @author <a href="https://github.com/NeutronSun">NeutroSun</a>
 * 
 * @since 1.3
 */
public class MoveChannelSlash extends SlashCommand{

    public MoveChannelSlash(){
        this.name = this.getClass().getSimpleName().replace("Slash", "").toLowerCase();
        this.aliases = new CommandsHandler().getArray(this.name, "alias");
        this.help = new CommandsHandler().getString(this.name, "help");
        this.cooldown = new CommandsHandler().getCooldown(this.name);
        this.category = new Category(new CommandsHandler().getString(this.name, "category"));
        this.arguments = new CommandsHandler().getString(this.name, "arguments");
        this.options = Arrays.asList(
            new OptionData(OptionType.CHANNEL, "room", "Room to move", true)
                .setChannelTypes(ChannelType.VOICE),
            new OptionData(OptionType.CHANNEL, "destroom", "Destination Room", false)
                .setChannelTypes(ChannelType.VOICE),
            new OptionData(OptionType.USER, "destuser", "Destination user", false));
    }

    @Override
    protected void execute(SlashCommandEvent event) {

        List<Member> theGuys = null;

        VoiceChannel channel = null;
        
        if(event.getOption("destroom") != null){
            if(event.getOption("destroom").getChannelType() != ChannelType.VOICE){
                event.deferReply(true).addContent("Select a voice channel.").queue();
                return;
            }
            channel = event.getOption("destroom").getAsChannel().asVoiceChannel();
        }else{
            Member destGuy = event.getOption("destuser").getAsMember();
            if(destGuy.getVoiceState().getChannel() == null){
                event.deferReply(true).addContent(destGuy.getEffectiveName()+ " needs to be in a voice channel.").queue();
                return;
            }
            channel = destGuy.getVoiceState().getChannel().asVoiceChannel();
        }
        
        theGuys = event.getGuild().getVoiceChannelById(event.getOption("room").getAsChannel().getId()).getMembers();
        for(Member member : theGuys){
            event.getGuild().moveVoiceMember(member, channel).queue();
        }
        event.deferReply(false).addContent("Moved  "+theGuys.size() +" users in:        "+ channel.getName() + ".").queue();

    }
}
