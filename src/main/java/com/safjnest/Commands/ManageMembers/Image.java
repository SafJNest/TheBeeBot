package com.safjnest.Commands.ManageMembers;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import com.safjnest.Utilities.CommandsLoader;
import com.safjnest.Utilities.PermissionHandler;

import net.dv8tion.jda.api.entities.User;

/**
 * @author <a href="https://github.com/Leon412">Leon412</a>
 * 
 * @since 1.1
 */
public class Image extends Command{
    public Image(){
        this.name = this.getClass().getSimpleName().toLowerCase();
        this.aliases = new CommandsLoader().getArray(this.name, "alias");
        this.help = new CommandsLoader().getString(this.name, "help");
        this.cooldown = new CommandsLoader().getCooldown(this.name);
        this.category = new Category(new CommandsLoader().getString(this.name, "category"));
        this.arguments = new CommandsLoader().getString(this.name, "arguments");
    }

    @Override
    protected void execute(CommandEvent event) {
        try {
            User mentionedUser;
            if(event.getArgs().equals(""))
                mentionedUser = event.getAuthor();
            else
                mentionedUser = PermissionHandler.getMentionedUser(event, event.getArgs());

            if(mentionedUser == null)
                event.reply("Couldn't find the specified member, please mention or write the id of a member.");
            else
                event.reply(mentionedUser.getAvatarUrl() + "?size=4096&quality=lossless");
        } catch (Exception e) {
            event.reply("Error: " + e.getMessage());
        }
    }
}