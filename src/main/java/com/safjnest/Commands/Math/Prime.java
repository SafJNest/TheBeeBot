package com.safjnest.Commands.Math;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import com.safjnest.Utilities.CommandsLoader;
import com.safjnest.Utilities.SafJNest;
import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;

import net.dv8tion.jda.api.entities.channel.middleman.MessageChannel;
import net.dv8tion.jda.api.utils.FileUpload;

/**
 * @author <a href="https://github.com/NeutronSun">NeutronSun</a>
 * 
 * @since 1.1
 */
public class Prime extends Command {
    private int maxPrime;

    public Prime(int maxPrime){
        this.maxPrime = maxPrime;
        this.name = this.getClass().getSimpleName();
        this.aliases = new CommandsLoader().getArray(this.name, "alias");
        this.help = new CommandsLoader().getString(this.name, "help");
        this.cooldown = new CommandsLoader().getCooldown(this.name);
        this.category = new Category(new CommandsLoader().getString(this.name, "category"));
        this.arguments = new CommandsLoader().getString(this.name, "arguments");
    }

	@Override
	protected void execute(CommandEvent event) {
        String[] commandArray = event.getMessage().getContentRaw().split(" ");
        MessageChannel channel = event.getChannel();
        try {
            if (Integer.parseInt(commandArray[1]) > maxPrime)
                channel.sendMessage("The prime number cant be bigger than " + maxPrime + " bits").queue();
            else{
                String primi = SafJNest.getFirstPrime(SafJNest.randomBighi(Integer.parseInt(commandArray[1])));
                if (primi.length() > 2000) {
                    File supp = new File("primi.txt");
                    FileWriter app;
                    try {
                        app = new FileWriter(supp);
                        app.write(primi);
                        app.flush();
                        app.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    channel.sendMessage("Your prime number is too insane for discord, we need a file to hold it").queue();
                    channel.sendFiles(FileUpload.fromData(supp)).queue();
                } else {
                    channel.sendMessage(primi).queue();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            channel.sendMessage(e.getMessage()).queue();
        }
	}
}