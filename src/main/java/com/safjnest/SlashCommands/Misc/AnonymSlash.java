package com.safjnest.SlashCommands.Misc;

import java.awt.Color;
import java.io.File;
import java.util.Arrays;

import com.jagrosh.jdautilities.command.SlashCommand;
import com.jagrosh.jdautilities.command.SlashCommandEvent;
import com.safjnest.Utilities.CommandsLoader;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.utils.FileUpload;

/**
 * @author <a href="https://github.com/NeuntronSun">NeutronSun</a>
 * 
 * @since 1.3
 */
public class AnonymSlash extends SlashCommand {

    public AnonymSlash(){
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
        eb.setTitle("NEW ANONYMUS MESSAGE");
        String img = "punto.jpg";
        File file = new File("rsc" + File.separator + "img" + File.separator+ img);
        eb.setThumbnail("attachment://" + img);
        eb.setDescription(event.getOption("msg").getAsString());
        eb.setColor(new Color(3, 252, 169));
        theGuy.openPrivateChannel().queue((privateChannel) -> privateChannel.sendMessageEmbeds(
            eb.build())
            .addFiles(FileUpload.fromData(file))
            .queue());
        event.deferReply(false).addContent("Message sent successfuly").queue();
	}
}