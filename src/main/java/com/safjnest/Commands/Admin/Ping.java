package com.safjnest.Commands.Admin;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import com.safjnest.Utilities.Commands.CommandsHandler;

/**
 * The commands shows the ping of the bot.
 * <p>The bot sends a message, once the message is received, the bot sends a message back, and the ping is calculated.</p>
 * @author <a href="https://github.com/Leon412">Leon412</a>
 * 
 * @since 1.0
 */

public class Ping extends Command {

    /**
     * Default constructor for the class.
     */
    public Ping(){
        this.name = this.getClass().getSimpleName();
        this.aliases = new CommandsHandler().getArray(this.name, "alias");
        this.help = new CommandsHandler().getString(this.name, "help");
        this.cooldown = new CommandsHandler().getCooldown(this.name);
        this.category = new Category(new CommandsHandler().getString(this.name, "category"));
        this.arguments = new CommandsHandler().getString(this.name, "arguments");
        this.hidden = true;
    }
    /**
     * This method is called every time a member executes the command.
     */
    @Override
    protected void execute(CommandEvent e) {
        long time = System.currentTimeMillis();
        e.reply("Pong!", response -> {
            response.editMessageFormat("Pong: %d ms", System.currentTimeMillis() - time).queue();
        });
    }
   
}