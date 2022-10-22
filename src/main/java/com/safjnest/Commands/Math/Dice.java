package com.safjnest.Commands.Math;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import com.safjnest.Utilities.CommandsHandler;

/**
 * @author <a href="https://github.com/NeutronSun">NeutronSun</a>
 * @author <a href="https://github.com/Leon412">Leon412</a>
 * 
 * @since 1.0
 */
public class Dice extends Command {

    public Dice(){
        this.name = this.getClass().getSimpleName();
        this.aliases = new CommandsHandler().getArray(this.name, "alias");
        this.help = new CommandsHandler().getString(this.name, "help");
        this.cooldown = new CommandsHandler().getCooldown(this.name);
        this.category = new Category(new CommandsHandler().getString(this.name, "category"));
        this.arguments = new CommandsHandler().getString(this.name, "arguments");
    }

	@Override
	protected void execute(CommandEvent event) {
        int ndice = 1, nface = 6, sum = 0;
        if(!event.getArgs().equals("")){
            ndice = Integer.parseInt(event.getArgs().split(" ")[0]);
            if(event.getArgs().split(" ").length > 1 && !event.getArgs().split(" ")[1].equals(""))
                nface = Integer.parseInt(event.getArgs().split(" ")[1]);
        }
        for(int i = 0; i < ndice; i++)
            sum+=(int)(Math.random() * nface) + 1;
        
        event.reply((ndice == 1)
                    ? "Rolled a " + nface + "-sided dice: " + sum        
                    : "Rolled " + ndice +" "+nface + "-sided dices: " + sum);
	}
}