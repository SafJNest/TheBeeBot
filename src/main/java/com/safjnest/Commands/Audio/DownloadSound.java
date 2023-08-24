package com.safjnest.Commands.Audio;

import java.io.File;
import java.util.ArrayList;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import com.safjnest.Utilities.CommandsLoader;
import com.safjnest.Utilities.SQL;



public class DownloadSound extends Command{
    String path = "rsc" + File.separator + "SoundBoard" + File.separator;
    SQL sql;

    public DownloadSound(SQL sql){
        this.name = this.getClass().getSimpleName();
        this.aliases = new CommandsLoader().getArray(this.name, "alias");
        this.help = new CommandsLoader().getString(this.name, "help");
        this.cooldown = new CommandsLoader().getCooldown(this.name);
        this.category = new Category(new CommandsLoader().getString(this.name, "category"));
        this.arguments = new CommandsLoader().getString(this.name, "arguments");
        this.sql = sql;
    }

    @Override
    protected void execute(CommandEvent event) {
        String fileName = event.getArgs();
        if(fileName.isEmpty()){
            event.reply("Missing sound's name.");
            return;
        }

        String query = null;
        String id = null, extension;
        ArrayList<ArrayList<String>> arr = null;

        if(fileName.matches("[0123456789]*")){
            query = "SELECT id, guild_id, extension FROM sound WHERE id = '" + fileName + "';";
        }
        else{
            query = "SELECT id, guild_id, extension FROM sound WHERE name = '" + fileName + "';";
        }

        if((arr = sql.getAllRows(query, 3)).isEmpty()){
            event.reply("There is no sound with that name/id");
            return;
        }
        int indexForKeria = -1;
        for(int i = 0; i < arr.size(); i++){
            if(arr.get(i).get(1).equals(event.getGuild().getId())){
               indexForKeria = i;
               break;
            }
        }
        
        if(indexForKeria == -1){
            indexForKeria = (int)(Math.random()*arr.size());
            event.reply("I couldn't find this sound on your guild so i downloaded a random sound with this name");
        }
        else{
            event.reply("I'm downloading the file with this name from your guild");
        }

        id = arr.get(indexForKeria).get(0);
        extension = arr.get(indexForKeria).get(2);
    
        fileName = id + "." + extension;

        File toSend = new File(path + fileName);
        event.reply(toSend, fileName);

    }
}