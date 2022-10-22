package com.safjnest.Commands.Audio;

import java.util.EventListener;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import com.safjnest.Utilities.CommandsHandler;

import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

/**
 * @author <a href="https://github.com/NeutronSun">NeutronSun</a>
 * @author <a href="https://github.com/Leon412">Leon412</a>
 * 
 * @since 1.0
 */
public class Connect extends Command implements EventListener{

    public Connect(){
        this.name = this.getClass().getSimpleName();
        this.aliases = new CommandsHandler().getArray(this.name, "alias");
        this.help = new CommandsHandler().getString(this.name, "help");
        this.cooldown = new CommandsHandler().getCooldown(this.name);
        this.category = new Category(new CommandsHandler().getString(this.name, "category"));
        this.arguments = new CommandsHandler().getString(this.name, "arguments");
    }

	@Override
	protected void execute(CommandEvent event) {
        if(event.getMember().getVoiceState().getChannel() == null)
            event.reply("You need to be in a voice channel to use this command");
        else
		    event.getGuild().getAudioManager().openAudioConnection(event.getMember().getVoiceState().getChannel());

	}

    public void onMessageReceived(MessageReceivedEvent e){
        System.out.println(e.getMessage().getAttachments().get(0).getFileExtension());
    }
}