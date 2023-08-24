package com.safjnest.SlashCommands.ManageGuild;

import java.util.Arrays;
import java.util.List;

import com.safjnest.Utilities.CommandsLoader;
import com.safjnest.Utilities.PermissionHandler;
import com.jagrosh.jdautilities.command.SlashCommand;
import com.jagrosh.jdautilities.command.SlashCommandEvent;

import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageHistory;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;

/**
 * @author <a href="https://github.com/NeutronSun">NeutronSun</a>
 * @author <a href="https://github.com/Leon412">Leon412</a>
 * 
 * @since 1.0
 */
public class ClearSlash extends SlashCommand {

    public ClearSlash() {
        this.name = this.getClass().getSimpleName().replace("Slash", "").toLowerCase();
        this.aliases = new CommandsLoader().getArray(this.name, "alias");
        this.help = new CommandsLoader().getString(this.name, "help");
        this.cooldown = new CommandsLoader().getCooldown(this.name);
        this.category = new Category(new CommandsLoader().getString(this.name, "category"));
        this.arguments = new CommandsLoader().getString(this.name, "arguments");
        this.options = Arrays.asList(
            new OptionData(OptionType.INTEGER, "value", "number of messages to delete", false)
            .setMaxValue(99)
            .setMinValue(1));
    }

	@Override
	protected void execute(SlashCommandEvent event) {
        int value = event.getOption("value") == null ? 1 : event.getOption("value").getAsInt();
        
        if (!PermissionHandler.hasPermission(event.getMember(), Permission.MESSAGE_MANAGE)){
            event.reply("You can't use this command if you don't have the permission to delete messages");
            return;
        }
        
        MessageHistory history = new MessageHistory(event.getChannel());
        List<Message> msgs = history.retrievePast(value + 1).complete();
        event.getTextChannel().deleteMessages(msgs).queue();
        event.deferReply(false).addContent(value + " messages deleted").queue();
	}
}