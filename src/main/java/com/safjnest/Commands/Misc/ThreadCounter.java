package com.safjnest.Commands.Misc;

import org.apache.commons.lang3.ThreadUtils;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import com.safjnest.Utilities.CommandsHandler;
/**
 * @author <a href="https://github.com/NeutronSun">NeutronSun</a>
 * 
 * @since 1.3
 */
public class ThreadCounter extends Command{
    /**
     * Default constructor for the class.
     */
    public ThreadCounter(){
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
        double cont = 0.0;
        for (Thread t : ThreadUtils.getAllThreads()) {
            if(t.getName().startsWith("lava"))
                cont++;
        }
        e.reply("Active LavaPlayer's Threads: " + cont +"\n" +e.getPrefix()+"p executed: " + cont/3);
    }
}