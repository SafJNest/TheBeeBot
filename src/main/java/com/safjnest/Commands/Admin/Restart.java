package com.safjnest.Commands.Admin;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import com.safjnest.App;
import com.safjnest.Utilities.PermissionHandler;
import com.safjnest.Utilities.Commands.CommandsHandler;

import java.io.File;
import java.io.FileReader;
import java.io.Reader;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

public class Restart extends Command{
    /**
     * Default constructor for the class.
     */
    public Restart(){
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
        String bot = e.getArgs();
        JSONParser parser = new JSONParser();
        JSONObject settings = null;

        if(!PermissionHandler.isUntouchable(e.getAuthor().getId())){
            e.reply("Swear to god next time you dare to try this again I'll ban you from discord");
            return;
        }

        if(bot.equals("")){
            e.reply("Please specify a bot to restart.");
            return;
        }

        try (Reader reader = new FileReader("rsc" + File.separator + "settings.json")) {
            settings = (JSONObject) parser.parse(reader);
            if(settings.get(bot) == null)
                throw new Exception("sei un mongoloide");
        } catch (Exception ex) {
            e.reply(e.getAuthor().getAsMention() + " bro its your bot, how can you not know the name?");
            return;
        }


        App.restart(bot);
        e.reply("Shutting down " + bot);
    }
}