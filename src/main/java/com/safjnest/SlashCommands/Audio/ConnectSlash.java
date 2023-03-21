package com.safjnest.SlashCommands.Audio;

import java.util.EventListener;

import com.jagrosh.jdautilities.command.SlashCommand;
import com.jagrosh.jdautilities.command.SlashCommandEvent;
import com.safjnest.Utilities.CommandsHandler;

import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

/**
 * @author <a href="https://github.com/NeutronSun">NeutronSun</a>
 * @author <a href="https://github.com/Leon412">Leon412</a>
 * 
 * @since 1.0
 */
public class ConnectSlash extends SlashCommand implements EventListener{

    public ConnectSlash(){
        this.name = this.getClass().getSimpleName().replace("Slash", "").toLowerCase();
        this.aliases = new CommandsHandler().getArray(this.name, "alias");
        this.help = new CommandsHandler().getString(this.name, "help");
        this.cooldown = new CommandsHandler().getCooldown(this.name);
        this.category = new Category(new CommandsHandler().getString(this.name, "category"));
        this.arguments = new CommandsHandler().getString(this.name, "arguments");
    }

	@Override
	protected void execute(SlashCommandEvent event) {
        if(event.getMember().getVoiceState().getChannel() == null){
            event.deferReply(true).addContent("You need to be in a voice channel to use this command").queue();
            return;
        }
        else
		    event.getGuild().getAudioManager().openAudioConnection(event.getMember().getVoiceState().getChannel());
        event.deferReply(false).addContent("Im here!").queue();
	}

    public void onMessageReceived(MessageReceivedEvent e){
        System.out.println(e.getMessage().getAttachments().get(0).getFileExtension());
    }
}