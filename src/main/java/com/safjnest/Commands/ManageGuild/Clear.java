package com.safjnest.Commands.ManageGuild;

import java.util.List;

import com.safjnest.Utilities.CommandsHandler;
import com.safjnest.Utilities.PermissionHandler;

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
        this.aliases = new CommandsHandler().getArray(this.name, "alias");
        this.help = new CommandsHandler().getString(this.name, "help");
        this.cooldown = new CommandsHandler().getCooldown(this.name);
        this.category = new Category(new CommandsHandler().getString(this.name, "category"));
        this.arguments = new CommandsHandler().getString(this.name, "arguments");
    }

	@Override
	protected void execute(CommandEvent event) {
        String[] commandArray = event.getMessage().getContentRaw().split(" ");
        if (!PermissionHandler.hasPermission(event.getMember(), Permission.MESSAGE_MANAGE)){
            event.reply("You can't use this command if you're not admin");
            return;
        }
        if(Integer.parseInt(commandArray[1]) > 99){
            event.reply("You can't delete more than 99 messages at once");
            return;
        }
        MessageHistory history = new MessageHistory(event.getChannel());
        List<Message> msgs = history.retrievePast(Integer.parseInt(commandArray[1])+ 1).complete();
        event.getTextChannel().deleteMessages(msgs).queue();
	}
}