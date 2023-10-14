package com.safjnest.SlashCommands.Misc;



import com.jagrosh.jdautilities.command.SlashCommand;
import com.jagrosh.jdautilities.command.SlashCommandEvent;
import com.safjnest.Utilities.CommandsLoader;

/**
 * The commands shows the ping of the bot.
 * <p>The bot sends a message, once the message is received, the bot sends a message back, and the ping is calculated.</p>
 * @author <a href="https://github.com/Leon412">Leon412</a>
 * 
 * @since 1.0
 */

public class PingSlash extends SlashCommand{
    /**
     * Default constructor for the class.
     */
    public PingSlash(){
        this.name = this.getClass().getSimpleName().replace("Slash", "").toLowerCase();
        this.aliases = new CommandsLoader().getArray(this.name, "alias");
        this.help = new CommandsLoader().getString(this.name, "help");
        this.cooldown = new CommandsLoader().getCooldown(this.name);
        this.category = new Category(new CommandsLoader().getString(this.name, "category"));
        this.arguments = new CommandsLoader().getString(this.name, "arguments");
        this.hidden = true;
    }
    /**
     * This method is called every time a member executes the command.
     */
    @Override
    public void execute(SlashCommandEvent event) {
        
        long time = System.currentTimeMillis();
        event.deferReply(false).queue(
            hook -> hook.editOriginalFormat("Pong: %d ms ", System.currentTimeMillis() - time).queue()
        );
    }

   
    
}


