package com.safjnest.Commands.Owner;

import org.apache.commons.lang3.ThreadUtils;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import com.safjnest.Utilities.CommandsLoader;
/**
 * @author <a href="https://github.com/NeutronSun">NeutronSun</a>
 * 
 * @since 1.3
 */
public class ThreadCounter extends Command{

    public ThreadCounter(){
        this.name = this.getClass().getSimpleName();
        this.aliases = new CommandsLoader().getArray(this.name, "alias");
        this.help = new CommandsLoader().getString(this.name, "help");
        this.cooldown = new CommandsLoader().getCooldown(this.name);
        this.category = new Category(new CommandsLoader().getString(this.name, "category"));
        this.arguments = new CommandsLoader().getString(this.name, "arguments");
        this.ownerCommand = true;
        this.hidden = true;
    }

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