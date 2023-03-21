package com.safjnest.SlashCommands.Misc;

import org.apache.commons.lang3.ThreadUtils;

import com.jagrosh.jdautilities.command.SlashCommand;
import com.jagrosh.jdautilities.command.SlashCommandEvent;
import com.safjnest.Utilities.CommandsHandler;
/**
 * @author <a href="https://github.com/NeutronSun">NeutronSun</a>
 * 
 * @since 1.3
 */
public class ThreadCounterSlash extends SlashCommand{
    /**
     * Default constructor for the class.
     */
    public ThreadCounterSlash(){
        this.name = this.getClass().getSimpleName().replace("Slash", "").toLowerCase();
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
    protected void execute(SlashCommandEvent e) {
        double cont = 0.0;
        for (Thread t : ThreadUtils.getAllThreads()) {
            if(t.getName().startsWith("lava"))
                cont++;
        }
        e.deferReply(false).addContent("Active LavaPlayer's Threads: " + cont +"\n").queue();
    }
}