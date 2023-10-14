package com.safjnest.SlashCommands.ManageGuild;

import java.util.Arrays;
import java.util.List;

import com.safjnest.Utilities.CommandsLoader;
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
        this.botPermissions = new Permission[]{Permission.MESSAGE_MANAGE};
        this.userPermissions = new Permission[]{Permission.MESSAGE_MANAGE};
        this.options = Arrays.asList(
            new OptionData(OptionType.INTEGER, "value", "Number of messages to delete (max 100)", true)
                .setMinValue(2)
                .setMaxValue(100)
        );    
    }

	@Override
	protected void execute(SlashCommandEvent event) {
        int value = event.getOption("value").getAsInt();
        
        MessageHistory history = new MessageHistory(event.getChannel());
        List<Message> msgs = history.retrievePast(value).complete();
        event.getTextChannel().deleteMessages(msgs).queue();

        event.deferReply(true).addContent(value + " messages deleted.").queue();
	}
}