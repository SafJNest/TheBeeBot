package com.safjnest.Commands.Misc;


import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import com.safjnest.Utilities.CommandsHandler;


/**
 * Gets a list of aliases for a command.
 * @author <a href="https://github.com/NeutronSun">NeutronSun</a>
 * 
 * @since 1.1.02
 */
public class Aliases extends Command {
    
    /**
     * Default constructor for the class.
     */
    public Aliases() {
        this.name = this.getClass().getSimpleName();
        this.aliases = new CommandsHandler().getArray(this.name, "alias");
        this.help = new CommandsHandler().getString(this.name, "help");
        this.cooldown = new CommandsHandler().getCooldown(this.name);
        this.category = new Category(new CommandsHandler().getString(this.name, "category"));
        this.arguments = new CommandsHandler().getString(this.name, "arguments");
    }

    /**
     * This method is called every time a member executes the command.
     */
    @Override
    protected void execute(CommandEvent event) {
        
        String aliases = "";
        for(Command e : event.getClient().getCommands()) {
            aliases+="**"+e.getName()+"**\n";
            for(String alias : e.getAliases())
                aliases += "- " + alias;
            aliases+="\n";
        }
        event.reply(aliases);

    }

}
