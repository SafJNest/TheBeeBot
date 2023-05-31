package com.safjnest.Commands.Audio;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import com.safjnest.Utilities.Commands.CommandsHandler;

import net.dv8tion.jda.api.entities.User;

/**
 * @author <a href="https://github.com/NeutronSun">NeutronSun</a>
 * @author <a href="https://github.com/Leon412">Leon412</a>
 * 
 * @since 1.0
 */
public class Disconnect extends Command {

    public Disconnect(){
        this.name = this.getClass().getSimpleName();;
        this.aliases = new CommandsHandler().getArray(this.name, "alias");
        this.help = new CommandsHandler().getString(this.name, "help");
        this.cooldown = new CommandsHandler().getCooldown(this.name);
        this.category = new Category(new CommandsHandler().getString(this.name, "category"));
        this.arguments = new CommandsHandler().getString(this.name, "arguments");
    }

	@Override
	protected void execute(CommandEvent event) {
        User theGuy = null;
        if(event.getArgs().equalsIgnoreCase("bot") || event.getArgs().equalsIgnoreCase("")){
            event.getGuild().getAudioManager().closeAudioConnection();
        }
        else if(event.getMessage().getMentions().getMembers().size() > 0){
            theGuy = event.getMessage().getMentions().getMembers().get(0).getUser();
            event.getGuild().kickVoiceMember(event.getGuild().getMember(theGuy)).queue();
        }
        else{
            event.reply("I don't know who to disconnect (mention the member you want to disconnect or write bot or nothing to disconnect the bot)");
            return;
        }
	}
}