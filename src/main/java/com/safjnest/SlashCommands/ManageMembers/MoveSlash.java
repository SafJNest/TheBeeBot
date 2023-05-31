package com.safjnest.SlashCommands.ManageMembers;

import java.util.Arrays;

import com.jagrosh.jdautilities.command.SlashCommand;
import com.jagrosh.jdautilities.command.SlashCommandEvent;
import com.safjnest.Utilities.Commands.CommandsHandler;

import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.channel.ChannelType;
import net.dv8tion.jda.api.entities.channel.concrete.VoiceChannel;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;

/**
 * @author <a href="https://github.com/NeutronSun">NeutroSun</a>
 * 
 * @since 1.3
 */
public class MoveSlash extends SlashCommand{

    public MoveSlash(){
        this.name = this.getClass().getSimpleName().replace("Slash", "").toLowerCase();
        this.aliases = new CommandsHandler().getArray(this.name, "alias");
        this.help = new CommandsHandler().getString(this.name, "help");
        this.cooldown = new CommandsHandler().getCooldown(this.name);
        this.category = new Category(new CommandsHandler().getString(this.name, "category"));
        this.arguments = new CommandsHandler().getString(this.name, "arguments");
        this.options = Arrays.asList(
            new OptionData(OptionType.USER, "user", "User to move", true),
            new OptionData(OptionType.CHANNEL, "destroom", "Destination Room", false)
                .setChannelTypes(ChannelType.VOICE),
            new OptionData(OptionType.USER, "destuser", "Destination User", false));
    }

    @Override
    protected void execute(SlashCommandEvent event) {
        User theGuy = event.getOption("user").getAsUser();
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
        
       
        event.getGuild().moveVoiceMember(event.getGuild().getMember(theGuy), channel).queue();
        event.reply(theGuy.getAsMention() + " moved in: " + channel.getName());
        event.deferReply(false).addContent(theGuy.getName() + " moved to " + channel.getName() + ".").queue();

    }
}
