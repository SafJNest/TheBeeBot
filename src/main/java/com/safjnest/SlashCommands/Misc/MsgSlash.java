package com.safjnest.SlashCommands.Misc;

import java.awt.Color;
import java.util.Arrays;

import com.jagrosh.jdautilities.command.SlashCommand;
import com.jagrosh.jdautilities.command.SlashCommandEvent;
import com.safjnest.Utilities.CommandsLoader;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;

/**
 * @author <a href="https://github.com/Leon412">Leon412</a>
 * 
 * @since 1.1
 */
public class MsgSlash extends SlashCommand {

    public MsgSlash(){
        this.name = this.getClass().getSimpleName().replace("Slash", "").toLowerCase();
        this.aliases = new CommandsLoader().getArray(this.name, "alias");
        this.help = new CommandsLoader().getString(this.name, "help");
        this.cooldown = new CommandsLoader().getCooldown(this.name);
        this.category = new Category(new CommandsLoader().getString(this.name, "category"));
        this.arguments = new CommandsLoader().getString(this.name, "arguments");
        this.options = Arrays.asList(
            new OptionData(OptionType.USER, "user", "The user you want to send the message", true),
            new OptionData(OptionType.STRING, "msg", "The message you want to send", true));
    }

	@Override
	protected void execute(SlashCommandEvent event) {
        User theGuy = event.getOption("user").getAsUser();
        EmbedBuilder eb = new EmbedBuilder();
        eb.setTitle("NEW MESSAGE FROM " + event.getMember().getEffectiveName());
        eb.setThumbnail(event.getUser().getAvatarUrl());
        eb.setDescription(event.getOption("msg").getAsString());
        eb.setColor(new Color(3, 252, 169));
        theGuy.openPrivateChannel().queue((privateChannel) -> privateChannel.sendMessageEmbeds(eb.build()).queue());
        event.deferReply(false).addContent("Message sent successfuly").queue();
	}
}