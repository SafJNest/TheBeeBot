package com.safjnest.Commands.Misc;

import java.awt.Color;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import com.safjnest.Utilities.CommandsHandler;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.User;

/**
 * @author <a href="https://github.com/Leon412">Leon412</a>
 * 
 * @since 1.1
 */
public class Msg extends Command {

    public Msg(){
        this.name = this.getClass().getSimpleName();
        this.aliases = new CommandsHandler().getArray(this.name, "alias");
        this.help = new CommandsHandler().getString(this.name, "help");
        this.cooldown = new CommandsHandler().getCooldown(this.name);
        this.category = new Category(new CommandsHandler().getString(this.name, "category"));
        this.arguments = new CommandsHandler().getString(this.name, "arguments");
    }

	@Override
	protected void execute(CommandEvent event) {
        User theGuy = null;
        String[] command = event.getArgs().split(" ", 2);
        if(event.getMessage().getMentions().getMembers().size() > 0)
                theGuy = event.getMessage().getMentions().getMembers().get(0).getUser();
            else
                theGuy = event.getJDA().retrieveUserById(command[0]).complete();
        EmbedBuilder eb = new EmbedBuilder();
        eb.setTitle("NEW MESSAGE FROM " + event.getAuthor().getAsTag());
        eb.setThumbnail(event.getAuthor().getAvatarUrl());
        eb.setDescription(command[1]);
        eb.setColor(new Color(3, 252, 169));
        theGuy.openPrivateChannel().queue((privateChannel) -> privateChannel.sendMessageEmbeds(eb.build()).queue());
	}
}