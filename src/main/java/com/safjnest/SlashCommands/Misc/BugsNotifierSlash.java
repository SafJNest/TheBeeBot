package com.safjnest.SlashCommands.Misc;

import java.awt.Color;
import java.util.Arrays;

import com.safjnest.Utilities.CommandsLoader;
import com.safjnest.Utilities.PermissionHandler;
import com.jagrosh.jdautilities.command.SlashCommand;
import com.jagrosh.jdautilities.command.SlashCommandEvent;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;

/**
 * This command let the user send a message to the {@link com.safjnest.Utilities.PermissionHandler#untouchables developers}
 * about a bug that occurs with a command.
 * @author <a href="https://github.com/NeutronSun">NeutronSun</a>
 * 
 * @since 1.1
 */
public class BugsNotifierSlash extends SlashCommand {
    /**
     * Default constructor for the class.
     */
    public BugsNotifierSlash(){
        this.name = this.getClass().getSimpleName().replace("Slash", "").toLowerCase();
        this.aliases = new CommandsLoader().getArray(this.name, "alias");
        this.help = new CommandsLoader().getString(this.name, "help");
        this.cooldown = new CommandsLoader().getCooldown(this.name);
        this.category = new Category(new CommandsLoader().getString(this.name, "category"));
        this.arguments = new CommandsLoader().getString(this.name, "arguments");
        this.options = Arrays.asList(
            new OptionData(OptionType.STRING, "command", "Name of the command you found a problem with", true),
            new OptionData(OptionType.STRING, "text", "Describe the bug", true));
    }
    /**
     * This method is called every time a member executes the command.
     */
	@Override
	protected void execute(SlashCommandEvent event) {
        
        
        EmbedBuilder eb = new EmbedBuilder();
        eb.setTitle("BUGS ALERT "+event.getOption("command").getAsString());
        eb.setAuthor(event.getUser().getName() + " from " + event.getGuild().getName());
        eb.setThumbnail(event.getUser().getAvatarUrl());
        eb.setDescription(event.getOption("text").getAsString());
        eb.setColor(new Color(255, 0, 0));

        PermissionHandler.getUntouchables().forEach((id) -> event.getJDA().retrieveUserById(id).complete().openPrivateChannel().queue((privateChannel) -> privateChannel.sendMessageEmbeds(eb.build()).queue()));
        event.deferReply(false).addContent("Message sent successfuly").queue();
    }
}