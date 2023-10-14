package com.safjnest.Commands.Owner;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import com.safjnest.App;
import com.safjnest.Utilities.CommandsLoader;

import java.io.File;
import java.io.FileReader;
import java.io.Reader;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

/**
 * @author <a href="https://github.com/NeutronSun">NeutronSun</a>
 * 
 * @since 1.1
 */
public class Restart extends Command{

    public Restart(){
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
        String bot = e.getArgs();
        JSONParser parser = new JSONParser();
        JSONObject settings = null;

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