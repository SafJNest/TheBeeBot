package com.safjnest.Commands.ManageGuild;

import java.util.List;

import com.safjnest.Utilities.CommandsLoader;
import com.safjnest.Utilities.SafJNest;
import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;

import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageHistory;

/**
 * @author <a href="https://github.com/NeutronSun">NeutronSun</a>
 * @author <a href="https://github.com/Leon412">Leon412</a>
 * 
 * @since 1.0
 */
public class Clear extends Command {

    public Clear(){
        this.name = this.getClass().getSimpleName();;
        this.aliases = new CommandsLoader().getArray(this.name, "alias");
        this.help = new CommandsLoader().getString(this.name, "help");
        this.cooldown = new CommandsLoader().getCooldown(this.name);
        this.category = new Category(new CommandsLoader().getString(this.name, "category"));
        this.arguments = new CommandsLoader().getString(this.name, "arguments");
        this.botPermissions = new Permission[]{Permission.MESSAGE_MANAGE};
        this.userPermissions = new Permission[]{Permission.MESSAGE_MANAGE};
    }

	@Override
	protected void execute(CommandEvent event) {
        if(!SafJNest.intIsParsable(event.getArgs())) {
            event.reply("Specify how many messages to delete (max 99).");
            return;
        }
        int n = Integer.parseInt(event.getArgs());

        if(n > 99){
            event.reply("You can't delete more than 99 messages at once.");
            return;
        }

        MessageHistory history = new MessageHistory(event.getChannel());
        List<Message> msgs = history.retrievePast(n + 1).complete();
        event.getTextChannel().deleteMessages(msgs).queue();
	}
}