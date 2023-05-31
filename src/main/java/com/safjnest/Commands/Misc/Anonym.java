package com.safjnest.Commands.Misc;

import java.awt.Color;
import java.io.File;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import com.safjnest.Utilities.Commands.CommandsHandler;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.utils.FileUpload;

/**
 * @author <a href="https://github.com/NeuntronSun">NeutronSun</a>
 * 
 * @since 1.3
 */
public class Anonym extends Command {

    public Anonym(){
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
        eb.setTitle("NEW ANONYMUS MESSAGE");
        String img = "punto.jpg";
        File file = new File("rsc" + File.separator + "img" + File.separator+ img);
        eb.setThumbnail("attachment://" + img);
        eb.setDescription(command[1]);
        eb.setColor(new Color(3, 252, 169));
        theGuy.openPrivateChannel().queue((privateChannel) -> privateChannel.sendMessageEmbeds(
            eb.build())
            .addFiles(FileUpload.fromData(file))
            .queue());
	}
}