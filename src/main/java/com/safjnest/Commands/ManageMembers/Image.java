package com.safjnest.Commands.ManageMembers;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import com.safjnest.Utilities.CommandsLoader;

import net.dv8tion.jda.api.entities.User;

/**
 * @author <a href="https://github.com/Leon412">Leon412</a>
 * 
 * @since 1.1
 */
public class Image extends Command{
    public Image(){
        this.name = this.getClass().getSimpleName();
        this.aliases = new CommandsLoader().getArray(this.name, "alias");
        this.help = new CommandsLoader().getString(this.name, "help");
        this.cooldown = new CommandsLoader().getCooldown(this.name);
        this.category = new Category(new CommandsLoader().getString(this.name, "category"));
        this.arguments = new CommandsLoader().getString(this.name, "arguments");
    }

    @Override
    protected void execute(CommandEvent event) {
        User theGuy = null;
        try {
            if(event.getMessage().getMentions().getMembers().size() > 0)
                theGuy = event.getMessage().getMentions().getMembers().get(0).getUser();
            else
                theGuy = event.getJDA().retrieveUserById(event.getArgs()).complete();
            event.reply(theGuy.getAvatarUrl());
        } catch (Exception e) {
            event.reply("error: " + e.getMessage());
        }
    }
}